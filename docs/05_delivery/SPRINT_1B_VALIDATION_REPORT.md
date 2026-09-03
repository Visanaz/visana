# Sprint 1B — Core Platform Validation

**Base:** `5a0c63a` (`dev`, Sprint 1A merged)
**Rama:** `test/plan3-sprint1b-core-validation`

## Evidencia local

| Validación | Resultado | Evidencia |
|---|---|---|
| Docker CLI / Compose | PASS | Docker 29.7.2 y Compose v5.4.0 disponibles |
| Compose estático | PASS | `docker compose -f compose.postgres.yml config` válido |
| PostgreSQL Compose | PASS | contenedor `postgres:16-alpine` listo con `pg_isready` |
| Testcontainers local | BLOCKED_LOCAL_TESTCONTAINERS | el proveedor Npipe de Testcontainers recibió respuesta Docker no utilizable; no se sustituyó por H2 |
| Flyway/Testcontainers local | PENDING_CI | la prueba se omitió limpiamente al no detectar entorno válido |

## Pruebas añadidas

- `PostgreSqlAuditEventIntegrationTest`: PostgreSQLContainer, Flyway V1/V2 y persistencia/lectura de auditoría sanitizada.
- `FailClosedAccessPolicyTest`: una relación ausente o denegada no concede acceso; sólo una decisión explícita permite acceso.

La suite local terminó con 109 pruebas, 0 fallos, 0 errores y 1 omitida (Testcontainers bloqueado localmente). La ejecución CI es necesaria para demostrar el contenedor real en runner Linux.

## Límites preservados

Ownership funcional continúa `BLOCKED_BY_SOURCE`: no existe evidencia de mapeo JWT actor a afiliado/usuario ni matriz de roles por recurso. No se añadieron reglas financieras, provider de pago, frontend, despliegue, secretos ni limpieza de `target/`.
