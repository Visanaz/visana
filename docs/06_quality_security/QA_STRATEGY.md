# Estrategia QA

Objetivos propuestos: >=70% de cobertura de core financiero crítico y >=60% backend general, sin usar porcentaje como sustituto de trazabilidad. P0 exige unit, integration, contract, E2E y security cuando aplique.

| Nivel | Propósito |
|---|---|
| Unit | dinero, reglas, estados, resolver, políticas |
| Integration | PostgreSQL/adapters/transacciones; evaluar Testcontainers |
| Contract | API, payment gateway, identity y webhooks |
| E2E | pago confirmado→compensación→ledger→payout en datos controlados |
| Security | ownership, RBAC, JWT, validación, rate limiting y auditoría |

Quality gates: build reproducible, pruebas requeridas verdes, migración revisada cuando exista, SAST/dependency scan, contrato OpenAPI, logs sin secretos y revisión de decisiones/gaps. Los 102 resultados Surefire históricos no satisfacen este plan.
