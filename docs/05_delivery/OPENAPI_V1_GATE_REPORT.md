# OpenAPI v1 gate report

## Result

The canonical baseline is `openapi/visana-api-v1.json`. It is generated from runtime Springdoc in a controlled local contract context and checked for deterministic regeneration by CI.

## Scope

- Metadata: `VISANA Plan 3 API`, `v1`, OpenAPI `3.0.1`.
- Security: global HTTP bearer JWT; no OAuth client secret and no server URL.
- Errors: reusable `ProblemDetail` responses for 400/401/403/404/409/422/500 where applicable.
- Correlation: optional `X-Correlation-ID` request header and documented response header.
- Operations: stable IDs for identity, catalog, own orders, simulated payment, network queries and the blocked legacy network-node route.

## Known limits

`GET /api/v1/products/{id}` is not exposed and remains `CATALOG_PRODUCT_DETAIL_API_GAP`. The 422 reusable response exists for the backend standard error model, but no additional business endpoint was created merely to exercise it. No finance, payout, reward, qualification or business-rule API is introduced.

## Required review before F1

PM must review this first snapshot and its endpoint/security/error shape. Only then may frontend work generate typed transport and wire the OIDC bearer flow behind its existing feature boundary.
