# ADR-010 Cloud Runtime
**Propuesta:** Cloud Run + Cloud SQL PostgreSQL + Secret Manager/Artifact Registry/Logging/Monitoring, sujeto a presupuesto y controles de VISANA. **Contexto:** objetivo Google Cloud y necesidad pyme de operación simple. **Consecuencia:** servicio stateless, base separada por ambiente y secretos fuera del repositorio; no se crean recursos ni CI/CD en esta fase.
