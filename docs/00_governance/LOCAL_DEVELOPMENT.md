# Desarrollo local — VISANA

## Límite de repositorio

Este repositorio es el backend de VISANA: Java/Spring, PostgreSQL/Flyway, reglas de negocio y el contrato OpenAPI canónico. El código Angular, Node/npm, Playwright y su CI viven en `Visanaz/visana-front`; el frontend no se desarrolla ni se valida aquí. La futura integración consume el snapshot OpenAPI aprobado desde el backend.

## Prerrequisitos

- JDK 21.
- Docker Desktop sólo cuando se habilite el entorno PostgreSQL de Sprint 1.
- Variables locales definidas fuera del repositorio. Usar `.env.example` únicamente como plantilla sin secretos.

## Verificación reproducible

En PowerShell, con JDK 21 disponible:

```powershell
.\mvnw.cmd --batch-mode clean verify
```

Para preservar los artefactos históricos versionados bajo `target/`, una verificación local puede enviar su salida a una ruta temporal mediante `-Dvisana.build.directory=<ruta-temporal>`.

## Perfiles y datos

Los perfiles `local`, `test`, `dev`, `qa` y `prod` están definidos para separar configuración. MySQL se conserva como fuente legacy; PostgreSQL es el target de Plan 3 y no implica migración productiva.

## PostgreSQL local (Sprint 1)

Usar `docker compose -f compose.postgres.yml up -d` para una instancia PostgreSQL local aislada. Los valores de `.env.example` son placeholders seguros. Las credenciales de DEV, QA y producción permanecen externas al repositorio. Las pruebas específicas de PostgreSQL deben usar Testcontainers, no H2.
# OpenAPI local generation

Generate the canonical OpenAPI snapshot with Java 21:

```text
./mvnw --batch-mode -DskipTests test-compile exec:java -Dexec.args=openapi/visana-api-v1.json
```

The command starts a local, ephemeral contract context containing the real controllers, Springdoc and security configuration. It does not contact production, Keycloak, or a business database. Review and commit an intentional snapshot diff; the `openapi-contract` workflow regenerates it and fails on drift.
