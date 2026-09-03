# Sprint 2 — Identity, Ownership & Security Core

**Base:** `3b70498` (`dev`, Sprint 1B squash merge)
**Branch:** `feat/plan3-sprint2-identity-ownership`

## Implemented foundation

- UUID `PlatformActor` with `ACTIVE` / `DISABLED` lifecycle.
- Explicit OIDC external identity link using provider, issuer and subject, protected by a database unique constraint.
- `ActorResolverPort` and current Keycloak/OIDC adapter. No domain package depends on Spring Security or JWT.
- Explicit generic resource ownership for new Plan 3 resources and fail-closed ownership policy.
- `/api/v1/me`, documented through OpenAPI annotations, returns only actor ID when linked, identity state and granted authority names.
- Persistent audit for identity linking/disable, unresolved identity, ownership assignment and authorization denial; safe metrics without high-cardinality labels.

## Boundaries preserved

- No email, username, phone, display name or document number auto-linking.
- No public administrative identity-link endpoint; provisioning remains an application/migration operation pending controls.
- Existing orders remain `LEGACY_ORDER_OWNERSHIP_UNRESOLVED`; `affiliate_id` is not treated as actor ownership.
- Payment confirmation is denied before payment processing when the requester is unlinked, foreign or ownership is absent. Its financial state transition and provider simulation are unchanged.
- No sponsor endpoint was evidenced; sponsor authorization stays a fail-closed policy seam.
- Privileged/admin override is `BLOCKED_BY_ROLE_MATRIX`; no roles were created or inferred.

## Validation

- Local Maven: 113 tests, 0 failures, 0 errors, 2 skipped because Windows Testcontainers Npipe remains unavailable.
- `PostgreSqlIdentityOwnershipIntegrationTest` is a PostgreSQL/Testcontainers CI test. It covers Flyway V1–V4, actor/link persistence, unique external identity, linked/unlinked/disabled resolution, explicit ownership, owner/foreign-owner authorization and audit events.
- The CI runner is the authoritative runtime for container integration while `WINDOWS_TESTCONTAINERS_NPIPE` remains environment debt.

## CI evidence

GitHub Actions run `33716950496` completed `clean verify` with 113 tests, 0 failures, 0 errors and 0 skipped. It started `postgres:16-alpine`, applied Flyway V1–V4 on PostgreSQL and executed `PostgreSqlIdentityOwnershipIntegrationTest` successfully.
