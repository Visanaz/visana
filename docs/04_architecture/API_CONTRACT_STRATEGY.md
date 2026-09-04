# API contract strategy

## Canonical flow

`Spring controllers and DTOs -> Springdoc runtime -> openapi/visana-api-v1.json -> future typed frontend transport`.

The snapshot is JSON because Springdoc emits JSON directly and the generator sorts object keys for stable review. It declares `VISANA Plan 3 API`, version `v1`, OpenAPI `3.0.1`, global bearer JWT security, reusable `ProblemDetail` responses and `X-Correlation-ID` request/response conventions. No server URL is encoded, so DEV/QA/production endpoints remain runtime configuration.

## Generation and drift

Run `./mvnw --batch-mode -DskipTests test-compile exec:java -Dexec.args=openapi/visana-api-v1.json` with Java 21. The generator starts a local contract-only Spring context with real controllers/configuration and mock application dependencies; no business operation is invoked. It validates OpenAPI metadata, schemas, bearer scheme and unique operation IDs before writing a sorted snapshot. CI repeats generation and fails if `git diff --exit-code -- openapi/visana-api-v1.json` detects drift.

## Current boundary

The snapshot covers only current `/api/v1` mappings. `GET /api/v1/products/{id}` is absent (`CATALOG_PRODUCT_DETAIL_API_GAP`). Qualification, volume, rewards, commissions, tax, shipping, settlement and payout are absent by design. `POST /api/v1/orders/{orderId}/pay` is explicitly NON_PRODUCTION/SIMULATED.

## Future client

Prefer OpenAPI Generator `typescript-angular` for typed transport because it is already pinned in the F0 workspace and supports regeneration. Keep auth/bearer, correlation, ProblemDetail and view-model behavior in feature adapters/facades. Alternative generators such as `ng-openapi-gen` and `openapi-typescript-codegen` require a separate compatibility/maintenance review before adoption; no client is generated in this gate.

## Future breaking-change gate

This is `OPENAPI_V1_BASELINE`. A later backlog item must classify removed paths/fields, type changes, required-field additions and security changes as breaking or non-breaking; the present mandatory control is snapshot drift detection.
