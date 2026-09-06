# Sprint 5 — Qualification and Volume Foundation Report

## Resultado

Foundation backend implementada en una rama aislada. No expone endpoints nuevos y no conecta resultados con compensación, ledger, liquidación ni pago.

## Foundation implementada

- `BusinessRuleVersion` con estado, vigencia, fuente, aprobación opcional y confianza provisional.
- `ActivationPolicy`, `ActivationCalculator` y `ActivationWindow` disparados por confirmación de pago con tiempo/zona explícitos.
- `QualificationPeriod` y `QualificationPeriodPolicy` sin cerrar DG-11.
- Evidencia monetaria separada, `QualifyingSalePolicy` y `QualificationBasePolicy`.
- Team Sales propia + descendientes mediante el cierre genealógico existente.
- `VolumeResult` y `QualificationResult` versionados, explicables y append-only.
- Umbrales provisionales L1–L8 almacenados como datos versionados.
- Guard que bloquea el uso financiero de una versión no `APPROVED`.
- Migración Flyway V8 exclusivamente aditiva.

## Reglas provisionales utilizadas

- Trigger de activación: `PAYMENT_CONFIRMED`.
- Inicio: `paymentConfirmedAt`.
- Duración: un mes calendario.
- Default técnico: aniversario a igual hora local en `America/Bogota`.
- Team Sales: propias más todos los descendientes, sujeto a política de venta calificable.
- Umbrales L1–L8 coincidentes con el catálogo documental existente.

Todas permanecen no aprobadas. La fuente es aclaración verbal VISANA 2026-09-05.

## Reglas intencionalmente no codificadas

- Estado final de venta elegible.
- Fórmula final de base monetaria.
- Período de calificación.
- Vencimiento exacto versus fin de día y solapamiento de recompra.
- Comisiones Unilevel, L8, pools, impuestos, reverso financiero, settlement y pagos.
- Proveedor real de pago, gateway o webhook.

## Pruebas

- Línea base: `129` tests, `0` failures, `0` errors, `6` skipped; `BUILD SUCCESS`.
- Validación final: `150` tests, `0` failures, `0` errors, `6` skipped; `BUILD SUCCESS` con JDK 21.
- Casos nuevos: calendario normal, fines de febrero, bisiesto, año, offset horario, versión A/B, guard financiero, período intercambiable, agregación propia/downline, ajustes, umbrales, explicación, FKs y append-only.
- Los tests de integración PostgreSQL existentes se omiten localmente cuando Docker no está disponible; CI debe ejecutarlos según su entorno.

## Gaps y decisiones requeridas

DG-01 oficial, DG-09 base monetaria, DG-11 período, DG-18 movimiento de subárbol, pools de distribuidor, consistencia 6/12 meses, autoridad de aprobación de comisiones, reversos de comisiones pagadas, matriz tributaria, regla de envío 3 versus más de 3 y mecanismo/proveedor de pago.

`PR8_RECONCILIATION_REQUIRED`: reconciliar PR #8 sólo después de este gate y después de recibir la fuente oficial; no fue modificada ni resuelta por Sprint 5.
