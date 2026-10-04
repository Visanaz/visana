-- PROPOSAL ONLY. Not executed. New names/resources require separate approval.
-- Authorized Cloud SQL administrator provisions the LOGIN password directly,
-- stores it securely in the proposed numeric secret version, and never logs it.
CREATE ROLE keycloak_app_dev LOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION NOBYPASSRLS;
CREATE DATABASE keycloak_dev OWNER keycloak_app_dev;
REVOKE CONNECT, TEMPORARY ON DATABASE keycloak_dev FROM PUBLIC;
-- The database owner implicitly controls public through pg_database_owner on PG18.
-- Keycloak owns the objects it initializes, separately from all VISANA business data.
