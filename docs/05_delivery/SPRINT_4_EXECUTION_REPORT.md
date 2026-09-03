# Sprint 4 — Affiliation, Network & Genealogy Foundation

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

## Business rules blocking next phase

**Network structural foundation:** VALIDATED. **Genealogy closure:** VALIDATED. **Qualification:** NOT IMPLEMENTED. **Volume:** NOT IMPLEMENTED. **Compensation:** NOT IMPLEMENTED.

DG-01/DG-03/DG-06..DG-11, DG-17, DG-18 y DG-19 siguen abiertas. No se implementan activation, qualification N1-N8, volumen, team sales, rango, porcentajes, comisiones, ledger ni pagos. Sponsor re-parenting está `BLOCKED_BY_BUSINESS_DECISION`; affiliation provisioning authority queda pendiente de VISANA.
