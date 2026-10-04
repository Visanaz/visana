"""Synthetic guard tests; no network, real secrets, cloud resources or database are used."""
import copy
import unittest
from cloud_run_dev import prepare_existing, validate_config, verify_revision, safe_spec, capture, secret_reference
import json
from pathlib import Path
from tempfile import TemporaryDirectory
from unittest.mock import patch
from types import SimpleNamespace
from contextlib import redirect_stdout, redirect_stderr
from io import StringIO
import urllib.error
from cloud_run_dev import run, main, password_transition_flag, cors_transition_flag, validate_cors, verify_health


class DeploymentGuardsTest(unittest.TestCase):
    def setUp(self):
        self.env = {"PROJECT_ID": "visana-erp-dev", "DB_URL": "jdbc:postgresql:///appdb?socketFactory=com.google.cloud.sql.postgres.SocketFactory&cloudSqlInstance=visana-erp-dev:us-central1:visana-db-dev&ipTypes=PUBLIC&cloudSqlRefreshStrategy=lazy&enableIamAuth=false",
                    "DB_USER": "test-user", "DB_PASSWORD_SECRET_REF": "rotated-secret:3",
                    "DB_CREDENTIAL_ROTATION_CONFIRMED": "true", "REGION": "us-central1",
                    "HEALTHCHECK_SUBJECT": "synthetic-subject", "HEALTHCHECK_AUDIENCE": "synthetic-health-api",
                    "KEYCLOAK_ISSUER_URI": "https://id.example.invalid/realms/test",
                    "RUNTIME_SERVICE_ACCOUNT": "runtime@test-project.iam.gserviceaccount.com",
                    "HEALTHCHECK_CLIENT_ID": "test-client", "HEALTHCHECK_CLIENT_SECRET": "test-only-value",
                    "SERVICE_NAME": "service", "GITHUB_RUN_ID": "123", "GITHUB_RUN_ATTEMPT": "1",
                    "IMAGE_DIGEST": "sha256:" + "a" * 64}
        self.service = {"spec": {"template": {"spec": {"serviceAccountName": self.env["RUNTIME_SERVICE_ACCOUNT"],
                                                      "containers": [{"env": []}]}}}}

    def test_missing_source_fails_without_disclosing_other_values(self):
        for name in ("DB_URL", "DB_USER", "KEYCLOAK_ISSUER_URI", "RUNTIME_SERVICE_ACCOUNT", "HEALTHCHECK_CLIENT_SECRET"):
            env = {key: value for key, value in self.env.items() if key != name}
            with self.assertRaisesRegex(ValueError, "Missing external configuration: " + name):
                validate_config(env)

    def test_rejects_loopback_embedded_credentials_and_unimplemented_connector(self):
        for jdbc in ("jdbc:mysql://db/app", "jdbc:postgresql://localhost/app", "jdbc:postgresql://db/",
                     "jdbc:postgresql://user:password@db/app", "jdbc:postgresql://db/app?password=test",
                     "jdbc:postgresql://db/app?socketFactory=unverified"):
            with self.assertRaises(ValueError):
                validate_config(dict(self.env, DB_URL=jdbc))

    def test_preserves_existing_password_secret_reference(self):
        container = self.service["spec"]["template"]["spec"]["containers"][0]
        container["env"] = [{"name": "DB_PASSWORD", "valueFrom": {"secretKeyRef": {"name": "existing-secret", "key": "3"}}}]
        values, reference = prepare_existing(self.service, self.env)
        self.assertNotIn("DB_PASSWORD", values)
        self.assertEqual(reference, "DB_PASSWORD=rotated-secret:3")
        self.assertEqual(password_transition_flag(self.service), "")

    def test_literal_password_is_never_carried_to_the_new_revision(self):
        self.service["spec"]["template"]["spec"]["containers"][0]["env"] = [{"name": "DB_PASSWORD", "value": "fake-leaked-password"}]
        values, reference = prepare_existing(self.service, self.env)
        self.assertNotIn("DB_PASSWORD", values)
        self.assertEqual(reference, "DB_PASSWORD=rotated-secret:3")
        self.assertNotIn("fake-leaked-password", json.dumps(values))
        self.assertEqual(password_transition_flag(self.service), "--remove-env-vars=DB_PASSWORD")

    def test_rotation_attestation_and_pinned_secret_are_required(self):
        for updates in ({"DB_CREDENTIAL_ROTATION_CONFIRMED": "false"}, {"DB_PASSWORD_SECRET_REF": "secret:latest"},
                        {"DB_PASSWORD_SECRET_REF": "secret:3,INJECT=other:1"}):
            with self.assertRaises(ValueError):
                validate_config(dict(self.env, **updates))

    def test_connector_positive_and_negative_contract(self):
        validate_config(self.env)
        good = self.env["DB_URL"]
        invalid = ["${DB_URL}", "not-jdbc", "jdbc:postgresql://34.42.149.84/appdb", good.replace("/appdb", "/${DB_NAME}"),
                   good.replace("appdb?", "?"), good.replace("PUBLIC", "PRIVATE"), good.replace("lazy", "background"),
                   good.replace("postgres.SocketFactory", "unverified.Factory"), good.replace("visana-erp-dev:", "other-project:"),
                   good.replace("enableIamAuth=false", "enableIamAuth=true"), good + "&user=test", good + "&password=fake-secret",
                   good + "&sslmode=disable", good + "&cloudSqlInstance=other", good + "&socketFactory=evil", good + "&token=fake-token",
                   good.replace("socketFactory", "%73ocketFactory"), good.replace("socketFactory", "SocketFactory"),
                   good + "#fake-secret", good.replace("&ipTypes=PUBLIC", ""), good + "&", good + "&bad=fake-secret"]
        for url in invalid:
            with self.subTest(case=invalid.index(url)):
                with self.assertRaises(ValueError):
                    validate_config(dict(self.env, DB_URL=url))

    def test_diagnostic_allowlist_discards_secrets_even_in_mismatch_fields(self):
        secret = "fictional-password-do-not-log"
        spec = {"serviceAccountName": self.env["RUNTIME_SERVICE_ACCOUNT"], "containers": [{"command": [secret], "args": [secret],
                "env": [{"name": "DB_PASSWORD", "value": secret}, {"name": "UNKNOWN", "value": secret},
                        {"name": "DB_URL", "value": "jdbc:postgresql://user:" + secret + "@db/app"},
                        {"name": "KEYCLOAK_ISSUER_URI", "value": secret}]}]}
        sanitized = safe_spec(spec, self.env)
        self.assertNotIn(secret, json.dumps(sanitized))
        self.assertEqual(sanitized["containers"][0]["env"][2]["value"], "[MISMATCH]")
        with patch("cloud_run_dev.subprocess.run", return_value=SimpleNamespace(returncode=1, stdout=secret, stderr=secret)):
            with self.assertRaises(ValueError) as error:
                capture("service", self.env)
            self.assertNotIn(secret, str(error.exception))

    def test_error_outputs_never_disclose_exception_url_body_or_configuration(self):
        secret = "fictional-password-or-token"
        for error in (ValueError(secret), RuntimeError(secret),
                      urllib.error.HTTPError("https://invalid/" + secret, 401, secret, {}, StringIO(secret))):
            stdout, stderr = StringIO(), StringIO()
            with patch("cloud_run_dev.main", side_effect=error), redirect_stdout(stdout), redirect_stderr(stderr):
                self.assertEqual(run(), 1)
            self.assertNotIn(secret, stdout.getvalue() + stderr.getvalue())

    def test_rejects_identity_change_and_higher_precedence_configuration(self):
        bad = copy.deepcopy(self.service)
        bad["spec"]["template"]["spec"]["serviceAccountName"] = "other@example.invalid"
        with self.assertRaises(ValueError):
            prepare_existing(bad, self.env)
        for name in ("SPRING_DATASOURCE_URL", "SERVER_PORT", "JAVA_TOOL_OPTIONS", "SPRING_APPLICATION_JSON",
                     "SPRING_CONFIG_IMPORT", "SPRING_PROFILES_INCLUDE", "MANAGEMENT_HEALTH_DB_ENABLED",
                     "MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS", "MANAGEMENT_ENDPOINTS_WEB_BASE_PATH",
                     "MANAGEMENT_SERVER_ADDRESS", "VISANA_API_CORS_ALLOWED_ORIGINS",
                     "VISANA_API_CORSALLOWEDORIGINS", "VISANA_API_CORSALLOWEDORIGINS_0_"):
            bad = copy.deepcopy(self.service)
            bad["spec"]["template"]["spec"]["containers"][0]["env"] = [{"name": name, "value": "test-only-value"}]
            with self.assertRaises(ValueError):
                prepare_existing(bad, self.env)

    def valid_revision(self):
        revision = {"metadata": {"name": "service-ci-123-1"},
                    "status": {"conditions": [{"type": "Ready", "status": "True"}], "imageDigest": self.env["IMAGE_DIGEST"]},
                    "spec": {"serviceAccountName": self.env["RUNTIME_SERVICE_ACCOUNT"], "containers": [{
                        "ports": [{"containerPort": 8080}], "env": [{"name": name, "value": value} for name, value in
                        (("SPRING_PROFILES_ACTIVE", "dev"), ("DB_URL", self.env["DB_URL"]), ("DB_USER", self.env["DB_USER"]),
                         ("KEYCLOAK_ISSUER_URI", self.env["KEYCLOAK_ISSUER_URI"]),
                         ("DEV_HEALTHCHECK_CLIENT_ID", self.env["HEALTHCHECK_CLIENT_ID"]),
                         ("DEV_HEALTHCHECK_SUBJECT", self.env["HEALTHCHECK_SUBJECT"]),
                         ("DEV_HEALTHCHECK_AUDIENCE", self.env["HEALTHCHECK_AUDIENCE"]))] +
                         [{"name": "DB_PASSWORD", "valueFrom": {"secretKeyRef": {"name": "rotated-secret", "key": "3"}}}]}]}}
        service = {"status": {"traffic": [{"revisionName": "service-ci-123-1", "percent": 100}]}}
        return revision, service

    def test_rejects_ready_but_wrong_digest_or_old_traffic(self):
        revision, service = self.valid_revision()
        verify_revision(revision, service, self.env)
        bad = copy.deepcopy(revision)
        bad["status"]["imageDigest"] = "sha256:" + "b" * 64
        with self.assertRaises(ValueError):
            verify_revision(bad, service, self.env)
        with self.assertRaises(ValueError):
            verify_revision(revision, {"status": {"traffic": [{"revisionName": "old", "percent": 100}]}}, self.env)

    def test_cors_requires_approved_https_origins_when_frontend_is_enabled(self):
        self.assertEqual(validate_cors({}), "")
        with self.assertRaises(ValueError): validate_cors({"FRONTEND_CLOUD_ENABLED": "true"})
        origins = "https://frontend.example.invalid,https://qa.example.invalid"
        self.assertEqual(validate_cors({"CORS_ALLOWED_ORIGINS": origins, "FRONTEND_CLOUD_ENABLED": "true"}), origins)
        values, _ = prepare_existing(self.service, dict(self.env, CORS_ALLOWED_ORIGINS=origins, FRONTEND_CLOUD_ENABLED="true"))
        self.assertEqual(values["CORS_ALLOWED_ORIGINS"], origins)
        for origin in ("*", "https://*.example.invalid", "null", "http://localhost:4200", "https://127.0.0.2",
                       "https://localhost", "https://app.localhost", "https://[::1]", "https://0.0.0.0",
                       "https://app.example.invalid/", "https://app.example.invalid?x=1",
                       "https://app.example.invalid#x", "https://user:fake-secret@app.example.invalid",
                       origins + ",", " https://app.example.invalid", "https://app.example.invalid:invalid"):
            with self.subTest(origin=origin), self.assertRaisesRegex(ValueError, "values withheld"):
                validate_cors({"CORS_ALLOWED_ORIGINS": origin})

    def test_backend_only_payload_omits_cors(self):
        for updates in ({}, {"CORS_ALLOWED_ORIGINS": ""},
                        {"CORS_ALLOWED_ORIGINS": "", "FRONTEND_CLOUD_ENABLED": "false"}):
            with self.subTest(updates=updates):
                values, _ = prepare_existing(self.service, dict(self.env, **updates))
                self.assertNotIn("CORS_ALLOWED_ORIGINS", values)

    def test_cors_transition_removes_only_existing_cors_when_desired_empty(self):
        container = self.service["spec"]["template"]["spec"]["containers"][0]
        container["env"] = [{"name": "UNRELATED", "value": "keep"}]
        self.assertEqual(cors_transition_flag(self.service, self.env), "")
        for old in ("", "https://stale.example.invalid"):
            container["env"] = [{"name": "UNRELATED", "value": "keep"},
                                {"name": "CORS_ALLOWED_ORIGINS", "value": old}]
            before = copy.deepcopy(self.service)
            self.assertEqual(cors_transition_flag(self.service, self.env), "--remove-env-vars=CORS_ALLOWED_ORIGINS")
            self.assertEqual(cors_transition_flag(self.service, dict(self.env, CORS_ALLOWED_ORIGINS="https://approved.example.invalid")), "")
            self.assertEqual(self.service, before)
        with self.assertRaises(ValueError):
            cors_transition_flag(self.service, dict(self.env, CORS_ALLOWED_ORIGINS="*"))

    def test_existing_action_serializes_no_empty_values_and_only_safe_outputs(self):
        for existing_cors in (False, True):
            with self.subTest(existing_cors=existing_cors), TemporaryDirectory() as directory:
                root = Path(directory)
                source, output = root / "service.json", root / "outputs"
                service = copy.deepcopy(self.service)
                variables = [{"name": "DB_PASSWORD", "value": "synthetic-old-password"}]
                if existing_cors:
                    variables.append({"name": "CORS_ALLOWED_ORIGINS", "value": "https://stale.example.invalid"})
                service["spec"]["template"]["spec"]["containers"][0]["env"] = variables
                source.write_text(json.dumps(service))
                env = dict(self.env, RUNNER_TEMP=directory, GITHUB_OUTPUT=str(output), CORS_ALLOWED_ORIGINS="")
                stdout = StringIO()
                with patch.dict("cloud_run_dev.os.environ", env, clear=True), \
                        patch("cloud_run_dev.sys.argv", ["cloud_run_dev.py", "existing", str(source)]), redirect_stdout(stdout):
                    main()
                payload = json.loads((root / "dev-env.json").read_text())
                self.assertNotIn("", payload.values(), "deploy-cloudrun rejects empty JSON env values")
                self.assertNotIn("CORS_ALLOWED_ORIGINS", payload)
                self.assertEqual(output.read_text().splitlines(), [
                    "db_password_secret=DB_PASSWORD=rotated-secret:3",
                    "db_password_transition=--remove-env-vars=DB_PASSWORD",
                    "cors_transition=" + ("--remove-env-vars=CORS_ALLOWED_ORIGINS" if existing_cors else "")])
                self.assertEqual(stdout.getvalue(), "DEV guard passed: existing\n")

    def test_backend_only_revision_requires_absent_cors(self):
        revision, service = self.valid_revision()
        verify_revision(revision, service, self.env)
        for entry in ({"name": "CORS_ALLOWED_ORIGINS", "value": ""},
                      {"name": "CORS_ALLOWED_ORIGINS", "value": "https://stale.example.invalid"},
                      {"name": "CORS_ALLOWED_ORIGINS", "valueFrom": {"secretKeyRef": {"name": "synthetic", "key": "1"}}}):
            with self.subTest(entry=entry):
                bad = copy.deepcopy(revision)
                bad["spec"]["containers"][0]["env"].append(entry)
                with self.assertRaisesRegex(ValueError, "configuration mismatch: CORS_ALLOWED_ORIGINS"):
                    verify_revision(bad, service, self.env)

    def test_frontend_revision_requires_exact_approved_cors(self):
        origins = "https://frontend.example.invalid,https://qa.example.invalid"
        env = dict(self.env, CORS_ALLOWED_ORIGINS=origins, FRONTEND_CLOUD_ENABLED="true")
        revision, service = self.valid_revision()
        with self.assertRaisesRegex(ValueError, "configuration mismatch: CORS_ALLOWED_ORIGINS"):
            verify_revision(revision, service, env)
        revision["spec"]["containers"][0]["env"].append({"name": "CORS_ALLOWED_ORIGINS", "value": origins})
        verify_revision(revision, service, env)
        for wrong in ("", "https://frontend.example.invalid", "https://stale.example.invalid"):
            revision["spec"]["containers"][0]["env"][-1]["value"] = wrong
            with self.assertRaisesRegex(ValueError, "configuration mismatch: CORS_ALLOWED_ORIGINS"):
                verify_revision(revision, service, env)

    def test_health_requires_application_and_visible_database_up(self):
        verify_health({"status": "UP", "components": {"db": {"status": "UP"}}})
        for body in ({"status": "UP"}, {"status": "DOWN"},
                     {"status": "UP", "components": {"db": {"status": "DOWN"}}}):
            with self.assertRaises(ValueError): verify_health(body)


if __name__ == "__main__":
    unittest.main()
