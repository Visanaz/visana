# Arquitectura cloud objetivo

Propuesta de despliegue gestionado y contenido para pyme: servicio Spring en Cloud Run, PostgreSQL en Cloud SQL, imágenes en Artifact Registry, secretos en Secret Manager, logs/métricas en Cloud Logging/Monitoring y archivos en Cloud Storage si el alcance lo requiere. Cloud Run permite ejecutar servicios HTTP en contenedores gestionados; Cloud SQL ofrece PostgreSQL gestionado. [Cloud Run](https://docs.cloud.google.com/run/docs/overview/what-is-cloud-run), [Cloud SQL PostgreSQL](https://docs.cloud.google.com/sql/docs/postgres).

## Implementación del primer pipeline DEV — 26 de septiembre de 2026

La propuesta anterior no acredita provisión de todos esos recursos. La guía operativa vigente está en el [README](../../README.md); las evidencias históricas y el legacy permanecen inmutables.

### Evidencia del incidente histórico

| Elemento | Evidencia comprobada |
|---|---|
| Commit | `040ec3f929c15e546571f48fa425736dd83c3b36` |
| Ejecución / intento | [36188945156](https://github.com/Visanaz/visana/actions/runs/36188945156), intento 1, 2026-09-25 20:58:54–21:02:09 UTC |
| Imagen | `us-central1-docker.pkg.dev/<proyecto enmascarado>/visana-repo/visana-api-dev:<SHA>` |
| Digest publicado | `sha256:b242ce81b91692d20e5cc5e80de44482e90b174689c444cf08b1671ba4eaa4a4` |
| Servicio / región | `visana-api-dev` / `us-central1` |
| Revisión fallida | `visana-api-dev-00001-dwp` |
| Resultado | Construcción/publicación exitosa; despliegue fallido por mensaje genérico PORT=8080 |
| Excepción de aplicación / cuenta runtime / proyecto | PENDIENTE: no obtenidos de metadata ni logs internos de revisión |

### Clasificación de hallazgos

- **A — causa respaldada por logs de aplicación:** pendiente. Los logs de Actions no contienen la cadena de excepciones del contenedor.
- **B — defectos comprobados en archivos:** puerto fijo 8084 pese a PORT=8080; CD independiente de CI/contrato; Docker recompilaba sin pruebas; ausencia de contexto Docker restringido; tag sin verificación de digest; flag que imponía exposición pública. La corrección hace explícitos puerto, gates, artefacto, digest, preservación IAM y comprobación autenticada.
- **C — hipótesis pendiente de configuración efectiva:** DataSource/Flyway/OIDC y conectividad SQL. El YAML anterior solo añadía perfil y contraseña, pero eso no demuestra ausencia de otras variables ya existentes en el servicio.
- **D — mejora externa pendiente:** sustituir la clave histórica de despliegue por WIF y adoptar Secret Manager si el servicio no lo utiliza. No se crearon recursos, claves, secretos o permisos en esta tarea.

### Prerrequisitos externos antes de integrar el PR

1. Cristian debe identificar el proyecto y facilitar acceso de lectura: `run.services.get`, `run.revisions.get` sobre servicio/revisión y `logging.logEntries.list` para el intervalo del incidente. No se requiere Owner/Editor ni una clave nueva.
2. Verificar instancia reportada `visana-db-dev`, región, connectionName, BD y usuario PostgreSQL. Para metadata SQL se necesitan permisos de lectura de instancia y listado de bases/usuarios; no se necesitan sus contraseñas para documentar la identidad.
3. Confirmar la ruta TCP PostgreSQL/VPC/TLS antes de cargar `DB_URL` y `DB_USER`. El código no implementa Java Connector; esa alternativa requiere decisión/evidencia adicional y no se agregó por hipótesis.
4. Cargar mediante los canales seguros las variables y el secreto de cliente técnico de salud enumerados en README. Confirmar la cuenta runtime actual y sus permisos sobre recursos concretos. El workflow falla tempranamente mientras falten fuentes.
5. Evaluar CI del commit publicado y compatibilidad Flyway/esquema antes de integrar. Integración del PR y aceptación de reglas de negocio son decisiones distintas.

El CD conserva secretos/variables ajenos mediante merge, la cuenta runtime y la exposición actual. Si descubre overrides o referencias incompatibles, falla antes del despliegue; no los elimina unilateralmente. La salud HTTP requiere 200/UP con ambas autenticaciones y no considera satisfactorio un 401. No hay evidencia de despliegue cloud exitoso de esta corrección hasta que se integre y ejecute el flujo aprobado.

| Ambiente | Propósito | Controles |
|---|---|---|
| Local | desarrollo aislado | secretos locales separados, datos sintéticos |
| DEV | integración temprana | DB/identity separados, observabilidad básica |
| QA | validación P0 | datos controlados, pruebas contract/E2E aprobadas |
| PROD | operación | cuentas de servicio mínimas, alertas, backups y auditoría |

STAGING independiente queda por decidir según riesgo y capacidad. No se crean recursos, pipelines ni contenedores en esta fase.
