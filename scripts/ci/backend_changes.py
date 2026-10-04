"""Classify the complete pushed tree diff; errors never authorize a deployment."""
import json
import os
from pathlib import Path
import re
import subprocess

FILES = {"pom.xml", "mvnw", "mvnw.cmd", "Dockerfile", ".dockerignore", "compose.keycloak-test.yml"}
PREFIXES = ("src/", ".mvn/", "openapi/", "scripts/ci/", "infra/keycloak/")
WORKFLOWS = {".github/workflows/build.yml", ".github/workflows/deploy-dev.yml",
             ".github/workflows/openapi-contract.yml"}


def affects_backend(paths):
    return any(path in FILES or path in WORKFLOWS or path.startswith(PREFIXES) for path in paths)


def changed_paths(event, env, git=None):
    if env.get("GITHUB_EVENT_NAME") != "push" or env.get("GITHUB_REF") != "refs/heads/dev":
        raise ValueError("Backend diff requires a DEV push")
    if event.get("ref") != "refs/heads/dev" or event.get("deleted"):
        raise ValueError("Invalid DEV push event")
    before, after = event.get("before", ""), event.get("after", "")
    if any(not re.fullmatch(r"[0-9a-f]{40}", sha) or sha == "0" * 40 for sha in (before, after)):
        raise ValueError("A complete before/after commit pair is required")
    if after != env.get("GITHUB_SHA"):
        raise ValueError("Push SHA differs from the workflow SHA")
    if git is None:
        def git(*args):
            return subprocess.run(["git", *args], check=True, stdout=subprocess.PIPE,
                                  stderr=subprocess.PIPE).stdout
    if git("rev-parse", "HEAD").decode().strip() != after:
        raise ValueError("Checkout differs from the pushed SHA")
    for sha in (before, after):
        git("cat-file", "-e", sha + "^{commit}")
    # Compare push endpoints, including every pushed commit and both sides of renames.
    raw = git("diff", "--name-only", "-z", "--no-renames", before, after, "--")
    return [path.decode("utf-8", errors="surrogateescape") for path in raw.split(b"\0") if path]


def main():
    event = json.loads(Path(os.environ["GITHUB_EVENT_PATH"]).read_text(encoding="utf-8"))
    changed = affects_backend(changed_paths(event, os.environ))
    with open(os.environ["GITHUB_OUTPUT"], "a", encoding="utf-8") as output:
        output.write("backend_changed=" + str(changed).lower() + "\n")
    print("Backend deployment impact: " + str(changed).lower())


if __name__ == "__main__":
    try:
        main()
    except Exception:
        raise SystemExit("Cannot establish backend change scope; deployment blocked")
