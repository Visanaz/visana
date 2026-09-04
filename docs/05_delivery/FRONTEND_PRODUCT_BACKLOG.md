# Frontend Product Backlog — propuesta

Estimaciones son sólo complejidad; no son horas, precio ni autorización de construcción. **Conteo: 6 epics, 13 features y 29 stories/tech stories.**

| Epic | Features | Dependencia |
|---|---|---|
| FEP-01 Foundation & Auth | FF-01, FF-02 | BACKEND_READY parcial / DG-14 |
| FEP-02 Commerce journeys | FF-04 a FF-07 | BACKEND_READY parcial / DG-13/DG-15 |
| FEP-03 Profile & Network | FF-03, FF-08 | BACKEND_READY parcial / DG-17 |
| FEP-04 Public, Support & Design | FF-13 | PRODUCT_DECISION_REQUIRED |
| FEP-05 Qualification, Rewards & Finance | FF-09 a FF-11 | BUSINESS_RULE_BLOCKED |
| FEP-06 Admin | FF-12 | BACKEND_BLOCKED / role matrix |

| ID | Epic | Story / tech story | P | Complexity | Dependency |
|---|---|---|---|---|---|
| ST-FE-001 | FEP-01 | Definir workspace, standalone routing y configuración externa | P0 | M | BACKEND_READY |
| ST-FE-002 | FEP-01 | Integrar adaptador OIDC PKCE según DG-14 | P0 | L | DG-14 |
| ST-FE-003 | FEP-01 | Consumir `/api/v1/me` y estados linked/unlinked | P0 | M | BACKEND_READY |
| ST-FE-004 | FEP-01 | Shell, guards y estados unauthorized/forbidden | P0 | M | BACKEND_READY |
| ST-FE-005 | FEP-01 | Error/ProblemDetail/correlation ID y observabilidad sin PII | P1 | M | BACKEND_READY |
| ST-FE-006 | FEP-02 | Cliente catálogo, listado, paginación y UX states | P0 | M | BACKEND_READY |
| ST-FE-007 | FEP-02 | Detalle producto con contrato disponible | P1 | S | BACKEND_READY |
| ST-FE-008 | FEP-02 | Draft de carrito sin precio/totales autoridad | P1 | M | PRODUCT_DECISION_REQUIRED |
| ST-FE-009 | FEP-02 | Crear orden con productId/cantidad exclusivamente | P0 | M | BACKEND_READY |
| ST-FE-010 | FEP-02 | Historial y detalle de orden propia | P0 | M | BACKEND_READY |
| ST-FE-011 | FEP-02 | Placeholder checkout y comunicación de pendiente | P2 | S | DG-15 |
| ST-FE-012 | FEP-02 | Cancelación/envío/pago real cuando existan contratos | P0 | L | DG-13/DG-15 |
| ST-FE-013 | FEP-03 | Mostrar estado técnico sin fijar perfil de negocio | P1 | S | DG-17 |
| ST-FE-014 | FEP-03 | Vista mi red/directos con datos mínimos | P0 | M | BACKEND_READY |
| ST-FE-015 | FEP-03 | Ancestros/descendientes con drill-down y límites | P1 | M | BACKEND_READY |
| ST-FE-016 | FEP-03 | PII/conteos/branch cuando contrato y roles existan | P0 | L | DG-17 / FR-API-GAP-002 |
| ST-FE-017 | FEP-04 | Inventario y aprobación de activos/marca/licencias | P1 | M | PRODUCT_DECISION_REQUIRED |
| ST-FE-018 | FEP-04 | Tokens y componentes accesibles | P1 | L | FR-DG-03 |
| ST-FE-019 | FEP-04 | Sitio público/contenido legal según frontera aprobada | P2 | M | FR-DG-02 |
| ST-FE-020 | FEP-04 | Migrar o retirar búsqueda `/viajero/search` por decisión | P2 | M | FR-API-GAP-012 |
| ST-FE-021 | FEP-05 | Contrato/UI de qualification tras reglas aprobadas | P0 | L | DG-01/DG-03/DG-11 |
| ST-FE-022 | FEP-05 | Rewards calculados por backend | P0 | L | DG-02/DG-04/DG-06–DG-09 |
| ST-FE-023 | FEP-05 | Historial de commission/ledger | P0 | L | DG-04/DG-12 |
| ST-FE-024 | FEP-05 | Settlement/payout/payment status | P0 | XL | DG-05/DG-10/DG-15 |
| ST-FE-025 | FEP-05 | Reportes/export sólo con contrato y permisos | P1 | L | BUSINESS_RULE_BLOCKED |
| ST-FE-026 | FEP-05 | Pruebas de escenarios financieros aprobados | P0 | L | BUSINESS_RULE_BLOCKED |
| ST-FE-027 | FEP-06 | Matriz de capability y navegación admin | P0 | M | BACKEND_BLOCKED |
| ST-FE-028 | FEP-06 | Administración de catálogo/perfiles sólo con APIs | P1 | L | BACKEND_BLOCKED |
| ST-FE-029 | FEP-06 | Correcciones de red, aprobación financiera y reportes | P0 | XL | Role matrix + business rules |
