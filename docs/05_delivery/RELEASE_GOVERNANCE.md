# Gobierno de releases — VISANA

## CI y promociones

El workflow `build` ejecuta `clean verify` en pull requests y pushes de `dev`, `qa` y `main`. La estrategia es:

| Origen | Destino | Condición | Resultado |
|---|---|---|---|
| Rama de trabajo | `dev` | PR, revisión y CI verde | Integración |
| `dev` | `qa` | PR, CI verde y QA/UAT | Candidato de release |
| `qa` | `main` | PR, QA/UAT y aprobación de producción | Release de producción |

## CD preparado, no activado

La topología prevista es `dev -> DEV`, `qa -> QA` y `main -> production`. No se crea un workflow de despliegue ni se consumen secretos cloud hasta contar con infraestructura aprobada, credenciales gestionadas y comandos de despliegue verificables. Esto evita fallos permanentes por secretos inexistentes y despliegues inferidos.

La producción requerirá un GitHub Environment `production` con aprobación manual si el plan de GitHub lo permite. La configuración efectiva queda pendiente de `BLOCKED_GITHUB_AUTHENTICATION` y de la plataforma autorizada.

## Rollback y etiquetado

Un rollback debe partir de un release identificado de `main`, documentar el incidente y reconciliar cualquier hotfix hacia `dev`. Los tags semánticos se crean sólo al promover una release aprobada de `qa` a `main`; no se crea tag durante Sprint 0 ni mientras no exista release de producción.
