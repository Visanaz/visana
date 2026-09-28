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


def main():
    name = "visana-sql-proof-" + uuid.uuid4().hex[:10]
    environment = dict(os.environ, POSTGRES_PASSWORD=secrets.token_urlsafe(32))

    def run(args, source=None):
        result = subprocess.run(args, input=source, env=environment, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        if result.returncode:
            raise RuntimeError("Isolated SQL proof command failed; no cloud changes")
        return result.stdout.decode()

    def sql(source, user="postgres", database="visana_dev"):
        return run(["docker", "exec", "-i", name, "psql", "-X", "-v", "ON_ERROR_STOP=1",
                    "-U", user, "-d", database, "-At"], source.encode())

    try:
        run(["docker", "run", "-d", "--rm", "--name", name, "--network", "none",
             "--env", "POSTGRES_PASSWORD", "postgres:18.3-alpine"])
        for attempt in range(90):
            state = subprocess.run(["docker", "exec", name, "pg_isready", "-U", "postgres"],
                                   stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            if state.returncode == 0:
                break
            time.sleep(1)
        else:
            raise RuntimeError("Isolated PostgreSQL startup timed out")
        sql("CREATE ROLE cloudsqlsuperuser; CREATE ROLE visana_app_dev LOGIN CREATEDB CREATEROLE;"
            " GRANT cloudsqlsuperuser TO visana_app_dev; CREATE DATABASE visana_dev OWNER cloudsqlsuperuser;",
            database="postgres")
        rights = "SELECT json_build_object('createdb',r.rolcreatedb,'createrole',r.rolcreaterole," \
                 "'administrative_member',pg_has_role('visana_app_dev','cloudsqlsuperuser','MEMBER')," \
                 "'connect',has_database_privilege('visana_app_dev','visana_dev','CONNECT')," \
                 "'usage',has_schema_privilege('visana_app_dev','public','USAGE')," \
                 "'create',has_schema_privilege('visana_app_dev','public','CREATE'))" \
                 " FROM pg_roles r WHERE r.rolname='visana_app_dev';"
        before = json.loads(sql(rights))
        if not all(before.values()):
            raise RuntimeError("Fixture does not reproduce the observed inherited owner privileges")
        # A different database must fail before applying the candidate change.
        proposal = (ROOT / "scripts/ci/visana_dev_privileges_proposal.sql").read_text()
        rejected = subprocess.run(["docker", "exec", "-i", name, "psql", "-X", "-v", "ON_ERROR_STOP=1",
                                   "-U", "postgres", "-d", "postgres"], input=proposal.encode(),
                                  stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        if rejected.returncode == 0:
            raise RuntimeError("Wrong-database guard failed")
        sql(proposal)
        after = json.loads(sql(rights, user="visana_app_dev"))
        if any(after[k] for k in ("createdb", "createrole", "administrative_member")) or not all(after[k] for k in ("connect", "usage", "create")):
            raise RuntimeError("Proposal does not preserve required rights while removing global privileges")
        for migration in sorted((ROOT / "src/main/resources/db/migration").glob("V*.sql")):
            sql(migration.read_text(), user="visana_app_dev")
        objects = json.loads(sql("SELECT json_build_object('tables',count(*),'owned_by_app',"
                                 "bool_and(pg_get_userbyid(c.relowner)='visana_app_dev'))"
                                 " FROM pg_class c JOIN pg_namespace n ON c.relnamespace=n.oid"
                                 " WHERE n.nspname='public' AND c.relkind IN ('r','p');"))
        if objects["tables"] == 0 or not objects["owned_by_app"]:
            raise RuntimeError("Migration object ownership mismatch")
        print(json.dumps({"result":"PASS", "scope":"synthetic networkless PostgreSQL 18.3 only",
                          "before":before, "after":after, "migrations":"V1-V8 execute as the restricted role",
                          "objects":objects}))
    finally:
        subprocess.run(["docker", "rm", "--force", name], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)


if __name__ == "__main__":
    try:
        main()
    except RuntimeError as failure:
        print(str(failure))
        raise SystemExit(1)
