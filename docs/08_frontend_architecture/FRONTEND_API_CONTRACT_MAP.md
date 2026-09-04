# Frontend API Contract Map

## Contratos disponibles

| Feature | Endpoint actual | Uso futuro | Estado |
|---|---|---|---|
| Auth/context | `GET /api/v1/me` | resolver actorId, identityStatus y roles tras OIDC | AVAILABLE |
| Catalog | `GET /api/v1/products`, `GET /api/v1/categories` | lista/detalle/paginación/sort permitido | AVAILABLE |
| Orders | `POST /api/v1/orders`, `GET /api/v1/orders`, `GET /api/v1/orders/{id}` | crear/listar/ver propias | AVAILABLE |
| Payment simulation | `POST /api/v1/orders/{id}/pay` | no exponer como checkout productivo | PARTIAL / DG-15 |
| Network | `GET /api/v1/network/me`, `/direct`, `/ancestors`, `/descendants` | red mínima y drill-down | PARTIAL |

El cliente de órdenes debe enviar sólo `lines[{productId, quantity}]`. Nunca envía precio, total, ownerActorId, status privilegiado ni campos de compensación. `GET /api/v1/products` actual no evidencia búsqueda textual; el filtro UI debe permanecer local sobre página cargada o esperar contrato de servidor, no simular paginación global.

## FR-API gap register

| ID | Feature | Necesidad legado/producto | Target endpoint | Estado | Dominio / blocker | Prioridad |
|---|---|---|---|---|---|---|
| FR-API-GAP-001 | Profile | perfiles de negocio y vínculo visible | no existe | MISSING | Platform / DG-17 | P0 |
| FR-API-GAP-002 | Network | display name, PII mínima, conteos y exploración de rama | actual APIs sólo IDs/profundidad | PARTIAL | Network / role-PII decision | P0 |
| FR-API-GAP-003 | Catalog | búsqueda por texto, detalle enriquecido/medios | no está demostrado | PARTIAL | Commerce / product decision | P1 |
| FR-API-GAP-004 | Cart | validación/actualización de draft sin precio cliente | no existe | PRODUCT_DECISION_REQUIRED | Commerce | P2 |
| FR-API-GAP-005 | Orders | cancelación, estado operativo, envío e idempotencia | no existe | MISSING | Commerce / DG-13/DG-15 | P0 |
| FR-API-GAP-006 | Checkout | intención/confirmación real, callback y reconciliación | no existe | BLOCKED | Payments / DG-15 | P0 |
| FR-API-GAP-007 | Qualification | estado, progreso y requisitos | no existe | BLOCKED | Rules / DG-01/DG-03/DG-11 | P0 |
| FR-API-GAP-008 | Rewards | resultados calculados por periodo/beneficiario | no existe | BLOCKED | Compensation / DG-02/DG-04/DG-06–DG-09 | P0 |
| FR-API-GAP-009 | Finance | ledger, settlement, payout, reverso y retenciones | no existe | BLOCKED | Finance / DG-04/DG-05/DG-10/DG-12 | P0 |
| FR-API-GAP-010 | Admin | capacidades y contratos administrativos | no existe | BLOCKED | Role matrix | P1 |
| FR-API-GAP-011 | Support | ticketing/caso de soporte | no existe | PRODUCT_DECISION_REQUIRED | Product | P3 |
| FR-API-GAP-012 | Public search | `/viajero/search` y propiedades | no equivalente | LEGACY_ONLY | Product scope | P2 |

**Conteo: 12 gaps**, de los cuales 7 son P0. Un gap no autoriza a que el frontend invente contrato; se convierte en acuerdo OpenAPI y pruebas contract-first antes de implementación.

## Estrategia de cliente API

Se propone evaluar cliente generado desde OpenAPI para DTOs/operaciones estables, conservando una capa manual delgada para autenticación, `ProblemDetail`, correlation ID, cancelación y view models. ADR-017 evita un acoplamiento de componentes a respuestas raw y evita también generación ciega que oculte comportamiento de UX.
