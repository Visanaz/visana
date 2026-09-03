# Informe de ejecución — Plan 3, Sprint 1 Core Platform

**Rama:** `feat/plan3-sprint1-core-platform`
**Base:** `50254723ce816b626a0e4f9533b79c7f29ddf494` (`dev`)

## Alcance ejecutado

- PostgreSQL quedó preparado para desarrollo y pruebas mediante driver, Compose local y Testcontainers PostgreSQL como dependencia de prueba. MySQL se conserva sin cambios como fuente legacy.
- Flyway incorpora `V2__create_platform_audit_events.sql`, únicamente para una bitácora append-only de plataforma.
- Se añadió `AuditEventWriter` persistente con metadata JSON sanitizada: excluye claves relacionadas con contraseñas, tokens, secretos, autorización, tarjetas y cuentas bancarias.
- Se agregaron perfiles `local`, `test`, `dev`, `qa` y `prod`; los secretos continúan siendo obligatoriamente externos.
- Se endurecieron cabeceras HTTP y se restringieron explícitamente endpoints de Actuator sensibles.

## Ownership y autorización

`AuthorizationPolicy` sigue como foundation. No se implementó una política efectiva de ownership de orden, confirmación de pago o patrocinador porque la fuente no demuestra el mapeo entre el actor JWT (`sub`/usuario) y `affiliate_id`, ni una matriz de roles por recurso. Inferir esa equivalencia contradiría GAP-014, GAP-015, GAP-016 y DG-14. Este alcance queda `PARTIAL/BLOCKED_BY_SOURCE`, sin modificar reglas comerciales o financieras.

## Verificación

El baseline era 104 pruebas verdes. Tras añadir la prueba de sanitización de auditoría: 105 pruebas, 0 failures, 0 errors y 0 skipped mediante Maven Wrapper con JDK 21.

**Estado posterior Sprint 1A:** `FOUNDATION_MERGED`. **Estado de validación Sprint 1B:** `VALIDATION_COMPLETED`: CI ejecutó PostgreSQL/Testcontainers, Flyway V1/V2 y persistencia de auditoría con 109 pruebas verdes. La validación se detalla en `SPRINT_1B_VALIDATION_REPORT.md`; su PR a `dev` permanece abierto, pendiente de revisión PM y sin merge.

## Exclusiones confirmadas

No se modificaron compensación, activación, team sales, pools, nivel 8, ledger, settlement, payout, impuestos, proveedor de pago, frontend, migración productiva ni Decision Gates. No se retiraron los binarios históricos bajo `target/`.
