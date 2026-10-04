-- PostgreSQL 18 metadata only. Authenticate first; no password/hash/business data.
-- All statements run together; do not rely on Studio preserving a previous session.
BEGIN READ ONLY;
WITH RECURSIVE target AS (
  SELECT oid FROM pg_roles WHERE rolname = 'visana_app_dev'
), memberships AS (
  SELECT m.roleid, m.member, m.admin_option, m.inherit_option, m.set_option,
         ARRAY[m.member, m.roleid] AS path, 1 AS depth
  FROM pg_auth_members m JOIN target t ON m.member = t.oid
  UNION ALL
  SELECT m.roleid, m.member, m.admin_option, m.inherit_option, m.set_option,
         p.path || m.roleid, p.depth + 1
  FROM pg_auth_members m JOIN memberships p ON m.member = p.roleid
  WHERE NOT m.roleid = ANY(p.path)
), relations AS (
  SELECT c.*, n.nspname FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
  WHERE n.nspname NOT LIKE 'pg_%' AND n.nspname <> 'information_schema'
    AND c.relkind IN ('r', 'p', 'v', 'm', 'S')
)
SELECT jsonb_pretty(jsonb_build_object(
  'session', jsonb_build_object('database', current_database(), 'current_user', current_user,
    'session_user', session_user, 'server_version', current_setting('server_version'),
    'read_only', current_setting('transaction_read_only'), 'evaluated_role', 'visana_app_dev',
    'schema', current_schema(), 'schemas', current_schemas(false),
    'search_path', current_setting('search_path'),
    'search_path_observed_for_application', current_user = 'visana_app_dev' AND session_user = 'visana_app_dev'),
  'role', (SELECT to_jsonb(r) FROM (
    SELECT rolname, rolcanlogin, rolsuper, rolinherit, rolcreatedb, rolcreaterole,
           rolreplication, rolbypassrls FROM pg_roles WHERE rolname = 'visana_app_dev') r),
  'direct_memberships', COALESCE((SELECT jsonb_agg(jsonb_build_object(
    'granted_role', p.rolname, 'grantor', g.rolname, 'admin_option', m.admin_option,
    'inherit_option', m.inherit_option, 'set_option', m.set_option))
    FROM pg_auth_members m JOIN target t ON m.member = t.oid
    JOIN pg_roles p ON p.oid = m.roleid JOIN pg_roles g ON g.oid = m.grantor), '[]'::jsonb),
  'membership_paths', COALESCE((SELECT jsonb_agg(jsonb_build_object(
    'role', pg_get_userbyid(m.roleid), 'depth', m.depth,
    'path', (SELECT jsonb_agg(pg_get_userbyid(p.oid) ORDER BY p.position) FROM unnest(m.path) WITH ORDINALITY p(oid, position)),
    'edge_admin_option', m.admin_option, 'edge_inherit_option', m.inherit_option,
    'edge_set_option', m.set_option,
    'effective_usage', pg_has_role('visana_app_dev', m.roleid, 'USAGE'),
    'effective_set', pg_has_role('visana_app_dev', m.roleid, 'SET'))) FROM memberships m), '[]'::jsonb),
  'database', (SELECT jsonb_build_object('name', d.datname, 'owner', pg_get_userbyid(d.datdba),
    'connect', has_database_privilege('visana_app_dev', d.oid, 'CONNECT'),
    'create', has_database_privilege('visana_app_dev', d.oid, 'CREATE'),
    'temporary', has_database_privilege('visana_app_dev', d.oid, 'TEMPORARY'),
    'public_acl', COALESCE((SELECT jsonb_agg(jsonb_build_object('privilege', a.privilege_type,
      'grantable', a.is_grantable, 'grantor', pg_get_userbyid(a.grantor)))
      FROM aclexplode(COALESCE(d.datacl, acldefault('d', d.datdba))) a WHERE a.grantee = 0), '[]'::jsonb))
    FROM pg_database d WHERE d.datname = 'visana_dev'),
  'application_search_path_settings', COALESCE((SELECT jsonb_agg(jsonb_build_object(
    'database', COALESCE(d.datname, '*'), 'role', COALESCE(r.rolname, '*'), 'setting', cfg))
    FROM pg_db_role_setting s LEFT JOIN pg_database d ON d.oid = s.setdatabase
    LEFT JOIN pg_roles r ON r.oid = s.setrole CROSS JOIN LATERAL unnest(s.setconfig) cfg
    WHERE (s.setdatabase = 0 OR d.datname = 'visana_dev')
      AND (s.setrole = 0 OR r.rolname = 'visana_app_dev') AND cfg LIKE 'search_path=%'), '[]'::jsonb),
  'schemas', COALESCE((SELECT jsonb_agg(jsonb_build_object('name', n.nspname,
    'owner', pg_get_userbyid(n.nspowner),
    'usage', has_schema_privilege('visana_app_dev', n.oid, 'USAGE'),
    'create', has_schema_privilege('visana_app_dev', n.oid, 'CREATE'),
    'public_acl', COALESCE((SELECT jsonb_agg(jsonb_build_object('privilege', a.privilege_type,
      'grantable', a.is_grantable, 'grantor', pg_get_userbyid(a.grantor)))
      FROM aclexplode(COALESCE(n.nspacl, acldefault('n', n.nspowner))) a WHERE a.grantee = 0), '[]'::jsonb)))
    FROM pg_namespace n WHERE n.nspname NOT LIKE 'pg_%' AND n.nspname <> 'information_schema'), '[]'::jsonb),
  'tables', COALESCE((SELECT jsonb_agg(jsonb_build_object('schema', c.nspname, 'name', c.relname,
    'kind', c.relkind, 'owner', pg_get_userbyid(c.relowner),
    'owner_role_available', pg_has_role('visana_app_dev', c.relowner, 'USAGE'),
    'select', has_table_privilege('visana_app_dev', c.oid, 'SELECT'),
    'insert', has_table_privilege('visana_app_dev', c.oid, 'INSERT'),
    'update', has_table_privilege('visana_app_dev', c.oid, 'UPDATE'),
    'delete', has_table_privilege('visana_app_dev', c.oid, 'DELETE'),
    'references', has_table_privilege('visana_app_dev', c.oid, 'REFERENCES'),
    'public_acl', COALESCE((SELECT jsonb_agg(jsonb_build_object('privilege', a.privilege_type))
      FROM aclexplode(COALESCE(c.relacl, acldefault('r', c.relowner))) a WHERE a.grantee = 0), '[]'::jsonb)))
    FROM relations c WHERE c.relkind <> 'S'), '[]'::jsonb),
  'sequences', COALESCE((SELECT jsonb_agg(jsonb_build_object('schema', c.nspname, 'name', c.relname,
    'owner', pg_get_userbyid(c.relowner), 'usage', has_sequence_privilege('visana_app_dev', c.oid, 'USAGE'),
    'select', has_sequence_privilege('visana_app_dev', c.oid, 'SELECT'),
    'update', has_sequence_privilege('visana_app_dev', c.oid, 'UPDATE'),
    'public_acl', COALESCE((SELECT jsonb_agg(jsonb_build_object('privilege', a.privilege_type))
      FROM aclexplode(COALESCE(c.relacl, acldefault('S', c.relowner))) a WHERE a.grantee = 0), '[]'::jsonb)))
    FROM relations c WHERE c.relkind = 'S'), '[]'::jsonb),
  'flyway_history', COALESCE((SELECT jsonb_agg(jsonb_build_object('schema', c.nspname,
    'name', c.relname, 'owner', pg_get_userbyid(c.relowner))) FROM relations c
    WHERE c.relname = 'flyway_schema_history'), '[]'::jsonb)
)) AS metadata_report
WHERE current_database() = 'visana_dev' AND EXISTS (SELECT 1 FROM target);
ROLLBACK;
