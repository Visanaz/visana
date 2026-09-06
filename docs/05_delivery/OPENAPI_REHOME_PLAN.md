# OpenAPI rehome plan after repository split

## Status

Repository split is complete: backend cleanup PR #13 merged to backend `dev`, and OpenAPI PR #12 was squash-merged after passing its checks. `OPENAPI_V1_BASELINE = APPROVED`. Rehome status: `REHOME_COMPLETED`.

### Approved baseline record

| Field | Value |
|---|---|
| Backend repository | `Visanaz/visana` |
| Backend `dev` commit | `bd656c938400414edc21cc0ff830937a53e05e3b` |
| Snapshot | `openapi/visana-api-v1.json` |
| Snapshot SHA-256 | `1A1CE867634D8D957D75F2ED24EFE593D8A78B61FA490386685BF72FCE0B432B` |
| OpenAPI version | `3.0.1` |
| API version | `v1` |
| Approved date | `2026-09-04` |

### Pending additive revision record

| Field | Value |
|---|---|
| Proposed revision | `OPENAPI_V1_BASELINE_R2` |
| Status | `APPROVED` |
| Change classification | `NON_BREAKING_ADDITIVE` |
| Added operation | `GET /api/v1/products/{id}` (`getProductById`) |
| Prior SHA-256 | `1A1CE867634D8D957D75F2ED24EFE593D8A78B61FA490386685BF72FCE0B432B` |
| Current generated SHA-256 | `D899F6669248E28C6FF3BE9F4EED57950A06C458F5EF6F6125EABC96EF799795` |

## Inventory and destination

| PR #12 path | Classification | Target after split | Action |
|---|---|---|---|
| `.github/workflows/openapi-contract.yml` | BACKEND_CI | backend | retained |
| `.node-version` | FRONTEND_CONFIG | frontend | removed; `visana-front/.node-version` is canonical |
| `docs/00_governance/LOCAL_DEVELOPMENT.md` | BACKEND_DOC | backend | reconciled with split guidance |
| `docs/04_architecture/ADR/ADR-026-OPENAPI-CONTRACT-SOURCE-OF-TRUTH.md` | BACKEND_DOC | backend | retained |
| `docs/04_architecture/API_CONTRACT_STRATEGY.md` | BACKEND_DOC | backend | retained and updated with delivery guidance |
| `docs/05_delivery/OPENAPI_V1_GATE_REPORT.md` | BACKEND_DOC | backend | retained |
| `openapi/visana-api-v1.json` | OPENAPI_BACKEND | backend | retained |
| `pom.xml` | OPENAPI_BACKEND | backend | retained |
| `src/main/java/com/visana/erp/commerce/infrastructure/adapter/in/web/CatalogController.java` | OPENAPI_BACKEND | backend | retained |
| `src/main/java/com/visana/erp/commerce/infrastructure/adapter/in/web/OrderController.java` | OPENAPI_BACKEND | backend | retained |
| `src/main/java/com/visana/erp/commerce/infrastructure/adapter/in/web/OwnedOrderController.java` | OPENAPI_BACKEND | backend | retained |
| `src/main/java/com/visana/erp/core/infrastructure/config/OpenApiConfig.java` | OPENAPI_BACKEND | backend | retained |
| `src/main/java/com/visana/erp/network/foundation/infrastructure/web/NetworkQueryController.java` | OPENAPI_BACKEND | backend | retained |
| `src/main/java/com/visana/erp/network/infrastructure/adapter/in/web/NetworkNodeController.java` | OPENAPI_BACKEND | backend | retained |
| `src/main/java/com/visana/erp/platform/infrastructure/adapter/in/web/MeController.java` | OPENAPI_BACKEND | backend | retained |
| `src/test/java/com/visana/erp/contract/OpenApiRuntimeApplication.java` | OPENAPI_BACKEND | backend | retained |
| `src/test/java/com/visana/erp/contract/OpenApiSnapshotGenerator.java` | OPENAPI_BACKEND | backend | retained |

The changed controller annotations, OpenApi configuration, snapshot runtime context and generator are backend-owned. No #12 file is an Angular source, frontend CI or generated Angular client.

## Reconciliation result

No replacement branch was required: the existing PR history was preserved through a normal merge of backend-only `dev`. The resulting PR contains only backend/OpenAPI work. Run the backend build, runtime snapshot generation and contract drift workflow before PM review. Do not close or merge #12 during this gate.
