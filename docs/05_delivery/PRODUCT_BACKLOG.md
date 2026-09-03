# Backlog técnico/producto propuesto

| Epic | Features | US/TS representativas | Prioridad / dependencia |
|---|---|---|---|
| EPIC-001 Foundation | FEAT-001 estructura modular; FEAT-002 build/CI/observabilidad | TS-0001..0003 | P0; GAP-021/022 |
| EPIC-002 Identity & access | FEAT-003 identidad; FEAT-004 ownership/RBAC | TS-0004, US-0001..0002 | P0; DG-14 |
| EPIC-003 Commerce & payments | FEAT-005 orders; FEAT-006 payment gateway | TS-0005, US-0003..0004 | P0; GAP-011 |
| EPIC-004 Network & rewards core | FEAT-007 genealogy; FEAT-008 volume/qualification | TS-0006..0008, US-0005 | P0; DG-03/GAP-021 |
| EPIC-005 Compensation | FEAT-009 plan resolver; FEAT-010 Unilevel | TS-0009..0011, US-0006 | P0; DG-01/02/07/08/09/11 |
| EPIC-006 Finance | FEAT-011 ledger; FEAT-012 settlement/payout/tax | TS-0012..0014, US-0007 | P0; DG-04/05/10/12 |
| EPIC-007 Data migration | FEAT-013 staging/validation; FEAT-014 reconciliation | TS-0015..0017 | P0; GAP-018..020 |
| EPIC-008 Experience/support | FEAT-015 API contracts/frontend handoff; FEAT-016 reporting/notifications | TS-0018, US-0008..0010 | P1/P2; DG-16 |

**Totales:** 8 EPIC, 16 FEATURE y 28 US/TS identificadas (10 US, 18 TS). Complejidad y criterios se detallan en la hoja de ruta; no se estiman horas ni precio.

Condición común de aceptación: BR/DG trazado, policy de seguridad aplicable, auditoría/correlación, pruebas requeridas y documentación. Un item bloqueado no autoriza decidir su regla.

## Actualización Sprint 3

`FEAT-005` queda **PARCIALMENTE_IMPLEMENTADA**: catálogo autenticado de lectura y orden Plan 3 con ownership explícito. `TS-COM-003` (idempotencia de creación) permanece diferida por contrato/reintento/reconciliación no aprobados. `FEAT-006` y cualquier efecto financiero continúan bloqueados por GAP-011/012 y DG-15.
