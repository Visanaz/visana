# Sprint 3 — Commerce Core: catálogo y órdenes actor-owned

**Base:** `8067451` (`dev`, Sprint 2 squash merge)
**Branch:** `feat/plan3-sprint3-commerce-orders`
**Estado:** IMPLEMENTADO_LOCALMENTE — NO_MERGED

## Entregado

- Migraciones Flyway V5/V6: categorías, productos, órdenes Plan 3 y líneas, PK/FK, índices, `sku`/`code` únicos, checks de estado, cantidad y montos.
- Catálogo autenticado de lectura, paginado y ordenado; mutaciones bloqueadas sin matriz de roles evidenciada.
- Órdenes Plan 3 de actor vinculado con ownership persistente, cálculo server-side y snapshots de línea.
- Consulta por owner, protección de lectura foránea, `ProblemDetail`, auditoría y correlación reutilizados.
- Pruebas de servicio y web de creación vinculada/no vinculada, denegación sin autenticación y lectura foránea.

## No incluido

No frontend/POS, impuesto, envío, descuento, gateway/webhook, pago, reverso, ledger, liquidación, payout, volumen, calificación, unilevel, pools, sponsor ni comisión. El endpoint heredado de confirmación conserva su semántica previa y sus gates de Sprint 2.

## Evidencia de validación

`mvnw.cmd --batch-mode -Dvisana.build.directory=C:/Users/Kmilo/AppData/Local/Temp/visana-sprint3-final clean verify` terminó con 119 pruebas, 0 fallos, 0 errores y 3 omitidas. Las omisiones son las tres pruebas PostgreSQL/Testcontainers, incluida `PostgreSqlOwnedCommerceIntegrationTest`, por `WINDOWS_TESTCONTAINERS_NPIPE`; CI queda como evidencia autoritativa de esa capa.

GitHub Actions run `33718384087` aprobó `clean verify` en 54 segundos sobre el PR #6. La evidencia CI de ese run no incluye aún `PostgreSqlOwnedCommerceIntegrationTest`: una regla heredada `src/.gitignore` ignoró su ruta `adapter/out` y el archivo se compila localmente pero no fue publicado en ese commit. La prueba se añade forzadamente al control de versiones y requiere una nueva ejecución CI antes de declararla validada en PostgreSQL. Las anotaciones del runner son avisos externos de deprecación para `actions/checkout@v4` y `actions/setup-java@v4`; no son fallos del build ni de la aplicación.
