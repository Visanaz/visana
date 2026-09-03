# ADR-011 External identity links and internal platform actors

**Status:** ACCEPTED for Plan 3 technical foundation.

**Context:** Keycloak is the current identity provider, while DG-14 remains open. The legacy source does not demonstrate that a Keycloak user, email, username or affiliate is the same business identity.

**Decision:** Introduce UUID `PlatformActor` and link an external identity with the unique tuple `(provider, issuer, subject)`. The OIDC subject is not used alone; issuer scopes it. Links and actors may be `ACTIVE` or `DISABLED`. Provisioning is explicit only. The identity and ownership domain remains outside the Keycloak/Spring Security adapter.

**Consequences:** Valid but unlinked identities are denied by default. Email-based auto-linking is prohibited. PostgreSQL V3/V4 and Testcontainers validate the constraint and ownership foundation. This decision does not define actor-to-business-profile cardinality, privileged roles, or a Keycloak replacement; DG-14 and DG-17 remain open.
