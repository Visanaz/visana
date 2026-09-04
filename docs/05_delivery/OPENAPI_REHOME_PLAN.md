# OpenAPI rehome plan after repository split

## Status

Repository split is complete: backend cleanup PR #13 merged to backend `dev`, and this PR has been reconciled by merging that backend-only baseline. PR #12 remains open and unmerged for PM contract review. Rehome status: `REHOME_COMPLETED_PENDING_PM_MERGE`.

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
