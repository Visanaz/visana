"""DEV deployment guards. Never print credentials, raw service configuration or HTTP bodies."""
import json
import os
from pathlib import Path
import re
import sys
import urllib.error
import urllib.parse
import urllib.request


def required(env, name):
    value = env.get(name, "")
    if not value:
        raise ValueError("Missing external configuration: " + name)
    return value


def secure_url(value):
    url = urllib.parse.urlsplit(value)
    if url.scheme != "https" or not url.hostname or url.username or url.password or url.fragment:
        raise ValueError("OIDC/service URLs must use HTTPS without embedded credentials or fragments")
    if url.hostname.lower() in {"localhost", "127.0.0.1", "::1"}:
        raise ValueError("Cloud configuration cannot use loopback URLs")
    return url


def validate_config(env):
    for name in ("PROJECT_ID", "DB_URL", "DB_USER", "KEYCLOAK_ISSUER_URI",
                 "RUNTIME_SERVICE_ACCOUNT", "HEALTHCHECK_CLIENT_ID", "HEALTHCHECK_CLIENT_SECRET"):
        required(env, name)
    jdbc = env["DB_URL"]
    if not jdbc.startswith("jdbc:postgresql://"):
        raise ValueError("DB_URL must be the verified PostgreSQL TCP JDBC URL; connector/network decisions remain external")
    url = urllib.parse.urlsplit(jdbc.removeprefix("jdbc:"))
    if not url.hostname or url.hostname.lower() in {"localhost", "127.0.0.1", "::1"} or url.username or url.password:
        raise ValueError("DB_URL requires a non-loopback host without embedded credentials")
    if not url.path.strip("/"):
        raise ValueError("DB_URL must name the actual PostgreSQL database")
    query = {key.lower() for key in urllib.parse.parse_qs(url.query)}
    if query & {"user", "password", "socketfactory", "cloudsqlinstance"}:
        raise ValueError("Use DB_USER/DB_PASSWORD; no Java Connector has been verified or added")
    secure_url(env["KEYCLOAK_ISSUER_URI"])
    if not re.fullmatch(r"[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.gserviceaccount\.com", env["RUNTIME_SERVICE_ACCOUNT"]):
        raise ValueError("DEV_RUNTIME_SERVICE_ACCOUNT must identify the existing runtime service account")


def prepare_existing(service, env):
    validate_config(env)
    spec = service["spec"]["template"]["spec"]
    if spec.get("serviceAccountName") != env["RUNTIME_SERVICE_ACCOUNT"]:
        raise ValueError("Runtime identity differs from the existing service; no identity change authorized")
    containers = spec.get("containers", [])
    if len(containers) != 1:
        raise ValueError("Only the existing single application container is supported")
    container = containers[0]
    variables = {item["name"]: item for item in container.get("env", [])}
    if any(name.startswith("SPRING_DATASOURCE_") or name.startswith("SPRING_FLYWAY_") for name in variables):
        raise ValueError("Existing Spring datasource/Flyway overrides require review before deployment")
    if any(name == "SPRING_APPLICATION_JSON" or name.startswith("SPRING_CONFIG_")
           or name.startswith("MANAGEMENT_HEALTH_") or name == "MANAGEMENT_SERVER_PORT"
           or (name.startswith("SPRING_PROFILES_") and name != "SPRING_PROFILES_ACTIVE")
           for name in variables):
        raise ValueError("Existing configuration sources/profiles/health overrides require review")
    for name in ("DB_URL", "DB_USER", "KEYCLOAK_ISSUER_URI"):
        if "valueFrom" in variables.get(name, {}):
            raise ValueError("Preserve existing secret reference for " + name + "; external review required")
    for name in ("SERVER_PORT", "SERVER_ADDRESS", "JAVA_TOOL_OPTIONS", "JAVA_OPTS", "JDK_JAVA_OPTIONS"):
        if name in variables:
            raise ValueError("Existing server/JVM override requires review: " + name)
    if container.get("command") or container.get("args"):
        raise ValueError("Existing command/arguments require review before using the image ENTRYPOINT")
    values = {"SPRING_PROFILES_ACTIVE": "dev", "DB_URL": env["DB_URL"],
              "DB_USER": env["DB_USER"], "KEYCLOAK_ISSUER_URI": env["KEYCLOAK_ISSUER_URI"]}
    secret = variables.get("DB_PASSWORD", {}).get("valueFrom", {}).get("secretKeyRef")
    reference = ""
    if secret:
        name, version = secret["name"], secret["key"]
        if not re.fullmatch(r"[a-zA-Z0-9_/-]+", name) or not re.fullmatch(r"[a-zA-Z0-9_-]+", version):
            raise ValueError("Existing DB_PASSWORD secret reference has an unsupported format")
        reference = "DB_PASSWORD=" + name + ":" + version
    else:
        values["DB_PASSWORD"] = required(env, "DB_PASSWORD")
    return values, reference


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        raise ValueError("Redirect rejected; do not forward authentication to a different endpoint")


def request_json(url, data=None, headers=None):
    secure_url(url)
    request = urllib.request.Request(url, data=data, headers=headers or {"Accept": "application/json"})
    with urllib.request.build_opener(NoRedirect()).open(request, timeout=30) as response:
        if response.status != 200:
            raise ValueError("Endpoint did not return HTTP 200")
        return json.load(response)


def application_token(env):
    issuer = required(env, "KEYCLOAK_ISSUER_URI").rstrip("/")
    discovery = request_json(issuer + "/.well-known/openid-configuration")
    if discovery.get("issuer", "").rstrip("/") != issuer:
        raise ValueError("OIDC discovery issuer mismatch")
    endpoint = discovery["token_endpoint"]
    if secure_url(endpoint).netloc != secure_url(issuer).netloc:
        raise ValueError("Token endpoint origin differs from the verified issuer")
    form = urllib.parse.urlencode({"grant_type": "client_credentials",
                                  "client_id": required(env, "HEALTHCHECK_CLIENT_ID"),
                                  "client_secret": required(env, "HEALTHCHECK_CLIENT_SECRET")}).encode()
    result = request_json(endpoint, form, {"Content-Type": "application/x-www-form-urlencoded", "Accept": "application/json"})
    token = result.get("access_token", "")
    if not token or result.get("token_type", "").lower() != "bearer":
        raise ValueError("OIDC client did not provide a bearer access token")
    return token


def verify_revision(revision, service, env):
    expected = env["SERVICE_NAME"] + "-ci-" + env["GITHUB_RUN_ID"] + "-" + env["GITHUB_RUN_ATTEMPT"]
    if revision["metadata"]["name"] != expected:
        raise ValueError("Wrong revision inspected")
    if not any(item.get("type") == "Ready" and item.get("status") == "True"
               for item in revision["status"].get("conditions", [])):
        raise ValueError("Candidate revision is not Ready")
    digest = revision["status"].get("imageDigest", "").split("@")[-1]
    if digest != required(env, "IMAGE_DIGEST"):
        raise ValueError("Ready revision image digest differs from the published image")
    if revision["spec"].get("serviceAccountName") != env["RUNTIME_SERVICE_ACCOUNT"]:
        raise ValueError("Revision runtime identity changed")
    container = revision["spec"]["containers"][0]
    if container.get("ports", [{}])[0].get("containerPort") != 8080:
        raise ValueError("Revision container port differs from 8080")
    values = {item["name"]: item.get("value") for item in container.get("env", [])}
    for key, expected_value in (("SPRING_PROFILES_ACTIVE", "dev"), ("DB_URL", env["DB_URL"]),
                                ("DB_USER", env["DB_USER"]), ("KEYCLOAK_ISSUER_URI", env["KEYCLOAK_ISSUER_URI"])):
        if values.get(key) != expected_value:
            raise ValueError("Effective revision configuration mismatch: " + key)
    traffic = service.get("status", {}).get("traffic", [])
    if sum(item.get("percent", 0) for item in traffic if item.get("revisionName") == expected) != 100:
        raise ValueError("Service URL does not route all traffic to the verified revision")


def main():
    env = os.environ
    action = sys.argv[1]
    if action == "config":
        validate_config(env)
    elif action == "existing":
        values, reference = prepare_existing(json.loads(Path(sys.argv[2]).read_text()), env)
        path = Path(env["RUNNER_TEMP"]) / "dev-env.json"
        path.write_text(json.dumps(values))
        path.chmod(0o600)
        with open(env["GITHUB_OUTPUT"], "a") as output:
            output.write("db_password_secret=" + reference + "\n")
    elif action == "token":
        application_token(env)  # Confirm authentication before publishing/deploying; never persist the token.
    elif action == "revision":
        verify_revision(json.loads(Path(sys.argv[2]).read_text()), json.loads(Path(sys.argv[3]).read_text()), env)
    elif action == "health":
        url = required(env, "SERVICE_URL").rstrip("/") + "/actuator/health"
        cloud_token = (Path(env["RUNNER_TEMP"]) / "cloud-run-token").read_text().strip()
        if not cloud_token:
            raise ValueError("Missing Cloud Run invocation identity token")
        response = request_json(url, headers={"Accept": "application/json",
                     "Authorization": "Bearer " + application_token(env),
                     "X-Serverless-Authorization": "Bearer " + cloud_token})
        if response.get("status") != "UP":
            raise ValueError("Authenticated health did not report UP")
    else:
        raise ValueError("Unknown deployment guard")
    print("DEV guard passed: " + action)


if __name__ == "__main__":
    try:
        main()
    except urllib.error.HTTPError as error:
        sys.exit("DEV guard failed: HTTP " + str(error.code) + "; response body withheld")
    except ValueError as error:
        sys.exit(str(error))
    except Exception as error:
        # HTTP response bodies, configuration values and exception URLs may contain secrets.
        sys.exit("DEV guard failed (" + type(error).__name__ + "); inspect configuration/access without publishing values")
