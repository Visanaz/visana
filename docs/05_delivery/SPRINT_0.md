# Sprint 0 propuesto — Foundation

**Estado: DONE (2026-09-02).** Objetivo: hacer reproducible y gobernable la entrega, sin lógica financiera.

| Objetivo | Alcance | Aceptación |
|---|---|---|
| Estructura | límites de módulos y reglas de dependencia | DONE — contrato de salida faltante restaurado para que la fuente compile; no se incorporó código binario ni se alteró lógica de negocio |
| Build/CI | fuente/artefactos, pipeline propuesto, quality gates | DONE — Maven Wrapper y flujo CI de `clean verify`; los binarios históricos de `target` no se usan como fuente |
| Datos | decisión de destino y contrato de staging | BLOCKED — PostgreSQL y el contrato de staging requieren decisiones y validación de entorno; no hay migración funcional en este sprint |
| Seguridad | actor context, ownership policy y secreto externo | PARTIAL — interfaz de autorización, CORS explícito y contraseña de BD por entorno; identidad, roles y ownership efectivos siguen pendientes |
| QA | pirámide, Testcontainers evaluado, P0 test plan | PARTIAL — prueba unitaria de correlación y suite existente ejecutadas; Testcontainers y pruebas de PostgreSQL/Keycloak siguen pendientes |
| Observabilidad | correlación, logs estructurados, health/metrics | DONE — correlación, respuestas de error trazables, logging estructurado y Actuator de salud sin detalles |
| Entorno | Local/DEV/QA/PROD y configuración separada | PARTIAL — plantilla `.env.example` y configuración externa local; no se aprovisionaron ambientes ni infraestructura |

Fuera de Sprint 0: porcentajes, compensación, migraciones, gateway, frontend, cloud real y creación de infraestructura.

Evidencia de ejecución: `docs/05_delivery/SPRINT_0_EXECUTION_REPORT.md`.
