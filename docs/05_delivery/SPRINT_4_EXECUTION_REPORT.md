# Sprint 4 — Affiliation, Network & Genealogy Foundation

**Clasificación documental:** `HISTORICAL_SPRINT_SNAPSHOT`. Describe el cierre de Sprint 4; el estado vigente posterior está en los informes de Sprint 5 y en `DECISION_REGISTER.md`.

**Base:** `69c0dc6` (`dev`, Sprint 3 merged)
**Branch:** `feat/plan3-sprint4-network-genealogy`
**Estado:** DONE
**PR:** #7 `feat/plan3-sprint4-network-genealogy -> dev` (squash merge `9e4c9cc`, 2026-09-03)

## Alcance

- V7 PostgreSQL/Flyway: perfiles de negocio, enlace técnico actor-perfil, miembros, sponsor y closure table.
- Afiliación explícita por aplicación; ningún login OIDC o ID legacy se convierte automáticamente.
- Sponsor inicial transaccional, sin self-sponsor, ciclo, duplicado o cambio libre; la política de re-parenting queda abierta como DG-18.
- Consultas seguras de red propia: miembro, directos, ancestros y descendientes con límite 1..100.
- Auditoría sanitizada: `BUSINESS_PROFILE_LINK`, `NETWORK_MEMBER_CREATED`, `SPONSOR_ASSIGNED`, `SPONSOR_CHANGE_DENIED`, `NETWORK_CYCLE_REJECTED`.
- `src/.gitignore` restringe `out/` a la raíz de `src`, evitando ignorar pruebas ubicadas bajo paquetes `out`.
- Validación final: 124 pruebas locales, 0 fallos, 0 errores y 4 omitidas por Npipe/Testcontainers; CI Linux: 124, 0 fallos, 0 errores, 0 omitidas, incluida integración PostgreSQL V7 de red.

## Business rules blocking next phase at Sprint 4 close

**Network structural foundation:** VALIDATED. **Genealogy closure:** VALIDATED. **Qualification:** NOT IMPLEMENTED. **Volume:** NOT IMPLEMENTED. **Compensation:** NOT IMPLEMENTED.

En este corte histórico, DG-01/DG-03/DG-06..DG-11, DG-17, DG-18 y DG-19 seguían abiertas. Sprint 5 implementó después una foundation versionada de activation, qualification y volume sin autorizar efectos productivos. Comisiones, ledger, liquidación y pagos continúan fuera de alcance; sponsor re-parenting sigue bloqueado en código y la autoridad de provisioning permanece pendiente.
