"""DEV deployment guards. Never print credentials, raw service configuration or HTTP bodies."""
import json
import os
from pathlib import Path
import re
import sys
import subprocess
import urllib.error
import urllib.parse
import urllib.request

class GuardError(ValueError):
    """Only these fixed, reviewed messages may be emitted to Actions."""


CONNECTOR_PROPERTIES = {"socketFactory": "com.google.cloud.sql.postgres.SocketFactory",
                        "ipTypes": "PUBLIC", "cloudSqlRefreshStrategy": "lazy", "enableIamAuth": "false"}
CONFIG_NAMES = ("DB_URL", "DB_USER", "KEYCLOAK_ISSUER_URI", "DEV_HEALTHCHECK_CLIENT_ID",
                "DEV_HEALTHCHECK_SUBJECT", "DEV_HEALTHCHECK_AUDIENCE")


def validate_jdbc(jdbc, instance):
    if not re.fullmatch(r"jdbc:postgresql:///[A-Za-z_][A-Za-z0-9_]*\?[^?#%\s]+", jdbc):
        raise GuardError("Invalid DEV PostgreSQL connector URL structure")
    pairs = urllib.parse.parse_qsl(jdbc.split("?", 1)[1], keep_blank_values=True, strict_parsing=True)
    expected = dict(CONNECTOR_PROPERTIES, cloudSqlInstance=instance)
    if len(pairs) != len(expected) or len({key for key, value in pairs}) != len(pairs) or dict(pairs) != expected:
        raise GuardError("Invalid DEV connector parameters; values withheld")


def secret_reference(env):
    reference = required(env, "DB_PASSWORD_SECRET_REF")
    if not re.fullmatch(r"[A-Za-z0-9_-]+:[1-9][0-9]*", reference):
        raise GuardError("DB_PASSWORD_SECRET_REF requires a secret name and pinned numeric version")
    if required(env, "DB_CREDENTIAL_ROTATION_CONFIRMED") != "true":
        raise GuardError("PostgreSQL credential rotation must be confirmed by its authorized owner")
    return "DB_PASSWORD=" + reference


def required(env, name):
    value = env.get(name, "")
    if not value:
        raise GuardError("Missing external configuration: " + name)
    return value


def secure_url(value):
    url = urllib.parse.urlsplit(value)
    if url.scheme != "https" or not url.hostname or url.username or url.password or url.fragment:
        raise GuardError("OIDC/service URLs must use HTTPS without embedded credentials or fragments")
    if url.hostname.lower() in {"localhost", "127.0.0.1", "::1"}:
        raise GuardError("Cloud configuration cannot use loopback URLs")
    return url


def validate_config(env):
    for name in ("PROJECT_ID", "DB_URL", "DB_USER", "KEYCLOAK_ISSUER_URI",
                 "RUNTIME_SERVICE_ACCOUNT", "HEALTHCHECK_CLIENT_ID", "HEALTHCHECK_CLIENT_SECRET"):
        required(env, name)
    for name in ("REGION", "HEALTHCHECK_SUBJECT", "HEALTHCHECK_AUDIENCE"):
        required(env, name)
    if env["PROJECT_ID"] != "visana-erp-dev" or env["REGION"] != "us-central1":
        raise GuardError("Invalid DEV destination")
    validate_jdbc(env["DB_URL"], env["PROJECT_ID"] + ":" + env["REGION"] + ":visana-db-dev")
    secret_reference(env)
    for name in ("DB_USER", "HEALTHCHECK_CLIENT_ID", "HEALTHCHECK_SUBJECT", "HEALTHCHECK_AUDIENCE"):
        if any(c.isspace() for c in env[name]) or "${" in env[name] or not env[name]:
            raise GuardError("Invalid external identity/database configuration")
    secure_url(env["KEYCLOAK_ISSUER_URI"])
    if not re.fullmatch(r"[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.gserviceaccount\.com", env["RUNTIME_SERVICE_ACCOUNT"]):
        raise GuardError("DEV_RUNTIME_SERVICE_ACCOUNT must identify the existing runtime service account")


def prepare_existing(service, env):
    validate_config(env)
    spec = service["spec"]["template"]["spec"]
    if spec.get("serviceAccountName") != env["RUNTIME_SERVICE_ACCOUNT"]:
        raise GuardError("Runtime identity differs from the existing service; no identity change authorized")
    containers = spec.get("containers", [])
    if len(containers) != 1:
        raise GuardError("Only the existing single application container is supported")
    container = containers[0]
    variables = {item["name"]: item for item in container.get("env", [])}
    if len(variables) != len(container.get("env", [])):
        raise GuardError("Duplicate environment names require review")
    if any(name.startswith("VISANA_SECURITY_") or name.startswith("SPRING_SECURITY_") for name in variables):
        raise GuardError("Existing security overrides require review")
    if any(name.startswith("SPRING_DATASOURCE_") or name.startswith("SPRING_FLYWAY_") for name in variables):
        raise GuardError("Existing Spring datasource/Flyway overrides require review before deployment")
    if any(name == "SPRING_APPLICATION_JSON" or name.startswith("SPRING_CONFIG_")
           or name.startswith("MANAGEMENT_HEALTH_") or name == "MANAGEMENT_SERVER_PORT"
           or (name.startswith("SPRING_PROFILES_") and name != "SPRING_PROFILES_ACTIVE")
           for name in variables):
        raise GuardError("Existing configuration sources/profiles/health overrides require review")
    for name in CONFIG_NAMES:
        if "valueFrom" in variables.get(name, {}):
            raise GuardError("Preserve existing secret reference for " + name + "; external review required")
    for name in ("SERVER_PORT", "SERVER_ADDRESS", "JAVA_TOOL_OPTIONS", "JAVA_OPTS", "JDK_JAVA_OPTIONS", "GOOGLE_APPLICATION_CREDENTIALS"):
        if name in variables:
            raise GuardError("Existing server/JVM override requires review: " + name)
    if container.get("command") or container.get("args"):
        raise GuardError("Existing command/arguments require review before using the image ENTRYPOINT")
    values = {"SPRING_PROFILES_ACTIVE": "dev", "DB_URL": env["DB_URL"],
              "DB_USER": env["DB_USER"], "KEYCLOAK_ISSUER_URI": env["KEYCLOAK_ISSUER_URI"]}
    values.update({"DEV_HEALTHCHECK_CLIENT_ID": env["HEALTHCHECK_CLIENT_ID"],
                   "DEV_HEALTHCHECK_SUBJECT": env["HEALTHCHECK_SUBJECT"],
                   "DEV_HEALTHCHECK_AUDIENCE": env["HEALTHCHECK_AUDIENCE"]})
    reference = secret_reference(env)
    return values, reference


def password_transition_flag(service):
    """A future approved deploy must remove the literal before changing its Cloud Run type."""
    variables = service["spec"]["template"]["spec"]["containers"][0].get("env", [])
    password = next((item for item in variables if item.get("name") == "DB_PASSWORD"), None)
    if password and "valueFrom" not in password:
        return "--remove-env-vars=DB_PASSWORD"
    return ""


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        raise GuardError("Redirect rejected; do not forward authentication to a different endpoint")


def request_json(url, data=None, headers=None):
    secure_url(url)
    request = urllib.request.Request(url, data=data, headers=headers or {"Accept": "application/json"})
    with urllib.request.build_opener(NoRedirect()).open(request, timeout=30) as response:
        if response.status != 200:
            raise GuardError("Endpoint did not return HTTP 200")
        return json.load(response)


def application_token(env):
    issuer = required(env, "KEYCLOAK_ISSUER_URI").rstrip("/")
    discovery = request_json(issuer + "/.well-known/openid-configuration")
    if discovery.get("issuer", "").rstrip("/") != issuer:
        raise GuardError("OIDC discovery issuer mismatch")
    endpoint = discovery["token_endpoint"]
    if secure_url(endpoint).netloc != secure_url(issuer).netloc:
        raise GuardError("Token endpoint origin differs from the verified issuer")
    form = urllib.parse.urlencode({"grant_type": "client_credentials",
                                  "client_id": required(env, "HEALTHCHECK_CLIENT_ID"),
                                  "client_secret": required(env, "HEALTHCHECK_CLIENT_SECRET"),
                                  "scope": "visana.health"}).encode()
    result = request_json(endpoint, form, {"Content-Type": "application/x-www-form-urlencoded", "Accept": "application/json"})
    token = result.get("access_token", "")
    if not token or result.get("token_type", "").lower() != "bearer":
        raise GuardError("OIDC client did not provide a bearer access token")
    return token


def verify_revision(revision, service, env):
    expected = env["SERVICE_NAME"] + "-ci-" + env["GITHUB_RUN_ID"] + "-" + env["GITHUB_RUN_ATTEMPT"]
    if revision["metadata"]["name"] != expected:
        raise GuardError("Wrong revision inspected")
    if not any(item.get("type") == "Ready" and item.get("status") == "True"
               for item in revision["status"].get("conditions", [])):
        raise GuardError("Candidate revision is not Ready")
    digest = revision["status"].get("imageDigest", "").split("@")[-1]
    if digest != required(env, "IMAGE_DIGEST"):
        raise GuardError("Ready revision image digest differs from the published image")
    if revision["spec"].get("serviceAccountName") != env["RUNTIME_SERVICE_ACCOUNT"]:
        raise GuardError("Revision runtime identity changed")
    container = revision["spec"]["containers"][0]
    if container.get("ports", [{}])[0].get("containerPort") != 8080:
        raise GuardError("Revision container port differs from 8080")
    values = {item["name"]: item.get("value") for item in container.get("env", [])}
    for key, expected_value in (("SPRING_PROFILES_ACTIVE", "dev"), ("DB_URL", env["DB_URL"]),
                                ("DB_USER", env["DB_USER"]), ("KEYCLOAK_ISSUER_URI", env["KEYCLOAK_ISSUER_URI"]),
                                ("DEV_HEALTHCHECK_CLIENT_ID", env["HEALTHCHECK_CLIENT_ID"]),
                                ("DEV_HEALTHCHECK_SUBJECT", env["HEALTHCHECK_SUBJECT"]),
                                ("DEV_HEALTHCHECK_AUDIENCE", env["HEALTHCHECK_AUDIENCE"])):
        if values.get(key) != expected_value:
            raise GuardError("Effective revision configuration mismatch: " + key)
    secret = next((item.get("valueFrom", {}).get("secretKeyRef", {}) for item in container.get("env", [])
                   if item.get("name") == "DB_PASSWORD"), {})
    name, version = env["DB_PASSWORD_SECRET_REF"].split(":")
    if secret != {"name": name, "key": version}:
        raise GuardError("Revision DB_PASSWORD must reference the approved rotated Secret Manager version")
    traffic = service.get("status", {}).get("traffic", [])
    if sum(item.get("percent", 0) for item in traffic if item.get("revisionName") == expected) != 100:
        raise GuardError("Service URL does not route all traffic to the verified revision")


def safe_spec(spec, env):
    """Never retain literal passwords, unknown env values or command/argument contents."""
    expected = {"SPRING_PROFILES_ACTIVE": "dev", **{name: env.get(name) for name in CONFIG_NAMES}}
    expected.update({"DEV_HEALTHCHECK_CLIENT_ID": env.get("HEALTHCHECK_CLIENT_ID"),
                     "DEV_HEALTHCHECK_SUBJECT": env.get("HEALTHCHECK_SUBJECT"),
                     "DEV_HEALTHCHECK_AUDIENCE": env.get("HEALTHCHECK_AUDIENCE")})
    containers = []
    for container in spec.get("containers", []):
        variables = []
        for item in container.get("env", []):
            safe = {"name": item["name"]}
            if item["name"] in expected:
                safe["value"] = expected[item["name"]] if item.get("value") == expected[item["name"]] else "[MISMATCH]"
            if "valueFrom" in item:
                ref = item.get("valueFrom", {}).get("secretKeyRef", {})
                if re.fullmatch(r"[A-Za-z0-9_-]+", str(ref.get("name", ""))) and re.fullmatch(r"[1-9][0-9]*", str(ref.get("key", ""))):
                    safe["valueFrom"] = {"secretKeyRef": {"name": ref["name"], "key": ref["key"]}}
                else:
                    safe["valueFrom"] = {}  # Preserve detection without leaking unreviewed values.
            variables.append(safe)
        containers.append({"env": variables, "command": bool(container.get("command")),
                           "args": bool(container.get("args")),
                           "ports": [{"containerPort": port.get("containerPort")} for port in container.get("ports", [])]})
    return {"serviceAccountName": spec.get("serviceAccountName"), "containers": containers}


def capture(kind, env):
    """Capture gcloud output in memory; project an explicit allowlist before writing evidence."""
    validate_config(env)
    command = ["gcloud", "run", "services" if kind == "service" else "revisions", "describe"]
    name = env["SERVICE_NAME"] if kind == "service" else env["SERVICE_NAME"] + "-ci-" + env["GITHUB_RUN_ID"] + "-" + env["GITHUB_RUN_ATTEMPT"]
    if not re.fullmatch(r"[a-z][a-z0-9-]+", name):
        raise GuardError("Invalid resource identifier")
    result = subprocess.run(command + [name, "--project=" + env["PROJECT_ID"], "--region=" + env["REGION"], "--format=json"],
                            capture_output=True, text=True, check=False)
    if result.returncode:
        raise GuardError("Cloud resource metadata query failed; output withheld")
    raw = json.loads(result.stdout)
    if kind == "service":
        traffic = [{"revisionName": item.get("revisionName"), "percent": item.get("percent", 0)}
                   for item in raw.get("status", {}).get("traffic", [])]
        return {"spec": {"template": {"spec": safe_spec(raw["spec"]["template"]["spec"], env)}},
                "status": {"traffic": traffic}}
    return {"metadata": {"name": raw["metadata"]["name"]}, "spec": safe_spec(raw["spec"], env),
            "status": {"conditions": [{"type": item.get("type"), "status": item.get("status")}
                                      for item in raw.get("status", {}).get("conditions", [])],
                       "imageDigest": raw.get("status", {}).get("imageDigest", "")}}


def main():
    env = os.environ
    action = sys.argv[1]
    if action == "config":
        validate_config(env)
    elif action in ("capture-service", "capture-revision"):
        data = capture("service" if action == "capture-service" else "revision", env)
        path = Path(sys.argv[2])
        path.write_text(json.dumps(data))
        path.chmod(0o600)
    elif action == "existing":
        service = json.loads(Path(sys.argv[2]).read_text())
        values, reference = prepare_existing(service, env)
        path = Path(env["RUNNER_TEMP"]) / "dev-env.json"
        path.write_text(json.dumps(values))
        path.chmod(0o600)
        with open(env["GITHUB_OUTPUT"], "a") as output:
            output.write("db_password_secret=" + reference + "\n")
            output.write("db_password_transition=" + password_transition_flag(service) + "\n")
    elif action == "token":
        application_token(env)  # Confirm authentication before publishing/deploying; never persist the token.
    elif action == "revision":
        verify_revision(json.loads(Path(sys.argv[2]).read_text()), json.loads(Path(sys.argv[3]).read_text()), env)
    elif action == "health":
        url = required(env, "SERVICE_URL").rstrip("/") + "/actuator/health"
        response = request_json(url, headers={"Accept": "application/json",
                     "Authorization": "Bearer " + application_token(env)})
        if response.get("status") != "UP":
            raise GuardError("Authenticated health did not report UP")
    else:
        raise GuardError("Unknown deployment guard")
    print("DEV guard passed: " + action)


def run():
    try:
        main()
        return 0
    except urllib.error.HTTPError as error:
        print("DEV guard failed: HTTP " + str(error.code) + "; response body withheld", file=sys.stderr)
    except GuardError as error:
        print(str(error), file=sys.stderr)
    except Exception as error:
        # HTTP response bodies, configuration values and exception URLs may contain secrets.
        print("DEV guard failed; inspect configuration/access without publishing values", file=sys.stderr)
    return 1


if __name__ == "__main__":
    sys.exit(run())
