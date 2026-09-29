import unittest
from backend_changes import affects_backend, changed_paths


class BackendChangesTest(unittest.TestCase):
    def setUp(self):
        self.before, self.after = "a" * 40, "b" * 40
        self.event = {"before": self.before, "after": self.after, "ref": "refs/heads/dev"}
        self.env = {"GITHUB_EVENT_NAME": "push", "GITHUB_REF": "refs/heads/dev", "GITHUB_SHA": self.after}
        self.calls = []

    def git(self, *args):
        self.calls.append(args)
        return (self.after + "\n").encode() if args[0] == "rev-parse" else b""

    def test_documentation_and_cost_control_do_not_deploy(self):
        self.assertFalse(affects_backend(["README.md", "docs/guide.md", "infra/cost-dev-01/control.py"]))
        self.assertFalse(affects_backend([]))

    def test_all_artifact_and_deploy_dependencies_trigger(self):
        for path in ("pom.xml", "src/main/resources/a", "src/test/java/a", "Dockerfile", ".dockerignore",
                     "mvnw", "mvnw.cmd", ".mvn/wrapper/a", "openapi/api.json", "scripts/ci/guard.py",
                     ".github/workflows/build.yml", ".github/workflows/deploy-dev.yml",
                     ".github/workflows/openapi-contract.yml", "compose.keycloak-test.yml", "infra/keycloak/Dockerfile"):
            with self.subTest(path=path): self.assertTrue(affects_backend([path]))

    def test_direct_endpoints_include_all_commits_and_rename_deletions(self):
        def git(*args):
            return b"src/removed.java\0docs/renamed.java\0" if args[0] == "diff" else self.git(*args)
        self.assertTrue(affects_backend(changed_paths(self.event, self.env, git)))
        changed_paths(self.event, self.env, self.git)
        self.assertIn(("diff", "--name-only", "-z", "--no-renames", self.before, self.after, "--"), self.calls)

    def test_creation_deletion_bad_sha_and_mismatches_block(self):
        for updates in ({"before": "0" * 40}, {"after": "0" * 40}, {"deleted": True},
                        {"before": "--help"}, {"ref": "refs/heads/main"}, {"after": "c" * 40}):
            with self.subTest(updates=updates), self.assertRaises(ValueError):
                changed_paths(dict(self.event, **updates), self.env, self.git)

    def test_other_events_and_checkout_mismatch_block(self):
        for updates in ({"GITHUB_EVENT_NAME": "pull_request"}, {"GITHUB_REF": "refs/heads/main"}):
            with self.assertRaises(ValueError): changed_paths(self.event, dict(self.env, **updates), self.git)
        with self.assertRaises(ValueError): changed_paths(self.event, self.env, lambda *args: b"wrong\n")

    def test_missing_commit_or_git_error_never_becomes_no_change(self):
        def git(*args):
            if args[0] == "cat-file": raise RuntimeError("unavailable")
            return self.git(*args)
        with self.assertRaises(RuntimeError): changed_paths(self.event, self.env, git)
