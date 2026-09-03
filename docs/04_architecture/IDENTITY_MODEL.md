# Plan 3 Identity Model

## Boundary

```text
Keycloak/OIDC JWT (issuer + sub)
  -> KeycloakAuthenticatedPrincipalAdapter
  -> AuthenticatedPrincipal
  -> ActorResolverPort
  -> PlatformActor
  -> ResourceOwnership + AuthorizationPolicy
  -> persistent audit
```

`PlatformActor` is the stable internal technical identity, identified by UUID. It is not an affiliate, distributor, customer or administrator. The domain has no dependency on JWT, Spring Security, email, username or display name.

## External identity

`ExternalIdentityLink` binds one Platform Actor to `provider + issuer + subject`. The database constraint prevents an external OIDC identity from being linked to two actors. `sub` is the stable OIDC subject and is always evaluated together with its issuer. Email is not stored, queried or used for linking.

Valid JWTs without an active link resolve explicitly to `UNLINKED_IDENTITY`; disabled actor or link resolves to `DISABLED_IDENTITY`. Neither state grants ownership.

## Provisioning and business profiles

Provisioning is explicit through an application service or an approved migration process. There is no public link-administration endpoint and no automatic account linking. The current source does not demonstrate a Platform Actor-to-affiliate, customer or distributor cardinality. This remains DG-17.

## Ownership and authorization

`resource_ownerships` stores explicit ownership for Plan 3 resources and is unique per `(resource_type, resource_id)`. The policy is fail-closed: absent ownership denies. It contains no privileged-role override because the source does not provide a role matrix. Existing orders carry `affiliate_id`, not an internal actor identity; they remain `LEGACY_ORDER_OWNERSHIP_UNRESOLVED`.

Payment confirmation is guarded before the use case executes. A linked actor must own the `ORDER` resource; an unlinked actor, foreign actor, missing ownership or unknown resource is denied without entering payment semantics. Sponsor endpoints were not found; `SPONSOR_RELATIONSHIP` remains a fail-closed policy seam only.

## Observability and audit

Metrics `identity_resolution_failure`, `unlinked_identity` and `authorization_denied` have no user/resource labels. Audit events cover identity-link creation/disable, unresolved identity and ownership/authorization outcomes. Correlation IDs are read from the existing request foundation, with `system` for non-request explicit provisioning.
