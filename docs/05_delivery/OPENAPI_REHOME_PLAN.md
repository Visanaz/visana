# OpenAPI rehome plan after repository split

## Status

Backend PR #12 (`feat/plan3-openapi-v1-contract`, head `3238d6166358ad693b69e30dd26687b95fff60dc`) remains open and unmerged. It was based on the transitional `dev` that contained Angular F0, so it must not be merged after this cleanup.

## Inventory and destination

| PR #12 path | Classification | Target after split | Action |
|---|---|---|---|
| `.github/workflows/openapi-contract.yml` | BACKEND_CI | backend | retain/reapply |
| `.node-version` | FRONTEND_CONFIG | frontend | discard from backend; `visana-front/.node-version` is canonical |
| `docs/00_governance/LOCAL_DEVELOPMENT.md` | BACKEND_DOC | backend | reconcile/reapply with split guidance |
| `docs/04_architecture/ADR/ADR-026-OPENAPI-CONTRACT-SOURCE-OF-TRUTH.md` | BACKEND_DOC | backend | retain/reapply |
| `docs/04_architecture/API_CONTRACT_STRATEGY.md` | BACKEND_DOC | backend | retain/reapply |
| `docs/05_delivery/OPENAPI_V1_GATE_REPORT.md` | BACKEND_DOC | backend | retain/reapply |
| `openapi/visana-api-v1.json` | OPENAPI_BACKEND | backend | retain/reapply |
| `pom.xml` | OPENAPI_BACKEND | backend | retain/reapply |
| `src/main/java/com/visana/erp/commerce/infrastructure/adapter/in/web/CatalogController.java` | OPENAPI_BACKEND | backend | retain/reapply |
| `src/main/java/com/visana/erp/commerce/infrastructure/adapter/in/web/OrderController.java` | OPENAPI_BACKEND | backend | retain/reapply |
| `src/main/java/com/visana/erp/commerce/infrastructure/adapter/in/web/OwnedOrderController.java` | OPENAPI_BACKEND | backend | retain/reapply |
| `src/main/java/com/visana/erp/core/infrastructure/config/OpenApiConfig.java` | OPENAPI_BACKEND | backend | retain/reapply |
| `src/main/java/com/visana/erp/network/foundation/infrastructure/web/NetworkQueryController.java` | OPENAPI_BACKEND | backend | retain/reapply |
| `src/main/java/com/visana/erp/network/infrastructure/adapter/in/web/NetworkNodeController.java` | OPENAPI_BACKEND | backend | retain/reapply |
| `src/main/java/com/visana/erp/platform/infrastructure/adapter/in/web/MeController.java` | OPENAPI_BACKEND | backend | retain/reapply |
| `src/test/java/com/visana/erp/contract/OpenApiRuntimeApplication.java` | OPENAPI_BACKEND | backend | retain/reapply |
| `src/test/java/com/visana/erp/contract/OpenApiSnapshotGenerator.java` | OPENAPI_BACKEND | backend | retain/reapply |

The changed controller annotations, OpenApi configuration, snapshot runtime context and generator are backend-owned. No #12 file is an Angular source, frontend CI or generated Angular client.

## Future replacement branch

After the cleanup PR is merged, create a new branch from backend-only `dev` and reapply only the rows marked retain/reapply. Run the backend build, runtime snapshot generation and contract drift workflow there. Do not close or merge #12 until the replacement branch is reviewed; current recommendation is `KEEP_OPEN_TEMPORARILY`.
