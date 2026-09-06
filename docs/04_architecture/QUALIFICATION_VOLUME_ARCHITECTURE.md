# Qualification and Volume Architecture — Sprint 5 foundation

## Alcance

Esta foundation modela evaluación y trazabilidad. No calcula ni autoriza comisiones, pools, impuestos, liquidaciones o pagos. No expone API pública nueva.

## Flujo

```text
OrderPaidEvent(paymentConfirmedAt)
  -> ActivationPolicy + BusinessRuleVersion
  -> ActivationWindow

SaleEvidence(monetary components + lifecycle fact)
  -> QualifyingSalePolicy                 [regla pendiente]
  -> QualificationBasePolicy              [DG-09 pendiente]
  -> genealogy_closure subtree
  -> TeamSalesAggregator
  -> VolumeResult(period + ruleVersion + evidence)
  -> active directs / indirect-count policy
  -> QualificationEvaluatorService
  -> QualificationResult(ruleVersion + evidence + business explanation)
```

## Límites y reutilización

- Se reutiliza `OrderPaidEvent`; ahora transporta el instante técnico `paymentConfirmedAt` generado por un `Clock` UTC inyectado.
- Se reutiliza `genealogy_closure` de Sprint 4 mediante `ClosureTableGenealogyReader`. No hay segundo algoritmo de recorrido de grafo.
- Se reutilizan `LevelQualificationRule`, `NetworkRequirement`, `VolumeRequirement`, `Money` y el evaluador de máximo nivel existente.
- `Period` representa actualmente `YearMonth`; no puede expresar todas las alternativas abiertas de DG-11. `QualificationPeriod` es el rango temporal especializado, con inicio inclusivo y fin exclusivo, producido por `QualificationPeriodPolicy`.
- El antiguo cálculo de activación por cantidad de días se retira del evaluador de calificación. La activación calendario vive en `ActivationCalculator`.

## Tiempo y activación

- Eventos y persistencia usan `Instant`/UTC.
- La aritmética de calendario se ejecuta en el `ZoneId` explícito de la política.
- Foundation provisional: `America/Bogota`, un mes calendario y aniversario a igual hora local.
- `END_OF_ANNIVERSARY_DAY` existe como capacidad técnica, pero no es la regla seleccionada ni aprobada.
- `PENDING_OFFICIAL_CONFIRMATION` rechaza una recompra solapada para impedir que el sistema invente el nuevo inicio.

## Volumen y Team Sales

`SaleMonetaryComponents` conserva importes distintos. `QualifyingSalePolicy` decide elegibilidad y `QualificationBasePolicy` decide el componente o fórmula. Los tests usan estados sintéticos explícitos; producción no asume `PAID`, `CONFIRMED` ni `DELIVERED`.

`TeamSalesAggregator` recibe el miembro y los IDs del subárbol de cierre. Incluye siempre al propio miembro y todos los descendientes suministrados, filtra por período/política y genera:

- volumen personal monetario;
- volumen de equipo monetario;
- base de calificación monetaria;
- total de ajustes/reversos;
- referencias de evidencia.

El ajuste asociado a una evidencia permite excluir o contrarrestar contribuciones sin implementar economía de reverso de comisiones.

## Calificación

Los umbrales L1–L8 están en `qualification_rank_thresholds` y pertenecen a `business_rule_versions`. No están enterrados en una cadena `if/else`. La evaluación exige que activación, volumen y calificación compartan la misma versión.

`QualificationResult` conserva rango, versión, período, `volumeResultId`, elegibilidad de activación, evidencia y assessments de negocio. Las explicaciones contienen hechos comparables —requerido versus real— y no razonamiento interno de IA.

El conteo provisional de indirectos usa profundidad `> 1`; la interfaz `IndirectCountPolicy` permite reemplazar esta interpretación cuando VISANA confirme el subconjunto.

## Persistencia e inmutabilidad

Flyway V8 agrega tablas y constraints. `JdbcQualificationHistoryStore` sólo ofrece operaciones `append`; no expone update/delete. PK, unique constraints y FKs protegen identidad, versión y vínculo histórico. Los rangos temporales se guardan como timestamps UTC y se reconstruyen por sus límites explícitos.

## Guardia de aprobación

`RuleExecutionGuard` permite evaluación técnica de `DRAFT`/`PROVISIONAL`, impide nuevas evaluaciones con `RETIRED` y exige `APPROVED` para cualquier consumidor futuro que quiera producir un efecto financiero. Ningún consumidor financiero se conecta en Sprint 5.

## Decisiones abiertas

DG-01 conserva vencimiento horario y recompra solapada; DG-09 conserva base monetaria; DG-11 conserva período de calificación; DG-15 conserva proveedor/mecanismo de confirmación. `PR8_RECONCILIATION_REQUIRED` queda posterior al gate de Sprint 5.
