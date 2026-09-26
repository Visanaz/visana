"""Synthetic guard tests; no network, real secrets, cloud resources or database are used."""
import copy
import unittest
from cloud_run_dev import prepare_existing, validate_config, verify_revision


class DeploymentGuardsTest(unittest.TestCase):
    def setUp(self):
        self.env = {"PROJECT_ID": "test-project", "DB_URL": "jdbc:postgresql://db.example.invalid/appdb",
                    "DB_USER": "test-user", "DB_PASSWORD": "test-only-value",
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
        self.assertEqual(reference, "DB_PASSWORD=existing-secret:3")

    def test_preserves_literal_password_contract_when_no_secret_reference_exists(self):
        env = dict(self.env, DB_PASSWORD="  test-only-value  ")
        values, reference = prepare_existing(self.service, env)
        self.assertEqual(values["DB_PASSWORD"], "  test-only-value  ")
        self.assertEqual(reference, "")

    def test_rejects_identity_change_and_higher_precedence_configuration(self):
        bad = copy.deepcopy(self.service)
        bad["spec"]["template"]["spec"]["serviceAccountName"] = "other@example.invalid"
        with self.assertRaises(ValueError):
            prepare_existing(bad, self.env)
        for name in ("SPRING_DATASOURCE_URL", "SERVER_PORT", "JAVA_TOOL_OPTIONS", "SPRING_APPLICATION_JSON",
                     "SPRING_CONFIG_IMPORT", "SPRING_PROFILES_INCLUDE", "MANAGEMENT_HEALTH_DB_ENABLED"):
            bad = copy.deepcopy(self.service)
            bad["spec"]["template"]["spec"]["containers"][0]["env"] = [{"name": name, "value": "test-only-value"}]
            with self.assertRaises(ValueError):
                prepare_existing(bad, self.env)

    def test_rejects_ready_but_wrong_digest_or_old_traffic(self):
        revision = {"metadata": {"name": "service-ci-123-1"},
                    "status": {"conditions": [{"type": "Ready", "status": "True"}], "imageDigest": self.env["IMAGE_DIGEST"]},
                    "spec": {"serviceAccountName": self.env["RUNTIME_SERVICE_ACCOUNT"], "containers": [{
                        "ports": [{"containerPort": 8080}], "env": [{"name": name, "value": value} for name, value in
                        (("SPRING_PROFILES_ACTIVE", "dev"), ("DB_URL", self.env["DB_URL"]), ("DB_USER", self.env["DB_USER"]),
                         ("KEYCLOAK_ISSUER_URI", self.env["KEYCLOAK_ISSUER_URI"]))]}]}}
        service = {"status": {"traffic": [{"revisionName": "service-ci-123-1", "percent": 100}]}}
        verify_revision(revision, service, self.env)
        bad = copy.deepcopy(revision)
        bad["status"]["imageDigest"] = "sha256:" + "b" * 64
        with self.assertRaises(ValueError):
            verify_revision(bad, service, self.env)
        with self.assertRaises(ValueError):
            verify_revision(revision, {"status": {"traffic": [{"revisionName": "old", "percent": 100}]}}, self.env)


if __name__ == "__main__":
    unittest.main()
