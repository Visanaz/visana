# Estrategia de datos objetivo

**Propuesta:** PostgreSQL como destino Plan 3, sujeto a ADR-002 y migración aprobada. El modelo no reutiliza directamente la semántica ambigua del dump legado.

| Área | Modelo objetivo conceptual |
|---|---|
| Dinero | `Money(amount, currency)`, escala y redondeo centralizados; COP inicial, no rígido a una sola moneda. |
| Planes | `CompensationPlan`, `PlanVersion`, `Rule`, vigencia y resultado aplicado. |
| Red | sponsor y representación genealogía seleccionada; snapshots para cálculo. |
| Volumen | `VolumeEntry` trazable a venta/reversal; `PeriodVolume`, personal y equipo separados. |
| Calificación | `QualificationRule` y `QualificationSnapshot` por periodo. |
| Finanzas | ledger append-oriented, settlement, payout order, impuestos y reconciliación. |
| Auditoría | actor, recurso, before/after permitido, correlación y fuente. |

Toda llave de legado, estado, fecha sentinela o total financiero se trata en staging/conciliación; nunca como supuesto de modelo destino.
