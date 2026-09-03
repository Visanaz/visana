# Sprint 4 — Affiliation, Network & Genealogy Foundation

**Base:** `69c0dc6` (`dev`, Sprint 3 merged)
**Branch:** `feat/plan3-sprint4-network-genealogy`
**Estado:** IMPLEMENTADO_LOCALMENTE — NO_MERGED

## Alcance

- V7 PostgreSQL/Flyway: perfiles de negocio, enlace técnico actor-perfil, miembros, sponsor y closure table.
- Afiliación explícita por aplicación; ningún login OIDC o ID legacy se convierte automáticamente.
- Sponsor inicial transaccional, sin self-sponsor, ciclo, duplicado o cambio libre.
- Consultas seguras de red propia: miembro, directos, ancestros y descendientes con límite 1..100.
- Auditoría sanitizada: `BUSINESS_PROFILE_LINK`, `NETWORK_MEMBER_CREATED`, `SPONSOR_ASSIGNED`, `SPONSOR_CHANGE_DENIED`, `NETWORK_CYCLE_REJECTED`.
- `src/.gitignore` restringe `out/` a la raíz de `src`, evitando ignorar pruebas ubicadas bajo paquetes `out`.

## Business rules blocking next phase

DG-01/DG-03/DG-06..DG-11 y DG-17 siguen abiertas. No se implementan activation, qualification N1-N8, volumen, team sales, rango, porcentajes, comisiones, ledger ni pagos. Sponsor re-parenting está `BLOCKED_BY_BUSINESS_DECISION`.
