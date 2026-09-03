# Arquitectura objetivo Plan 3

**Decisión propuesta:** monolito modular Java 21/Spring Boot, con límites de dominio y puertos/adaptadores. No se adopta microservicios ni Spring Modulith en esta fase; su valor debe validarse cuando haya pruebas de modularidad, integración externa o escala.

```text
Angular futuro / integraciones
  -> API /api/v1 + error, correlation e idempotency conventions
  -> application services / policies
  -> domain modules
  -> ports
  -> adapters: PostgreSQL, identity, payment gateway, notification, storage
  -> Cloud SQL / servicios externos
```

Los controllers no calculan reglas financieras; repositorios no deciden reglas; adapters no resuelven porcentajes. Cada operación financiera requiere actor, ownership, correlation ID, idempotency key, timestamp y audit trail.

## Módulos propuestos

| Grupo | Módulos | Responsabilidad |
|---|---|---|
| Platform | identity, authorization, audit | actor, roles, ownership, trazabilidad |
| Commerce | customer, catalog, orders, payments | intención/orden/pago verificable |
| Network | affiliation, genealogy | sponsor, árbol, relaciones derivadas |
| Rewards | activation, volume, qualification, compensation | snapshots y cálculo versionado |
| Finance | ledger, settlement, payout, tax | historia financiera, aprobación y pago |
| Support | reporting, notifications | proyecciones y comunicaciones |
| Transition | data_migration | staging, validación y conciliación |

Los módulos históricos binarios no son implementaciones disponibles; sus responsabilidades se cubren por diseño y backlog, no copiando nombres o bytecode.
