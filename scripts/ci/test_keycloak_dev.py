import contextlib
import io
import json
from pathlib import Path
import tempfile
import unittest
import keycloak_dev


class KeycloakPreparationTest(unittest.TestCase):
    def test_no_exported_users_and_only_runtime_secret_reference(self):
        realm = keycloak_dev.validate()
        self.assertNotIn("users", realm)
        self.assertNotIn("smtpServer", realm)
        self.assertEqual("visana-erp", realm["realm"])

    def test_render_keeps_cloud_scope_identity_secret_versions_and_probes(self):
        with tempfile.TemporaryDirectory() as directory, contextlib.redirect_stdout(io.StringIO()):
            path = Path(directory) / "service.json"
            keycloak_dev.render("https://proof.example.invalid",
                                "us-central1-docker.pkg.dev/visana-erp-dev/visana-repo/visana-keycloak-dev@sha256:" + "a" * 64, path)
            service = json.loads(path.read_text())
        spec = service["spec"]["template"]["spec"]
        self.assertEqual("visana-keycloak-dev@visana-erp-dev.iam.gserviceaccount.com", spec["serviceAccountName"])
        self.assertEqual("visana-keycloak-dev", service["metadata"]["name"])
        self.assertEqual("visana-erp-dev", service["metadata"]["namespace"])
        self.assertEqual("1", service["metadata"]["annotations"]["run.googleapis.com/maxScale"])
        self.assertEqual("1", service["metadata"]["annotations"]["run.googleapis.com/minScale"])
        keycloak, proxy = spec["containers"]
        self.assertEqual("/health/ready", keycloak["startupProbe"]["httpGet"]["path"])
        self.assertEqual("/health/ready", keycloak["readinessProbe"]["httpGet"]["path"])
        self.assertIn("--address=127.0.0.1", proxy["args"])
        refs = [item["valueFrom"]["secretKeyRef"] for item in keycloak["env"] if "valueFrom" in item]
        self.assertEqual(3, len(refs))
        self.assertTrue(all(item["key"] == "1" for item in refs))

    def test_rejects_local_http_credential_or_non_origin_hostname(self):
        for hostname in ("http://example.invalid", "https://localhost", "https://127.0.0.1",
                         "https://user:password@example.invalid", "https://example.invalid/path", "https://example.invalid?query=1"):
            with self.subTest(hostname=hostname), self.assertRaises(ValueError):
                keycloak_dev.render(hostname, "invalid", "unused.json")

    def test_rejects_mutable_or_other_project_image(self):
        for image in ("keycloak:latest", "us-central1-docker.pkg.dev/other/repo/visana-keycloak-dev@sha256:" + "a" * 64):
            with self.subTest(image=image), self.assertRaises(ValueError):
                keycloak_dev.render("https://proof.example.invalid", image, "unused.json")


if __name__ == "__main__":
    unittest.main()
