-- Synthetic, isolated Docker database only. The test runner generates passwords.
\getenv proof_password VISANA_KC_PROOF_DB_PASSWORD
CREATE ROLE keycloak_test LOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION NOBYPASSRLS PASSWORD :'proof_password';
CREATE DATABASE keycloak_test OWNER keycloak_test;
REVOKE CONNECT, TEMPORARY ON DATABASE keycloak_test FROM PUBLIC;
