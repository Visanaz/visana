#!/usr/bin/env python3
"""Reproduce the observed privilege shape on a disposable networkless PostgreSQL.

Runs the proposed change and V1-V8 ONLY in this synthetic container. No cloud
endpoint, user credential, live data, published port or real migration runner.
"""
import json
import os
from pathlib import Path
import secrets
import subprocess
import time
import uuid

ROOT = Path(__file__).resolve().parents[2]
POSTGRES_IMAGE = "postgres:18.3-alpine@sha256:54451ecb8ab38c24c3ec123f2fd501303a3a1856a5c66e98cecf2460d5e1e9d7"


def sanitized_failure(stage, result, synthetic_password):
    stderr = result.stderr.decode(errors="replace").replace(
        synthetic_password, "[REDACTED_SYNTHETIC_PASSWORD]"
    )
    tail = "\n".join(stderr.splitlines()[-20:]).encode("utf-8")[-4096:].decode(
        "utf-8", errors="replace"
    )
    return RuntimeError(f"stage={stage} return_code={result.returncode} stderr_tail={tail}")


def main():
    name = "visana-sql-proof-" + uuid.uuid4().hex[:10]
    synthetic_password = secrets.token_urlsafe(32)
    environment = dict(os.environ, POSTGRES_PASSWORD=synthetic_password)

    def run(args, source=None, stage="command"):
        result = subprocess.run(args, input=source, env=environment, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        if result.returncode:
            raise sanitized_failure(stage, result, synthetic_password)
        return result.stdout.decode()

    def psql_command(user="postgres", database="visana_dev", at=True):
        command = ["docker", "exec", "-i", name, "sh", "-c",
                   'PGPASSWORD="$POSTGRES_PASSWORD" exec psql "$@"', "sh",
                   "-X", "-v", "ON_ERROR_STOP=1", "-h", "127.0.0.1", "-p", "5432",
                   "-U", user, "-d", database]
        if at:
            command.append("-At")
        return command

    def sql(source, user="postgres", database="visana_dev", stage="sql"):
        return run(psql_command(user, database), source.encode(), stage)

    def ready():
        deadline = time.monotonic() + 90
        tcp_attempts = sql_attempts = stable_successes = 0
        started = time.monotonic()
        last_failure = None
        while time.monotonic() < deadline:
            running = run(["docker", "inspect", "--format", "{{.State.Running}}", name],
                          stage="startup-container-state").strip()
            if running != "true":
                raise RuntimeError("stage=startup-container-state container_not_running")
            tcp_attempts += 1
            state = subprocess.run(["docker", "exec", name, "pg_isready", "-h", "127.0.0.1",
                                    "-p", "5432", "-U", "postgres", "-d", "postgres"],
                                   env=environment, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            if state.returncode == 0:
                sql_attempts += 1
                query = subprocess.run(psql_command(database="postgres"),
                                       input=b"SELECT 1;", env=environment,
                                       stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                if query.returncode == 0 and query.stdout.strip() == b"1":
                    stable_successes += 1
                    if stable_successes == 2:
                        print(f"READY_TCP_ATTEMPTS={tcp_attempts} READY_SQL_ATTEMPTS={sql_attempts} "
                              f"READY_STABLE_SUCCESSES={stable_successes} "
                              f"READY_DURATION_MS={int((time.monotonic() - started) * 1000)}")
                        return
                    time.sleep(0.3)
                    continue
                last_failure = ("startup-select-1", query)
                stable_successes = 0
            else:
                last_failure = ("startup-pg-isready", state)
                stable_successes = 0
            time.sleep(0.25)
        if last_failure is not None:
            stage, result = last_failure
            detail = sanitized_failure(stage, result, synthetic_password)
        else:
            detail = "stage=startup-readiness no_attempts"
        raise RuntimeError(f"stage=startup-readiness timeout_seconds=90 "
                           f"READY_TCP_ATTEMPTS={tcp_attempts} READY_SQL_ATTEMPTS={sql_attempts} "
                           f"READY_STABLE_SUCCESSES={stable_successes} "
                           f"READY_DURATION_MS={int((time.monotonic() - started) * 1000)} "
                           f"last_failure=({detail})")

    try:
        run(["docker", "run", "-d", "--rm", "--name", name, "--network", "none",
             "--env", "POSTGRES_PASSWORD", POSTGRES_IMAGE], stage="container-start")
        ready()
        sql("CREATE ROLE cloudsqlsuperuser; CREATE ROLE visana_app_dev LOGIN CREATEDB CREATEROLE;"
            " GRANT cloudsqlsuperuser TO visana_app_dev; CREATE DATABASE visana_dev OWNER cloudsqlsuperuser;",
            database="postgres", stage="fixture-create")
        rights = "SELECT json_build_object('createdb',r.rolcreatedb,'createrole',r.rolcreaterole," \
                 "'administrative_member',pg_has_role('visana_app_dev','cloudsqlsuperuser','MEMBER')," \
                 "'connect',has_database_privilege('visana_app_dev','visana_dev','CONNECT')," \
                 "'usage',has_schema_privilege('visana_app_dev','public','USAGE')," \
                 "'create',has_schema_privilege('visana_app_dev','public','CREATE'))" \
                 " FROM pg_roles r WHERE r.rolname='visana_app_dev';"
        before = json.loads(sql(rights, stage="rights-before"))
        if not all(before.values()):
            raise RuntimeError("Fixture does not reproduce the observed inherited owner privileges")
        # A different database must fail before applying the candidate change.
        proposal = (ROOT / "scripts/ci/visana_dev_privileges_proposal.sql").read_text()
        rejected = subprocess.run(psql_command(database="postgres", at=False),
                                  input=proposal.encode(), env=environment,
                                  stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        if rejected.returncode == 0:
            raise RuntimeError("Wrong-database guard failed")
        sql(proposal, stage="proposal")
        after = json.loads(sql(rights, user="visana_app_dev", stage="rights-after"))
        if any(after[k] for k in ("createdb", "createrole", "administrative_member")) or not all(after[k] for k in ("connect", "usage", "create")):
            raise RuntimeError("Proposal does not preserve required rights while removing global privileges")
        for migration in sorted((ROOT / "src/main/resources/db/migration").glob("V*.sql")):
            sql(migration.read_text(), user="visana_app_dev", stage=f"migration-{migration.stem}")
        objects = json.loads(sql("SELECT json_build_object('tables',count(*),'owned_by_app',"
                                 "bool_and(pg_get_userbyid(c.relowner)='visana_app_dev'))"
                                 " FROM pg_class c JOIN pg_namespace n ON c.relnamespace=n.oid"
                                 " WHERE n.nspname='public' AND c.relkind IN ('r','p');", stage="ownership"))
        if objects["tables"] == 0 or not objects["owned_by_app"]:
            raise RuntimeError("Migration object ownership mismatch")
        print(json.dumps({"result":"PASS", "scope":"synthetic networkless PostgreSQL 18.3 only",
                          "before":before, "after":after, "migrations":"V1-V8 execute as the restricted role",
                          "objects":objects, "postgres_image":POSTGRES_IMAGE,
                          "readiness_transport":"TCP", "readiness_stable_queries":2}))
    finally:
        subprocess.run(["docker", "rm", "--force", name], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)


if __name__ == "__main__":
    try:
        main()
    except RuntimeError as failure:
        print(str(failure))
        raise SystemExit(1)
