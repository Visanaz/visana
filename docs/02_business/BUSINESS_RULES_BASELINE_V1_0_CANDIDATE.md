# Business Rules Baseline v1.0 — candidato

**Estado:** `CANDIDATE_FROM_DIGITAL_FUNCTIONAL_DECISION`

**Fuente vigente:** formulario oficial de Daniel Reina

**Ratificación física:** `PENDING`

**Entrada en vigor:** `EFFECTIVE_FROM_PENDING`

**Autorización de implementación:** sólo reglas completas y después de gate técnico específico; este documento no implementa ni liquida valores.

## Reglas confirmadas digitalmente

1. Una identidad puede poseer simultáneamente perfiles Cliente, Afiliado y Distribuidor, sin duplicar identidad ni documento.
2. Para Qualification/Volume, la base es el valor efectivamente pagado por el producto, incluyendo IVA.
3. Team Sales incluye ventas propias y de toda la descendencia. Sólo cuentan ventas pagadas/confirmadas; cancelaciones y devoluciones restan.
4. El bono de invitación de distribuidor es 10% y requiere distribuidor activo. No incluye automáticamente el margen comercial 25%.
5. El 2% de nivel 8 es una comisión individual por ventas de descendientes ubicados en ese nivel, no un pool global.
6. La aprobación de comisiones es automática por sistema; sus excepciones operacionales quedan pendientes.
7. La orden de pago sigue `Created → Approved → Processing → Paid / Failed`.
8. Los umbrales L1–L8 quedan confirmados según la tabla siguiente.
9. BR-CANON confirma compras mínimas, porcentajes Unilevel y cortes de pago.

## Umbrales QTH-F5-01

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

## BR-CANON confirmado

| Concepto | Decisión digital |
|---|---|
| Compra inicial mínima | `>= COP 200.000` |
| Recompra mínima | `>= COP 100.000` |
| Unilevel L1–L8 | `15%, 10%, 5%, 4%, 3%, 2%, 1%, 2%` |
| Corte 1–15 | Pago día 25 |
| Corte 16–fin de mes | Pago día 10 |

Estos porcentajes no definen automáticamente base, elegibilidad, tratamiento tributario, reversos, acumulación o vigencia.

## Núcleos confirmados con observaciones

- DG-19: afiliación formal mediante aprobación administrativa; lifecycle exacto pendiente.
- DG-18: cambio de sponsor sólo administrativo, justificado, auditado y prospectivo; compresión por inactividad queda separada y abierta.
- DG-01: aniversario mensual calendario; permanecen recompra temprana, expiración exacta y fin de mes.
- DG-07: 1% adicional; mecánica de pool incompleta.
- DG-06A/DG-06B: alcance global y consistencia mensual confirmados; bases, tiers, reparto y rachas incompletos.
- DG-08: se detiene toda comisión de la rama; temporalidad y reversibilidad pendientes.
- DG-10: reverso antes de pago y descuento futuro después de pago; casos parciales/cerrados pendientes.
- DG-13: beneficio de envío desde 3 unidades a un destino; excepciones pendientes.
- DG-15: dirección Banco de Occidente/Occired; integración no autorizada.
- DG-16: migración histórica como workstream futuro separado.

## Pendientes que impiden semántica completa

- DG-11: período de calificación.
- DG-12: matriz oficial de Contabilidad.
- DG-14: adopción formal de la recomendación técnica de identidad.
- DG-01: reglas de recompra anticipada, instante de expiración y fin de mes.
- `GENEALOGY_INACTIVITY_COMPRESSION_RULE`.
- `DG06A_MONETARY_BASE_CLARIFICATION_REQUIRED`.

## Integridad histórica

Este candidato `SUPERSEDES_PROVISIONAL_VERSION` sólo cuando se cree una nueva versión aprobada con vigencia explícita. ADR-027 prohíbe reinterpretar resultados históricos y exige conservar fuente y versión exactas.
