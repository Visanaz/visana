# Desarrollo local — VISANA

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

Los perfiles `local`, `test`, `dev`, `qa` y `prod` son el objetivo de configuración de Sprint 1. No están todos implementados aún. MySQL se conserva como fuente legacy; PostgreSQL target, Compose y Testcontainers requieren la foundation autorizada de Sprint 1 y no se infieren en este documento.

