"""Temporary KC-05 GitHub Actions runner. Embedded into one ops workflow only."""
import json
import os
from datetime import datetime, timezone
from pathlib import Path
import re
import subprocess
import sys
import time

PROJECT = "visana-erp-dev"
REGION = "us-central1"
SERVICE = "visana-keycloak-dev"
SOURCE = "8701fc5b377af72665dce210203e43c0433a1fbd"
RUNTIME = "visana-keycloak-dev@visana-erp-dev.iam.gserviceaccount.com"
DEPLOYER = "github-actions-dev@visana-erp-dev.iam.gserviceaccount.com"
HOSTNAME = "https://visana-keycloak-dev-984938781030.us-central1.run.app"
IMAGE = ("us-central1-docker.pkg.dev/visana-erp-dev/visana-repo/"
         "visana-keycloak-dev@sha256:00462375f745a2f05afbc22b337fab23ae7a757846581edbab8f9afc14819b95")
PROXY = ("gcr.io/cloud-sql-connectors/cloud-sql-proxy:2.25.4@"
         "sha256:88501f0a695a586988add1b8a206fdf3f29f9a1a3deeb9b45ef2b1481ea6be83")
SECRETS = {
    "KC_DB_PASSWORD": "visana-dev-keycloak-db-password",
    "KC_BOOTSTRAP_ADMIN_PASSWORD": "visana-dev-keycloak-bootstrap-password",
    "VISANA_KEYCLOAK_HEALTH_CLIENT_SECRET": "visana-dev-keycloak-health-client-secret",
}
REPORT = Path(os.environ["RUNNER_TEMP"]) / "kc05-deploy-result.json"
result = {
    "KC05_STATUS": "NOT_STARTED", "OWNER_VERIFIED_FOUNDATION": "YES",
    "SOURCE_SHA": SOURCE, "RUN_ID": os.environ.get("GITHUB_RUN_ID"),
    "SERVICE_NAME": SERVICE, "SERVICE_EXISTS": "UNKNOWN", "REVISION": None,
    "LATEST_CREATED_REVISION": None, "LATEST_READY_REVISION": None,
    "REVISION_READY": "UNKNOWN", "EXPECTED_HOSTNAME": HOSTNAME,
    "ACTUAL_SERVICE_URL": None, "HOSTNAME_MATCH": "UNKNOWN",
    "IMAGE_DIGEST_MATCH": "UNKNOWN", "RUNTIME_SA_MATCH": "UNKNOWN",
    "SECRET_REFS_MATCH": "UNKNOWN", "PROXY_READY": "UNKNOWN",
    "DATABASE_CONNECTED": "UNKNOWN", "KEYCLOAK_SCHEMA_INITIALIZED": "UNKNOWN",
    "KEYCLOAK_STARTUP_PROBE": "UNKNOWN", "KEYCLOAK_READINESS_PROBE": "UNKNOWN",
    "KEYCLOAK_LIVENESS_PROBE": "UNKNOWN", "PROXY_STARTUP_PROBE": "UNKNOWN",
    "PROXY_LIVENESS_PROBE": "UNKNOWN", "PUBLIC_INVOKER": "UNKNOWN",
    "PRIVATE_SERVICE": "UNKNOWN",
    "SCALING_MIN": "UNKNOWN", "SCALING_MAX": "UNKNOWN",
    "METADATA_VISIBILITY_LIMITED": "NO", "LIMITED_READS": [],
    "DEPLOY_ATTEMPTED": "NO", "DEPLOY_COUNT": 0,
    "KC06_READY_FOR_AUTHORIZATION": "NO",
    "PHASE2_GATE_IDP": "FAIL", "ALL_GATES_PASS": "NO", "KC06_AUTHORIZED": "NO",
    "FAILED_STAGE": None, "FAIL_CODE": None,
}
stage = "INIT"


class GateFailure(Exception):
    pass


def save():
    REPORT.write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")


def require(ok, code):
    if not ok:
        raise GateFailure(code)


def call(args, timeout=180):
    return subprocess.run(args, capture_output=True, text=True, timeout=timeout, check=False)


def error_kind(process):
    message = process.stderr.lower()
    if any(x in message for x in ("permission denied", "permission_denied", "403",
                                  "does not have permission", "not authorized")):
        return "PERMISSION"
    if any(x in message for x in ("not found", "not_found", "404",
                                  "does not exist", "could not be found")):
        return "NOT_FOUND"
    return "OTHER"


def sanitized_error(process):
    message = process.stderr or process.stdout
    line = next((part for part in message.splitlines() if "ERROR:" in part),
                message.splitlines()[0] if message.splitlines() else "")
    line = line.split(" This command is authenticated as ")[0]
    line = re.sub(r"(?i)(password|token|credential|private_key)\s*[:=]\s*\S+",
                  r"\1=[REDACTED]", line)
    line = re.sub(r"[A-Za-z0-9+/=]{100,}", "[REDACTED_LONG_VALUE]", line)
    return line[:700]


def metadata(args, label, *, absent_ok=False):
    process = call(args + ["--format=json"])
    if process.returncode == 0:
        try:
            return json.loads(process.stdout)
        except ValueError as exc:
            raise GateFailure(label + "_INVALID_JSON") from exc
    kind = error_kind(process)
    if kind == "PERMISSION":
        result["METADATA_VISIBILITY_LIMITED"] = "YES"
        result["LIMITED_READS"].append(label)
        return "PERMISSION_LIMITED"
    if kind == "NOT_FOUND" and absent_ok:
        return None
    raise GateFailure(label + ("_NOT_FOUND" if kind == "NOT_FOUND" else "_READ_FAILED"))


def service_metadata():
    return metadata(["gcloud", "run", "services", "describe", SERVICE,
                     "--project=" + PROJECT, "--region=" + REGION],
                    "CLOUD_RUN_SERVICE", absent_ok=True)


def service_preflight():
    listing = metadata(["gcloud", "run", "services", "list",
                        "--project=" + PROJECT, "--region=" + REGION],
                       "CLOUD_RUN_LIST")
    if listing == "PERMISSION_LIMITED":
        return "PERMISSION_LIMITED"
    require(isinstance(listing, list), "CLOUD_RUN_LIST_INVALID")
    return any(item.get("metadata", {}).get("name") == SERVICE
               or item.get("name", "").endswith("/services/" + SERVICE)
               for item in listing)


def pr_guard():
    process = call(["gh", "api", "repos/Visanaz/visana/pulls/20"])
    require(process.returncode == 0, "PR_RECHECK_FAILED")
    pr = json.loads(process.stdout)
    require(pr.get("state") == "open" and pr.get("draft") is True
            and pr.get("merged_at") is None and pr["base"]["ref"] == "dev"
            and pr["head"]["sha"] == SOURCE, "PR_CHANGED")


def render():
    target = Path(os.environ["RUNNER_TEMP"]) / "kc05-service.json"
    process = call([sys.executable, "approved-source/scripts/ci/keycloak_dev.py",
                    "render", "--hostname", HOSTNAME, "--image", IMAGE,
                    "--output", str(target)])
    require(process.returncode == 0, "RENDER_FAILED")
    raw = target.read_text(encoding="utf-8")
    require(chr(36) + "{" not in raw, "RENDER_PLACEHOLDERS")
    data = json.loads(raw)
    spec = data["spec"]["template"]["spec"]
    containers = spec["containers"]
    require(data["metadata"]["name"] == SERVICE
            and data["metadata"]["namespace"] == PROJECT
            and data["metadata"]["labels"]["cloud.googleapis.com/location"] == REGION,
            "RENDER_TARGET_MISMATCH")
    require(spec["serviceAccountName"] == RUNTIME
            and len(containers) == 2
            and [c["name"] for c in containers] == ["keycloak", "cloud-sql-proxy"]
            and containers[0]["image"] == IMAGE and containers[1]["image"] == PROXY,
            "RENDER_CONTAINERS_MISMATCH")
    env = {item["name"]: item for item in containers[0]["env"]}
    require(all(env[name].get("valueFrom", {}).get("secretKeyRef") ==
                {"name": secret, "key": "1"} for name, secret in SECRETS.items()),
            "RENDER_SECRET_REFS_MISMATCH")
    require(env["KC_HOSTNAME"].get("value") == HOSTNAME
            and env["KC_DB_URL"].get("value") ==
            "jdbc:postgresql://127.0.0.1:5432/keycloak_dev"
            and env["KC_DB_USERNAME"].get("value") == "keycloak_app_dev"
            and env["KC_BOOTSTRAP_ADMIN_USERNAME"].get("value") == "visana-dev-bootstrap",
            "RENDER_DB_OR_HOST_MISMATCH")
    annotation = data["metadata"]["annotations"]
    require(annotation["run.googleapis.com/minScale"] == "1"
            and annotation["run.googleapis.com/maxScale"] == "1"
            and spec["containerConcurrency"] == 20, "RENDER_SCALING_MISMATCH")
    require(data["spec"]["template"]["metadata"]["annotations"]
            ["run.googleapis.com/execution-environment"] == "gen2",
            "RENDER_GENERATION_MISMATCH")
    require(containers[0]["ports"][0]["containerPort"] == 8080
            and containers[0]["resources"]["limits"] == {"cpu": "2", "memory": "2Gi"}
            and containers[1]["resources"]["limits"] == {"cpu": "1", "memory": "512Mi"},
            "RENDER_RESOURCES_MISMATCH")
    expected_probes = ((containers[0], "startupProbe", "/health/ready", 9000),
                       (containers[0], "readinessProbe", "/health/ready", 9000),
                       (containers[0], "livenessProbe", "/health/live", 9000),
                       (containers[1], "startupProbe", "/startup", 9090),
                       (containers[1], "livenessProbe", "/liveness", 9090))
    require(all(container.get(name, {}).get("httpGet") == {"path": path, "port": port}
                for container, name, path, port in expected_probes),
            "RENDER_PROBES_MISMATCH")
    require(data["spec"]["traffic"] == [{"latestRevision": True, "percent": 100}]
            and spec["timeoutSeconds"] == 300, "RENDER_TRAFFIC_OR_TIMEOUT_MISMATCH")
    require("--address=127.0.0.1" in containers[1]["args"]
            and "--port=5432" in containers[1]["args"]
            and containers[1]["args"][-1] == "visana-erp-dev:us-central1:visana-db-dev",
            "RENDER_PROXY_MISMATCH")
    return target


def inspect_service(data):
    result["SERVICE_EXISTS"] = "YES"
    status = data.get("status", {})
    result["LATEST_CREATED_REVISION"] = status.get("latestCreatedRevisionName")
    result["LATEST_READY_REVISION"] = status.get("latestReadyRevisionName")
    result["REVISION"] = (status.get("latestReadyRevisionName")
                          or status.get("latestCreatedRevisionName"))
    result["ACTUAL_SERVICE_URL"] = status.get("url")
    result["HOSTNAME_MATCH"] = "YES" if status.get("url") == HOSTNAME else "NO"
    conditions = status.get("conditions", [])
    ready = any(c.get("type") == "Ready" and c.get("status") == "True"
                for c in conditions)
    terminal_fail = any(c.get("type") == "Ready" and c.get("status") == "False"
                        for c in conditions)
    result["REVISION_READY"] = "YES" if ready else ("NO" if terminal_fail else "PENDING")
    spec = data.get("spec", {}).get("template", {}).get("spec", {})
    containers = spec.get("containers", [])
    result["RUNTIME_SA_MATCH"] = "YES" if spec.get("serviceAccountName") == RUNTIME else "NO"
    result["IMAGE_DIGEST_MATCH"] = (
        "YES" if len(containers) == 2 and containers[0].get("image") == IMAGE
        and containers[1].get("image") == PROXY else "NO")
    env = {item.get("name"): item.get("valueFrom", {}).get("secretKeyRef")
           for item in containers[0].get("env", [])} if containers else {}
    result["SECRET_REFS_MATCH"] = (
        "YES" if all(env.get(name) == {"name": secret, "key": "1"}
                     for name, secret in SECRETS.items()) else "NO")
    plain_env = {item.get("name"): item.get("value")
                 for item in containers[0].get("env", [])} if containers else {}
    result["DB_ENV_MATCH"] = (
        "YES" if plain_env.get("KC_DB_URL") ==
        "jdbc:postgresql://127.0.0.1:5432/keycloak_dev"
        and plain_env.get("KC_DB_USERNAME") == "keycloak_app_dev"
        and plain_env.get("KC_BOOTSTRAP_ADMIN_USERNAME") == "visana-dev-bootstrap"
        and plain_env.get("KC_HOSTNAME") == HOSTNAME else "NO")
    annotations = data.get("metadata", {}).get("annotations", {})
    template_annotations = (data.get("spec", {}).get("template", {})
                            .get("metadata", {}).get("annotations", {}))
    result["GENERATION_MATCH"] = (
        "YES" if template_annotations.get("run.googleapis.com/execution-environment")
        == "gen2" else "NO")
    result["SCALING_MIN"] = annotations.get("run.googleapis.com/minScale", "UNKNOWN")
    result["SCALING_MAX"] = annotations.get("run.googleapis.com/maxScale", "UNKNOWN")
    result["CONCURRENCY"] = spec.get("containerConcurrency")
    result["TIMEOUT_SECONDS"] = spec.get("timeoutSeconds")
    if len(containers) == 2:
        keycloak, proxy = containers
        for label, container, probe, path, port in (
            ("KEYCLOAK_STARTUP_PROBE", keycloak, "startupProbe", "/health/ready", 9000),
            ("KEYCLOAK_READINESS_PROBE", keycloak, "readinessProbe", "/health/ready", 9000),
            ("KEYCLOAK_LIVENESS_PROBE", keycloak, "livenessProbe", "/health/live", 9000),
            ("PROXY_STARTUP_PROBE", proxy, "startupProbe", "/startup", 9090),
            ("PROXY_LIVENESS_PROBE", proxy, "livenessProbe", "/liveness", 9090),
        ):
            probe_match = container.get(probe, {}).get("httpGet") == {"path": path, "port": port}
            result[label] = ("CONFIGURED_REVISION_READY" if ready else "CONFIGURED") \
                if probe_match else "MISMATCH"
        result["PROXY_READY"] = (
            "YES" if ready and result["PROXY_STARTUP_PROBE"] == "CONFIGURED_REVISION_READY"
            and result["PROXY_LIVENESS_PROBE"] == "CONFIGURED_REVISION_READY"
            else "UNKNOWN")
        result["RESOURCES_MATCH"] = (
            "YES" if keycloak.get("ports", [{}])[0].get("containerPort") == 8080
            and keycloak.get("resources", {}).get("limits") == {"cpu": "2", "memory": "2Gi"}
            and proxy.get("resources", {}).get("limits") == {"cpu": "1", "memory": "512Mi"}
            else "NO")
        result["PROXY_TARGET_MATCH"] = (
            "YES" if "--address=127.0.0.1" in proxy.get("args", [])
            and "--port=5432" in proxy.get("args", [])
            and proxy.get("args", [])[-1:] ==
            ["visana-erp-dev:us-central1:visana-db-dev"] else "NO")
    traffic = data.get("spec", {}).get("traffic", [])
    result["TRAFFIC_100_LATEST"] = (
        "YES" if len(traffic) == 1 and traffic[0].get("percent") == 100
        and traffic[0].get("latestRevision") is True else "NO")
    return ready, terminal_fail


try:
    save()
    stage = "SECURITY_EXCEPTION"
    expires = datetime(2026, 10, 15, 21, 33, tzinfo=timezone.utc)
    require(datetime.now(timezone.utc) < expires, "SECURITY_EXCEPTION_EXPIRED")
    exception = Path("approved-source/docs/00_governance/"
                     "SECURITY_EXCEPTION_KEYCLOAK_PCRE2_DEV_20261001.md")
    text = exception.read_text(encoding="utf-8")
    require(all(marker in text for marker in (
        "VISANA-SEC-EXC-KC-PCRE2-20261001",
        "APPROVED_TEMPORARY_DEV_ONLY", "26.8.0",
        "2026-10-15 16:33 America/Bogota",
        "CVE-2026-103111", "CVE-2026-86145", "CVE-2026-89161")),
        "SECURITY_EXCEPTION_SOURCE_MISMATCH")
    result["SECURITY_EXCEPTION_ACTIVE"] = "YES"
    stage = "GCP_IDENTITY"
    project = call(["gcloud", "config", "get-value", "project"])
    identity = call(["gcloud", "auth", "list", "--filter=status:ACTIVE",
                     "--format=value(account)"])
    require(project.returncode == 0 and project.stdout.strip() == PROJECT,
            "PROJECT_MISMATCH")
    require(identity.returncode == 0 and identity.stdout.strip() == DEPLOYER,
            "DEPLOYER_MISMATCH")
    result["ACTIVE_GCP_IDENTITY"] = DEPLOYER

    stage = "METADATA_PREFLIGHT"
    account = metadata(["gcloud", "iam", "service-accounts", "describe", RUNTIME,
                        "--project=" + PROJECT], "RUNTIME_SA")
    if account != "PERMISSION_LIMITED":
        require(account.get("email") == RUNTIME and not account.get("disabled", False),
                "RUNTIME_SA_CONTRADICTION")
    image = metadata(["gcloud", "artifacts", "docker", "images", "describe", IMAGE,
                      "--project=" + PROJECT], "IMAGE")
    if image != "PERMISSION_LIMITED":
        require(isinstance(image, dict), "IMAGE_METADATA_INVALID")
    for name in SECRETS.values():
        secret = metadata(["gcloud", "secrets", "describe", name,
                           "--project=" + PROJECT], "SECRET_" + name)
        if secret != "PERMISSION_LIMITED":
            require(secret.get("name", "").endswith("/secrets/" + name),
                    "SECRET_METADATA_CONTRADICTION")
        version = metadata(["gcloud", "secrets", "versions", "describe", "1",
                            "--secret=" + name, "--project=" + PROJECT],
                           "SECRET_VERSION_" + name)
        if version != "PERMISSION_LIMITED":
            require(version.get("state") == "ENABLED", "SECRET_VERSION_NOT_ENABLED")
    existing = service_preflight()
    if existing is True:
        prior = service_metadata()
        if isinstance(prior, dict):
            inspect_service(prior)
        raise GateFailure("SERVICE_ALREADY_EXISTS")
    require(existing is False, "SERVICE_ALREADY_EXISTS_OR_NOT_VERIFIABLE")
    result["SERVICE_EXISTS"] = "NO_BY_METADATA"

    stage = "RENDER"
    manifest = render()
    result["RENDER_STATUS"] = "PASS"
    save()
    stage = "LAST_PR_RECHECK"
    pr_guard()
    stage = "DEPLOY"
    result["DEPLOY_ATTEMPTED"] = "YES"
    result["DEPLOY_COUNT"] = 1
    save()
    deployed = call(["gcloud", "run", "services", "replace", str(manifest),
                     "--region=" + REGION, "--project=" + PROJECT,
                     "--quiet"], timeout=1200)
    if deployed.returncode:
        result["DEPLOY_ERROR_SANITIZED"] = sanitized_error(deployed)
        save()
    require(deployed.returncode == 0, "DEPLOY_COMMAND_FAILED")

    stage = "REVISION_WAIT"
    deadline = time.monotonic() + 900
    while True:
        service = service_metadata()
        require(isinstance(service, dict), "SERVICE_POSTCHECK_UNAVAILABLE")
        ready, terminal_fail = inspect_service(service)
        save()
        if ready or terminal_fail:
            break
        require(time.monotonic() < deadline, "REVISION_TIMEOUT")
        time.sleep(10)
    require(ready, "REVISION_NOT_READY")
    stage = "SERVICE_POSTCHECK"
    require(result["HOSTNAME_MATCH"] == "YES", "HOSTNAME_MISMATCH")
    require(result["IMAGE_DIGEST_MATCH"] == "YES"
            and result["RUNTIME_SA_MATCH"] == "YES"
            and result["SECRET_REFS_MATCH"] == "YES"
            and result["DB_ENV_MATCH"] == "YES"
            and result["PROXY_TARGET_MATCH"] == "YES",
            "SERVICE_CONFIG_MISMATCH")
    require(result["SCALING_MIN"] == "1" and result["SCALING_MAX"] == "1"
            and result["TRAFFIC_100_LATEST"] == "YES"
            and result["GENERATION_MATCH"] == "YES"
            and result["RESOURCES_MATCH"] == "YES"
            and result["CONCURRENCY"] == 20
            and result["TIMEOUT_SECONDS"] == 300,
            "SERVICE_SCALING_OR_TRAFFIC_MISMATCH")
    policy = metadata(["gcloud", "run", "services", "get-iam-policy", SERVICE,
                       "--region=" + REGION, "--project=" + PROJECT],
                      "SERVICE_IAM")
    if policy != "PERMISSION_LIMITED":
        public = any(binding.get("role") == "roles/run.invoker" and
                     any(member in ("allUsers", "allAuthenticatedUsers")
                         for member in binding.get("members", []))
                     for binding in policy.get("bindings", []))
        public = public or (service.get("metadata", {}).get("annotations", {})
                           .get("run.googleapis.com/invoker-iam-disabled") == "true")
        result["PUBLIC_INVOKER"] = "YES" if public else "NO"
        result["PRIVATE_SERVICE"] = "NO" if public else "YES"
        require(not public, "PUBLIC_INVOKER_DETECTED")

    stage = "SANITIZED_LOGS"
    logs = call(["gcloud", "logging", "read",
                 'resource.type="cloud_run_revision" AND resource.labels.service_name="' +
                 SERVICE + '" AND resource.labels.revision_name="' + result["REVISION"] + '"',
                 "--project=" + PROJECT, "--limit=200", "--format=json"])
    if logs.returncode == 0:
        entries = json.loads(logs.stdout)
        messages = [str(entry.get("textPayload") or
                        entry.get("jsonPayload", {}).get("message", ""))
                    for entry in entries if isinstance(entry, dict)]
        joined = "\n".join(messages).lower()
        db_error = any(token in joined for token in (
            "password authentication failed", "could not obtain connection",
            "connection refused", "permission denied for database",
            "failed to connect to database"))
        started = bool(re.search(r"keycloak.*started|started in [0-9.]+s", joined))
        schema = any(token in joined for token in (
            "liquibase", "updating database", "database schema",
            "initializing master realm", "importing realm"))
        result["DATABASE_CONNECTED"] = "YES" if started and not db_error else "UNKNOWN"
        result["KEYCLOAK_SCHEMA_INITIALIZED"] = (
            "YES" if started and schema and not db_error else "UNKNOWN")
        result["DB_EVIDENCE_CLASS"] = "SANITIZED_STARTUP_LOG_AND_READY"
        result["SCHEMA_EVIDENCE_CLASS"] = (
            "SANITIZED_SCHEMA_LOG_AND_READY" if schema else "NOT_OBSERVED")
        result["SANITIZED_LOG_CHECK"] = ("DB_ERROR_INDICATOR" if db_error
                                         else "NO_DB_ERROR_INDICATOR")
    elif error_kind(logs) == "PERMISSION":
        result["METADATA_VISIBILITY_LIMITED"] = "YES"
        result["LIMITED_READS"].append("CLOUD_RUN_LOGS")
    else:
        raise GateFailure("LOG_READ_FAILED")

    complete = all(result[key] == "YES" for key in (
        "SERVICE_EXISTS", "REVISION_READY", "HOSTNAME_MATCH",
        "IMAGE_DIGEST_MATCH", "RUNTIME_SA_MATCH", "SECRET_REFS_MATCH",
        "PROXY_READY", "DATABASE_CONNECTED", "KEYCLOAK_SCHEMA_INITIALIZED",
        "PRIVATE_SERVICE", "DB_ENV_MATCH", "PROXY_TARGET_MATCH",
        "GENERATION_MATCH", "RESOURCES_MATCH"))
    complete = complete and result["PUBLIC_INVOKER"] == "NO"
    result["KC05_STATUS"] = "PASS" if complete else "PARTIAL_POSTCHECK"
    result["KC06_READY_FOR_AUTHORIZATION"] = "YES" if complete else "NO"
except (GateFailure, ValueError, KeyError, TypeError, OSError,
        subprocess.TimeoutExpired) as exc:
    result["FAILED_STAGE"] = stage
    result["FAIL_CODE"] = str(exc) if isinstance(exc, GateFailure) else type(exc).__name__
    result["KC05_STATUS"] = (
        "HOSTNAME_MISMATCH" if result["FAIL_CODE"] == "HOSTNAME_MISMATCH"
        else "DEPLOY_FAILED_OR_PARTIAL" if result["DEPLOY_ATTEMPTED"] == "YES"
        else "PRECHECK_FAILED")
    if result["DEPLOY_ATTEMPTED"] == "YES":
        try:
            service = service_metadata()
            if isinstance(service, dict):
                inspect_service(service)
        except Exception:
            pass
    save()
    raise SystemExit(result["FAIL_CODE"])
finally:
    save()
print("KC05_STATUS=" + result["KC05_STATUS"])
print("KC06_READY_FOR_AUTHORIZATION=" + result["KC06_READY_FOR_AUTHORIZATION"])
