# Informe de ejecución — Plan 3, Sprint 0 Foundation

**Fecha:** 2026-09-02
**Rama:** `feat/plan3-sprint0-foundation`
**Base inicial:** `5f241fe0d1a47cfc783f866408219da38e085ab2` (`main`)

## Propósito y límite aplicado

Se ejecutó exclusivamente la foundation técnica autorizada para Sprint 0. No se modificaron porcentajes, activación, recompra, distribuidores, reglas unilevel, nivel 8, team sales, pools, calificaciones, rangos, aprobación, estados de ledger, liquidaciones, pagos, impuestos, envíos, proveedores de pago ni migraciones funcionales. La simulación de pago preexistente no fue alterada.

## Preflight y reproducibilidad

El baseline de la rama `main` no compilaba: la fuente referenciaba `NetworkNodeRepository` desde `CreateOrderService`, `CreateNetworkNodeService` y su configuración, pero el contrato no estaba presente en `src/main`. El intento de build terminó con 8 errores de compilación asociados a esa ausencia.

Se añadió Maven Wrapper en modo only-script con Maven 3.9.9. Se utilizó JDK 21 local. Los builds se ejecutan con directorio de salida temporal para no modificar los binarios históricos versionados bajo `target/`; esos binarios no fueron tomados como fuente ni decompilados.

Verificación de la foundation:

```text
mvnw.cmd --batch-mode -Dvisana.build.directory=C:/Users/Kmilo/AppData/Local/Temp/visana-sprint0-build clean verify
Resultado: BUILD SUCCESS
Tests: 104 ejecutados, 0 failures, 0 errors, 0 skipped
```

## Cambios implementados

| Área | Entrega | Estado |
|---|---|---|
| Contrato de compilación | Puerto `NetworkNodeRepository` mínimo, derivado de los usos ya existentes de la fuente | DONE |
| Configuración | URL, usuario y contraseña de BD por variables de entorno; plantilla `.env.example`; SQL de JPA configurable | DONE |
| Seguridad web | CORS explícito y habilitado en la cadena de Spring Security; sin orígenes comodín | DONE |
| Autorización | Interfaz `AuthorizationPolicy` como seam para ownership y acciones sensibles, sin reglas ni implementación financiera | PARTIAL |
| API | `ProblemDetail` con identificador de correlación y respuesta 500 sanitizada | DONE |
| Observabilidad | Filtro `X-Correlation-ID`, MDC, logging estructurado y Actuator limitado a `health,info`, sin detalles de salud | DONE |
| Auditoría | Tipo de dominio `AuditEvent` para evento trazable; no se cableó persistencia ni flujo financiero | PARTIAL |
| QA | Pruebas unitarias del filtro de correlación, más suite preexistente | DONE |
| CI | Workflow de GitHub Actions para JDK 21 y `clean verify` en push a `main` y pull requests | DONE |

## Dependencias y entorno

Se agregó únicamente `spring-boot-starter-actuator`, sin versiones fijadas fuera del parent de Spring Boot. No se agregaron Firebase, Testcontainers, controlador PostgreSQL, componentes cloud, Dockerfile ni Docker Compose nuevos. La configuración actual mantiene el motor existente como fallback local y deja la preparación efectiva de PostgreSQL pendiente de una decisión y de pruebas de integración.

## Deuda, advertencias y pendientes

La verificación es exitosa, con advertencias preexistentes o de compatibilidad que no bloquean Sprint 0: uso no comprobado de operaciones en un convertidor de Keycloak, deprecación de `@MockBean`, auto-adjunción dinámica de Mockito y versión H2 más reciente que la validada por Flyway.

Quedan abiertos: provisión y pruebas con PostgreSQL, decisiones de identidad/roles/audiencia de Keycloak, implementación aprobada de ownership, persistencia/publicación de eventos de auditoría, Testcontainers, despliegue de ambientes, y todos los decision gates de negocio y finanzas. Ninguno se cerró por inferencia en este sprint.

## Trazabilidad de alcance

- Backlog, roadmap, ADR y matrices previos: `docs/01_audit` a `docs/06_quality`.
- Estado resumido de Sprint 0: `docs/05_delivery/SPRINT_0.md`.
- Ejecución reproducible local: Maven Wrapper, JDK 21 y las variables documentadas en `.env.example`.

## Consolidación Git — Master Prompt 05

La identidad Git se configuró únicamente en este repositorio para `Visanaz`. Sprint 0 quedó consolidado en commits locales de build/foundation, seguridad/API, observabilidad, CI y documentación. Las ramas locales `main`, `dev` y `qa` parten del baseline `5f241fe`; Sprint 0 permanece aislado en `feat/plan3-sprint0-foundation`.

El acceso remoto está bloqueado como `BLOCKED_GITHUB_AUTHENTICATION`: GitHub CLI no está instalado y Git Credential Manager no expone una sesión de `Visanaz`. El remoto histórico del proveedor se preservó localmente como `upstream`; no existe un nuevo `origin` hasta poder verificar el repositorio oficial. No se usó otra cuenta, no hubo push, PR ni merge. Por esa dependencia, `dev` no contiene Sprint 0 y Sprint 1 no se inicia.
