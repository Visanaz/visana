# OpenAPI v1 gate report

## Result

`OPENAPI_V1_BASELINE = APPROVED` after squash merge of PR #12 into backend `dev`.

Approved baseline commit: `bd656c938400414edc21cc0ff830937a53e05e3b`.
Snapshot SHA-256: `1A1CE867634D8D957D75F2ED24EFE593D8A78B61FA490386685BF72FCE0B432B`.

The canonical baseline is `openapi/visana-api-v1.json`. It is generated from runtime Springdoc in a controlled local contract context and checked for deterministic regeneration by CI.

## Scope

- Metadata: `VISANA Plan 3 API`, `v1`, OpenAPI `3.0.1`.
- Security: global HTTP bearer JWT; no OAuth client secret and no server URL.
- Errors: reusable `ProblemDetail` responses for 400/401/403/404/409/422/500 where applicable.
- Correlation: optional `X-Correlation-ID` request header and documented response header.
- Operations: stable IDs for identity, catalog, own orders, simulated payment, network queries and the blocked legacy network-node route.

## Additive revision pending PM approval

`OPENAPI_V1_BASELINE_R2 = PENDING_PM_APPROVAL`. It adds only `GET /api/v1/products/{id}` (`getProductById`): an authenticated, descriptive read of an active catalog product by UUID. The route uses the existing `CatalogQueryService`, persistence adapter and public `ProductResponse`; unknown or inactive IDs return the existing standard 404 `ProblemDetail`. This is classified `NON_BREAKING_ADDITIVE`; no existing path, operation, schema field or security declaration was removed or changed.

`CATALOG_PRODUCT_DETAIL_API_GAP = IMPLEMENTED_PENDING_PM_MERGE`. The 422 reusable response exists for the backend standard error model, but no additional business endpoint was created merely to exercise it. No finance, payout, reward, qualification or business-rule API is introduced.

## Required review before F1

PM must review this first snapshot and its endpoint/security/error shape. Only then may frontend work generate typed transport and wire the OIDC bearer flow behind its existing feature boundary.
