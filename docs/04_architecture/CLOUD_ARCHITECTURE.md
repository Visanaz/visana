# Arquitectura cloud objetivo

Propuesta de despliegue gestionado y contenido para pyme: servicio Spring en Cloud Run, PostgreSQL en Cloud SQL, imágenes en Artifact Registry, secretos en Secret Manager, logs/métricas en Cloud Logging/Monitoring y archivos en Cloud Storage si el alcance lo requiere. Cloud Run permite ejecutar servicios HTTP en contenedores gestionados; Cloud SQL ofrece PostgreSQL gestionado. [Cloud Run](https://docs.cloud.google.com/run/docs/overview/what-is-cloud-run), [Cloud SQL PostgreSQL](https://docs.cloud.google.com/sql/docs/postgres).

| Ambiente | Propósito | Controles |
|---|---|---|
| Local | desarrollo aislado | secretos locales separados, datos sintéticos |
| DEV | integración temprana | DB/identity separados, observabilidad básica |
| QA | validación P0 | datos controlados, pruebas contract/E2E aprobadas |
| PROD | operación | cuentas de servicio mínimas, alertas, backups y auditoría |

STAGING independiente queda por decidir según riesgo y capacidad. No se crean recursos, pipelines ni contenedores en esta fase.
