# BUSINESS RULES WORKING BASELINE — 2026-09-05 — PROVISIONAL

## Control de la fuente

- Identidad: `BUSINESS_RULES_WORKING_BASELINE_CATHERINE_2026-09-05_PROVISIONAL`.
- Fuente: aclaración verbal de VISANA atribuida a Catherine, recibida el 2026-09-05.
- Estado: `PROVISIONAL_HIGH_CONFIDENCE`.
- Aprobación del cliente: **NO**. No existe todavía documento oficial escrito.
- Uso permitido: evaluación técnica y fixtures sintéticos de desarrollo/prueba.
- Uso prohibido: comisiones, ledger, liquidación, pago o cualquier otro efecto financiero de producción.
- PR #8: permanece abierta y conflictiva; su contenido de negocio no se modifica ni se reconcilia en Sprint 5.

## DG-01 — Activación

Estado: `PROVISIONAL_HIGH_CONFIDENCE` / `PROVISIONAL_VERBAL_CLARIFICATION`.

| Aspecto | Base de trabajo provisional | Estado |
|---|---|---|
| Evento disparador | Confirmación de pago (`PAYMENT_CONFIRMED`) | `PROVISIONAL_HIGH_CONFIDENCE` |
| Inicio | `paymentConfirmedAt` | `PROVISIONAL_HIGH_CONFIDENCE` |
| Duración | Un mes calendario mediante `plusMonths(1)` | `PROVISIONAL_HIGH_CONFIDENCE` |
| Expiración horaria | Mismo día/hora local del aniversario mensual | `TECHNICAL_PROVISIONAL_DEFAULT` / `PENDING_OFFICIAL_CONFIRMATION` |
| Recompra solapada | No determinada; debe resolverse por política | `PENDING_OFFICIAL_CONFIRMATION` |
| Zona técnica | `America/Bogota`, explícita; persistencia temporal normalizada a UTC | Decisión técnica provisional, no regla comercial aprobada |

Ejemplos provisionales:

| Confirmación | Fin provisional |
|---|---|
| 05/09/2026 14:35 | 05/10/2026 14:35 |
| 31/01/2026 08:00 | 28/02/2026 08:00 |
| 31/01/2028 08:00 | 29/02/2028 08:00 |
| 29/02/2028 08:00 | 29/03/2028 08:00 |
| 28/02/2027 08:00 | 28/03/2027 08:00 |

No se codifica una duración fija en días. Sigue pendiente que VISANA confirme si el vencimiento ocurre en el instante exacto o al final del día aniversario, y si una recompra inicia en el pago nuevo, en la expiración vigente u otro punto.

## Team Sales

Base verbal provisional de alta confianza:

`Team Sales = ventas calificables propias + ventas calificables de todos los descendientes`.

La genealogía se obtiene de `genealogy_closure`. La pertenencia al subárbol se separa de `QualifyingSalePolicy`; por tanto, esta base no decide qué estados de venta, cancelaciones o devoluciones son elegibles.

## Base monetaria

Entendimiento verbal provisional: valor efectivo después de descuentos y antes de impuestos. Sigue abierto DG-09 y esta fórmula **no** se declara aprobada. La foundation conserva por separado `gross`, `discount`, `netBeforeTax`, `tax`, `shipping`, `paidAmount` y `refundOrCancellationAmount`; una política selecciona la base.

## Umbrales de calificación L1–L8

Los valores siguientes coinciden con `BUSINESS_RULE_CATALOG.md` (BR-QUAL-002..008) y con la estructura provisional suministrada para Sprint 5. Se almacenan como datos ligados a una versión; no son reglas aprobadas.

| Nivel | Afiliación | Directos activos | Indirectos | Team Sales COP |
|---|---:|---:|---:|---:|
| L1 | Sí | 0 | 0 | 0 |
| L2 | No | 5 | 0 | 3.000.000 |
| L3 | No | 7 | 25 | 25.000.000 |
| L4 | No | 9 | 110 | 80.000.000 |
| L5 | No | 12 | 350 | 250.000.000 |
| L6 | No | 15 | 1.200 | 1.000.000.000 |
| L7 | No | 20 | 3.500 | 2.500.000.000 |
| L8 | No | 25 | 15.000 | 30.000.000.000 |

## Decisiones no codificadas como verdad final

- Período de calificación DG-11: mes calendario, rolling o corte quincenal.
- Estados exactos de venta que califican y momento de inclusión.
- Base monetaria DG-09 y tratamiento definitivo de descuentos, impuestos, envío, cancelaciones y devoluciones.
- Semántica oficial del conteo de indirectos; la estrategia provisional cuenta descendientes de profundidad mayor que uno y permanece reemplazable.
- Vencimiento exacto frente a fin del día aniversario.
- Inicio de vigencia de recompra cuando existe solapamiento.
- Toda fórmula o consecuencia de compensación, pools, impuestos, liquidación y pago.

## Integridad histórica

Cada `VolumeResult` y `QualificationResult` referencia la versión exacta usada. Una versión nueva produce resultados nuevos; no reinterpreta ni actualiza resultados históricos. `PROVISIONAL` puede evaluarse en desarrollo/prueba, pero el guard técnico exige `APPROVED` antes de cualquier efecto financiero.

## Reconciliación pendiente

`PR8_RECONCILIATION_REQUIRED`: después del gate técnico de Sprint 5 se debe reconciliar esta aclaración provisional con PR #8 y con el documento oficial de VISANA. Sprint 5 no resuelve los conflictos ni cambia decisiones del cliente.
