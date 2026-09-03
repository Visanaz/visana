# Decision gates abiertos - compensacion

## Reevaluacion de gates historicos

| Gate | Tema | Estado | Evidencia que impide cierre |
|---|---|---|---|
| DG-01 | Activacion | SIGUE_ABIERTO | PDF mes de compra/30 dias; SQL 30; AS-IS 29/mes; Java parametrico sin caller. |
| DG-02 | Bono distribuidor | SIGUE_ABIERTO | PDF 10%; SQL tiers 3%/5%; Java no localizado. |
| DG-03 | Team sales | SIGUE_ABIERTO | PDF/JSON fijan umbrales; sale types tienen `counts_for_team_sales=0`; no writer/read adapter Java. |
| DG-04 | Aprobacion comisiones | SIGUE_ABIERTO | AS-IS usa pending/approved/paid; Java `Commission` CALCULATED/REVERSED sin puente a ledger. |
| DG-05 | Ordenes de pago | SIGUE_ABIERTO | tabla/historico existen; Java Payout no tiene persistencia, caller ni calendario. |
| DG-06 | Pools/consistencia | SIGUE_ABIERTO | PDF y semillas existen; ningun calculador/distribucion/ledger Java localizado. |

## Gates nuevos

| Gate | Decision requerida | Estado |
|---|---|---|
| DG-07 | Premio adicional 1% L1-L7: regla independiente, pool o duplicacion no permitida | SIGUE_ABIERTO |
| DG-08 | Restriccion de rango/rama: comparador, inicio, reactivacion e historico | SIGUE_ABIERTO |
| DG-09 | Base de calculo y precedencia: bruto/neto/volumen; default/rol/calificacion/pool | SIGUE_ABIERTO |
| DG-10 | Devoluciones/anulaciones: evento, reverso, impacto en volumen/calificacion/pago | PARCIALMENTE_RESUELTO por `Commission.reverse()` aislado |
| DG-11 | Mes calendario frente a 30 dias corridos | SIGUE_ABIERTO |
| DG-12 | Regla tributaria parametrizable | SIGUE_ABIERTO |
| DG-13 | "Mas de 3 productos": unidad de conteo, tipos, envio y excepciones | SIGUE_ABIERTO |

## DG-01A - evaluacion conceptual sin decidir

El PDF contiene dos expresiones no equivalentes: "lo activa por el mes" y "la activacion dura 30 dias". Para compras el 31 de enero o 15 de agosto, las fechas de vencimiento propuestas en el master prompt no pueden resolverse desde el PDF, SQL o Java actual: Java recibe el numero de dias y fecha externa, pero no construye calendario ni lo persiste. No se ha elegido fecha alguna.

## Top 10 brechas P0 de negocio

1. Regla canonica de activacion y recompra (DG-01/DG-11).
2. Porcentaje y elegibilidad de invitacion de distribuidor (DG-02).
3. Writer y definicion de team sales (DG-03).
4. Fuente mantenible de plan/default/override por rol (DG-09).
5. Semantica de nivel 8: bolsa equitativa o porcentaje individual (DG-09).
6. Premio adicional 1% por niveles 1-7 (DG-07).
7. Restriccion por rango/rama (DG-08).
8. Origen verificable de una orden pagada antes de disparar compensacion.
9. Commission -> ledger -> aprobacion -> liquidacion -> pago (DG-04/DG-05).
10. Tratamiento de devoluciones, anulaciones y reversos en volumen/pagos (DG-10).

## Riesgo de conciliacion de datos, no bug de produccion

SRC-003 contiene siete fechas `0000-00-00 00:00:00`, un `distributor_monthly_volumes.period='1969-12'` y una venta con fecha sentinela. Tablas/campos observados: `distributor_monthly_volumes.period`, sus fechas, `ledger_transactions` y `sales.createdAt`. Cualquier proceso futuro de migracion/conciliacion debera validarlos; en esta fase no se corrigen ni se presume que el dump sea produccion vigente.

## Componentes binarios historicos sin fuente equivalente

`NetworkNodeRepository`, `NetworkNodePersistenceAdapter`, `CommissionPlanJpaEntity`, `CommissionPlanLevelJpaEntity`, `CommissionPlanPersistenceAdapter`, `CommissionPlanSpringDataRepository`, `DistributorVolumeJpaEntity`, `DistributorVolumeSpringDataRepository`, `GenealogyPersistenceAdapter`, `NetworkNodeJpaEntity`, `NetworkNodeSpringDataRepository` y `QualificationPersistenceAdapter` existen como `.class` en `target/classes`, pero no como fuente equivalente en HEAD. Son evidencia historica SRC-005, no sustituto de implementacion.
