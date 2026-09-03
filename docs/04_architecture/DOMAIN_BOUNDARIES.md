# Límites de dominio

| Contexto | Posee | No posee | Interfaces principales |
|---|---|---|---|
| Identity/authorization | identidad, roles, policies, ownership | reglas de compensación | actor/contexto de acceso |
| Commerce/payments | orders, payment intent, confirmación verificable | ledger, cálculo de comisión | payment confirmed/reversed |
| Network | sponsor, ancestor, descendant, branch | volumen y porcentaje | consultas de genealogía versionadas/snapshot |
| Rewards | activation, volume, qualification, plan y cálculo | ejecución de payout | snapshots y resultados de comisión |
| Finance | ledger, approval, settlement, payout, tax | reglas de elegibilidad | movimientos y estados financieros |
| Support | reporting, notifications, audit read-model | fuente financiera canónica | eventos/proyecciones |
| Migration | staging, transformación y conciliación | operación productiva | reportes de migración |

La regla de compensación se resuelve en Rewards mediante versión de plan, rol, elegibilidad y base aprobada. Finance recibe un resultado inmutable y no reinterpreta porcentajes. Authorization se aplica antes de application services y también en políticas de recurso.
