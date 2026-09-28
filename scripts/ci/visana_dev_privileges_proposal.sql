-- PROPOSAL ONLY. Never run from the application or an automated pipeline.
-- Requires separate approval and an authorized administrator in visana_dev.
-- Re-read the same metadata immediately beforehand; stop if objects/roles changed.
BEGIN;
DO $$ BEGIN
  IF current_database() <> 'visana_dev' THEN
    RAISE EXCEPTION 'Wrong database; stop';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_database d WHERE d.datname = 'visana_dev'
                 AND pg_get_userbyid(d.datdba) = 'cloudsqlsuperuser')
     OR NOT EXISTS (SELECT 1 FROM pg_roles r WHERE r.rolname = 'visana_app_dev'
                    AND r.rolcreatedb AND r.rolcreaterole AND NOT r.rolsuper) THEN
    RAISE EXCEPTION 'Observed owner/role attributes changed; review before applying';
  END IF;
  IF EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
             WHERE n.nspname NOT LIKE 'pg_%' AND n.nspname <> 'information_schema'
               AND c.relkind IN ('r', 'p', 'v', 'm', 'S')) THEN
    RAISE EXCEPTION 'Database no longer empty; review ownership before changing membership';
  END IF;
END $$;
-- Keep the explicit application/migration rights before removing inherited ownership.
GRANT CONNECT ON DATABASE visana_dev TO visana_app_dev;
GRANT USAGE, CREATE ON SCHEMA public TO visana_app_dev;
REVOKE cloudsqlsuperuser FROM visana_app_dev;
ALTER ROLE visana_app_dev NOCREATEDB NOCREATEROLE;
COMMIT;
-- Re-run inspect_visana_dev.sql in a new application session; expect no indirect
-- administrative memberships, CONNECT + public USAGE/CREATE, and no global CREATE*.
