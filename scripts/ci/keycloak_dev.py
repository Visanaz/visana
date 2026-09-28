#!/usr/bin/env python3
"""Prepare/test Keycloak locally. No Google SDK, ADC, cloud mutations or deployment."""
import argparse
import base64
import json
import os
from pathlib import Path
import re
import secrets
import socket
import subprocess
import urllib.error
import urllib.parse
import urllib.request
import uuid
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[2]
INFRA = ROOT / "infra" / "keycloak"
CLIENT = "visana-dev-health"
REALM = "visana-erp"
PROXY = "gcr.io/cloud-sql-connectors/cloud-sql-proxy:2.25.4@sha256:88501f0a695a586988add1b8a206fdf3f29f9a1a3deeb9b45ef2b1481ea6be83"


def validate():
    realm = json.loads((INFRA / "visana-erp-realm.json").read_text())
    if realm["realm"] != REALM or realm.get("users") or realm.get("smtpServer"):
        raise ValueError("Realm must preserve the existing name without user/SMTP exports")
    health = next(c for c in realm["clients"] if c["clientId"] == CLIENT)
    if health["secret"] != "${VISANA_KEYCLOAK_HEALTH_CLIENT_SECRET}":
        raise ValueError("Only a runtime secret reference is permitted")
    if health["publicClient"] or not health["serviceAccountsEnabled"] or health["fullScopeAllowed"]:
        raise ValueError("Invalid confidential technical client")
    if any(health[k] for k in ("standardFlowEnabled", "directAccessGrantsEnabled", "implicitFlowEnabled")):
        raise ValueError("Technical client must only use client credentials")
    if health["defaultClientScopes"] != ["visana.health"] or health["optionalClientScopes"]:
        raise ValueError("Unexpected scopes")
    text = (INFRA / "Dockerfile").read_text() + (INFRA / "keycloak.conf").read_text()
    if "start-dev" in text or ":latest" in text or "sslmode=disable" in text:
        raise ValueError("Unsafe production configuration")
    if "start\", \"--optimized\", \"--import-realm" not in text:
        raise ValueError("Expected production startup and nondestructive import")
    return realm


def render(hostname, image, output):
    validate()
    url = urllib.parse.urlsplit(hostname)
    if url.scheme != "https" or not url.hostname or url.hostname in ("localhost", "127.0.0.1"):
        raise ValueError("An approved HTTPS cloud hostname is required")
    if url.username or url.password or url.query or url.fragment or url.path not in ("", "/"):
        raise ValueError("Hostname must be an origin without credentials/path/query")
    if not re.fullmatch(r"us-central1-docker\.pkg\.dev/visana-erp-dev/visana-repo/visana-keycloak-dev@sha256:[0-9a-f]{64}", image):
        raise ValueError("Approved project/region and published immutable image digest required")
    content = (INFRA / "cloud-run-service.template.json").read_text()
    content = content.replace("${KEYCLOAK_HOSTNAME}", hostname.rstrip("/")).replace("${KEYCLOAK_IMAGE}", image)
    service = json.loads(content)
    if "${" in json.dumps(service):
        raise ValueError("Unresolved deployment input")
    Path(output).write_text(json.dumps(service, indent=2) + "\n", encoding="utf-8")
    print("RENDERED ONLY: proposed resources are not created or deployed")


def free_port():
    with socket.socket() as listener:
        listener.bind(("127.0.0.1", 0))
        return listener.getsockname()[1]


def request(url, body=None, token=None, method=None):
    headers = {}
    if isinstance(body, dict):
        body = urllib.parse.urlencode(body).encode()
        headers["Content-Type"] = "application/x-www-form-urlencoded"
    elif body is not None:
        headers["Content-Type"] = "application/json"
    if token:
        headers["Authorization"] = "Bearer " + token
    req = urllib.request.Request(url, data=body, headers=headers, method=method)
    # Explicit localhost HTTP is confined to the isolated proof, not a TLS override.
    with urllib.request.urlopen(req, timeout=20) as result:
        data = result.read()
        return json.loads(data) if data else None


def claims(token):
    payload = token.split(".")[1]
    return json.loads(base64.urlsafe_b64decode(payload + "=" * (-len(payload) % 4)))


def test(output):
    validate()
    directory = Path(output).resolve()
    if directory == ROOT or ROOT in directory.parents:
        raise ValueError("Use an output directory outside the repository; preserve historical files")
    directory.mkdir(parents=True, exist_ok=True)
    project = "visana-kc-proof-" + uuid.uuid4().hex[:10]
    test_image = project + ":26.7.3"
    environment = dict(os.environ)
    secret_values = [secrets.token_urlsafe(32) for _ in range(4)]
    for name, value in zip(("ADMIN_PASSWORD", "DB_PASSWORD", "BOOTSTRAP_PASSWORD", "CLIENT_SECRET"), secret_values):
        environment["VISANA_KC_PROOF_" + name] = value
    port, management = free_port(), free_port()
    while management == port:
        management = free_port()
    environment.update(VISANA_KC_PROOF_PORT=str(port), VISANA_KC_PROOF_MANAGEMENT_PORT=str(management),
                       VISANA_KC_PROOF_IMAGE=test_image)
    compose = ["docker", "compose", "-p", project, "-f", str(ROOT / "compose.keycloak-test.yml")]
    log_path = directory / "execution.log"
    fixture = directory / "token-fixture.json"
    maven_volume = project + "-maven"
    report = {"scope": "isolated synthetic Docker only", "keycloak": "26.7.3", "checks": [], "result": "FAIL"}

    def check(label):
        report["checks"].append(label)
        print("PASS " + label, flush=True)

    def run(command, capture=False, timeout=900):
        result = subprocess.run(command, cwd=ROOT, env=environment, stdout=subprocess.PIPE,
                                stderr=subprocess.STDOUT, timeout=timeout)
        with log_path.open("ab") as log:
            log.write(result.stdout)
        if result.returncode:
            raise RuntimeError("Isolated command failed; inspect sanitized execution.log")
        return result.stdout.decode("utf-8", errors="replace") if capture else None

    def token():
        response = request(base + "/realms/" + REALM + "/protocol/openid-connect/token", {
            "grant_type": "client_credentials", "client_id": CLIENT,
            "client_secret": secret_values[3], "scope": "visana.health"})
        raw = response["access_token"]
        secret_values.append(raw)
        parsed = claims(raw)
        if parsed.get("iss") != base + "/realms/" + REALM or parsed.get("azp") != CLIENT:
            raise RuntimeError("Issuer/azp mismatch")
        audience = parsed.get("aud")
        if audience not in (CLIENT, [CLIENT]) or "visana.health" not in parsed.get("scope", "").split():
            raise RuntimeError("Exclusive audience/scope mismatch")
        if not parsed.get("sub"):
            raise RuntimeError("Subject absent")
        return raw, parsed

    def admin_token():
        response = request(base + "/realms/master/protocol/openid-connect/token", {
            "grant_type": "password", "client_id": "admin-cli", "username": "proof-admin",
            "password": secret_values[2]})
        secret_values.append(response["access_token"])
        return response["access_token"]

    def scan(data):
        if any(value.encode() in data for value in secret_values):
            raise RuntimeError("Synthetic credential/token detected in persisted output")

    try:
        print("Building and starting isolated PostgreSQL 18 / Keycloak", flush=True)
        run(compose + ["up", "-d", "--build", "--wait", "--wait-timeout", "240"])
        base = "http://127.0.0.1:" + str(port)
        version = run(compose + ["exec", "-T", "postgres", "psql", "-U", "proof_admin", "-d", "keycloak_test", "-Atc", "SHOW server_version"], capture=True).strip()
        if not version.startswith("18."):
            raise RuntimeError("PostgreSQL 18 required")
        report["postgresql"] = version
        check("production start --optimized with external PostgreSQL 18")
        for path in ("/health/started", "/health/ready", "/health/live"):
            if request("http://127.0.0.1:" + str(management) + path)["status"] != "UP":
                raise RuntimeError("Keycloak probe did not report UP")
        check("startup/readiness/liveness endpoints")
        discovery = request(base + "/realms/" + REALM + "/.well-known/openid-configuration")
        jwks = request(discovery["jwks_uri"])
        if discovery["issuer"] != base + "/realms/" + REALM or not jwks["keys"]:
            raise RuntimeError("Discovery/JWKS mismatch")
        check("discovery and JWKS")
        first_token, first_claims = token()
        check("actual client credentials token with exact azp, subject, audience and scope")
        admin = admin_token()
        request(base + "/admin/realms/" + REALM, body=json.dumps({"displayName": "synthetic-persistence-proof"}).encode(),
                token=admin, method="PUT")
        run(compose + ["up", "-d", "--no-deps", "--force-recreate", "--wait", "--wait-timeout", "240", "keycloak"])
        restarted_token, restarted_claims = token()
        after = request(base + "/admin/realms/" + REALM, token=admin_token())
        if restarted_claims["sub"] != first_claims["sub"] or after.get("displayName") != "synthetic-persistence-proof":
            raise RuntimeError("Persistence or nondestructive import failed")
        if {key["kid"] for key in jwks["keys"]} != {key["kid"] for key in request(discovery["jwks_uri"])["keys"]}:
            raise RuntimeError("Signing keys did not persist")
        check("new container preserves database, subject, signing keys and realm changes")
        proof = {"issuer": discovery["issuer"], "jwksUri": discovery["jwks_uri"], "client": CLIENT,
                 "subject": restarted_claims["sub"], "audience": CLIENT,
                 "tokenUri": discovery["token_endpoint"], "clientSecret": secret_values[3]}
        if os.name == "nt":
            proof["jwksUri"] = "http://keycloak:8080/realms/" + REALM + "/protocol/openid-connect/certs"
            proof["tokenUri"] = "http://keycloak:8080/realms/" + REALM + "/protocol/openid-connect/token"
        fixture.write_text(json.dumps(proof), encoding="utf-8")
        if os.name != "nt":
            fixture.chmod(0o600)
        print("Checking actual token against the unchanged backend security filter", flush=True)
        if os.name == "nt":
            run(["docker", "run", "--rm", "--network", project + "_default",
                 "-v", str(ROOT) + ":/workspace:ro", "-v", str(directory) + ":/proof",
                 "-v", maven_volume + ":/root/.m2", "-w", "/workspace", "maven:3.9.11-eclipse-temurin-21",
                 "mvn", "--batch-mode", "-Dvisana.build.directory=/proof/backend-build",
                 "-Dvisana.keycloak.proof.directory=/proof", "-Dtest=KeycloakHealthContractTest", "test"])
        else:
            run([str(ROOT / "mvnw"), "--batch-mode", "-Dvisana.build.directory=" + str(directory / "backend-build"),
                 "-Dvisana.keycloak.proof.directory=" + str(directory), "-Dtest=KeycloakHealthContractTest", "test"])
        suite_path = directory / "backend-build/surefire-reports/TEST-com.visana.erp.core.infrastructure.config.security.KeycloakHealthContractTest.xml"
        suite = ET.parse(suite_path).getroot()
        report["backend_tests"] = {k: int(suite.attrib[k]) for k in ("tests", "failures", "errors", "skipped")}
        if report["backend_tests"] != {"tests": 1, "failures": 0, "errors": 0, "skipped": 0}:
            raise RuntimeError("The real-token backend test must execute without skips")
        runtime_token = directory / "proof-runtime-token.txt"
        secret_values.append(runtime_token.read_text())
        runtime_token.unlink()
        fixture.unlink()
        check("real signed token: GET health 200/UP; business/docs/subpaths/POST health 403; anonymous health 401")
        run(["docker", "run", "--rm", PROXY, "--version"])
        help_output = run(["docker", "run", "--rm", PROXY, "--help"], capture=True)
        if not all(flag in help_output for flag in ("--health-check", "--http-address", "--http-port", "--lazy-refresh")):
            raise RuntimeError("Proposed Auth Proxy flags unavailable")
        check("Auth Proxy 2.25.4 version/flags only; no cloud connection or ADC")
        scan(run(compose + ["logs", "--no-color"], capture=True).encode())
        scan(log_path.read_bytes())
        scan(run(["docker", "image", "inspect", test_image, "--format", "{{json .Config.Env}}"], capture=True).encode())
        for path in directory.rglob("*"):
            if path.is_file() and path.suffix in (".json", ".xml", ".txt", ".log"):
                scan(path.read_bytes())
        # Scan all saved image layers, not a running container's mutable state.
        image_archive = directory / "proof-image.tar"
        run(["docker", "save", "--output", str(image_archive), test_image])
        tail = b""
        window = max(map(len, secret_values))
        with image_archive.open("rb") as image:
            while chunk := image.read(1024 * 1024):
                scan(tail + chunk)
                tail = chunk[-window:]
        image_archive.unlink()
        check("generated credentials/tokens absent from saved image layers, logs and retained test artifacts")
        report["result"] = "PASS"
    finally:
        if fixture.exists():
            fixture.unlink()
        runtime_token = directory / "proof-runtime-token.txt"
        if runtime_token.exists():
            secret_values.append(runtime_token.read_text())
            runtime_token.unlink()
        image_archive = directory / "proof-image.tar"
        if image_archive.exists():
            image_archive.unlink()
        try:
            run(compose + ["down", "--volumes", "--remove-orphans"], timeout=120)
            if os.name == "nt" and subprocess.run(["docker", "volume", "inspect", maven_volume],
                                                   stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL).returncode == 0:
                run(["docker", "volume", "rm", maven_volume], timeout=60)
            if subprocess.run(["docker", "image", "inspect", test_image],
                              stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL).returncode == 0:
                run(["docker", "image", "rm", test_image], timeout=60)
        finally:
            # Logs must be safe even when a dependency unexpectedly prints a value.
            if log_path.exists():
                safe_log = log_path.read_bytes()
                for value in secret_values:
                    safe_log = safe_log.replace(value.encode(), b"[REDACTED_SYNTHETIC]")
                log_path.write_bytes(safe_log)
            (directory / "result.json").write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(report), flush=True)


def main():
    parser = argparse.ArgumentParser()
    sub = parser.add_subparsers(dest="command", required=True)
    sub.add_parser("validate")
    proof = sub.add_parser("test")
    proof.add_argument("--output", required=True)
    rendering = sub.add_parser("render")
    rendering.add_argument("--hostname", required=True)
    rendering.add_argument("--image", required=True)
    rendering.add_argument("--output", required=True)
    args = parser.parse_args()
    if args.command == "test":
        test(args.output)
    elif args.command == "render":
        render(args.hostname, args.image, args.output)
    else:
        validate()
        print("PASS sanitized Keycloak configuration")


if __name__ == "__main__":
    try:
        main()
    except (ValueError, RuntimeError, subprocess.TimeoutExpired, urllib.error.URLError) as failure:
        print("FAIL " + type(failure).__name__ + ": preparation/proof incomplete; inspect sanitized artifacts")
        raise SystemExit(1)
