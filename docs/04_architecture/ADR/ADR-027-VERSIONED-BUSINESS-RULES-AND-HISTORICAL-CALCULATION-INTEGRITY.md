# ADR-027 — Versioned Business Rules and Historical Calculation Integrity

**Estado:** ACCEPTED para la foundation técnica; los valores de negocio cargados siguen `PROVISIONAL`.

## Contexto

Calificación y volumen dependen de reglas que pueden cambiar. La aclaración verbal de 2026-09-05 no es aprobación oficial. Recalcular silenciosamente historia con reglas nuevas destruiría la trazabilidad y podría afectar futuras consecuencias financieras.

## Decisión

1. Cada conjunto de reglas tiene identidad, versión, estado, vigencia y fuente.
2. Los estados soportados son `DRAFT`, `PROVISIONAL`, `APPROVED` y `RETIRED`.
3. `VolumeResult` y `QualificationResult` son snapshots append-only que referencian exactamente `business_rule_version_id`.
4. Una nueva regla crea una nueva versión y nuevos resultados; no actualiza resultados históricos.
5. `PROVISIONAL` se puede evaluar en desarrollo/prueba. Todo efecto financiero futuro debe pasar un guard que exige `APPROVED`.
6. La explicación persistida contiene hechos de negocio, no chain-of-thought.

## Consecuencias

- Se agregan tablas Flyway para versiones, umbrales y snapshots de resultados con FKs, índices y constraints.
- La aplicación no ofrece update/delete para esta historia.
- Cualquier corrección futura es una nueva versión y/o un registro compensatorio, no una reinterpretación retroactiva.
- La semilla Catherine 2026-09-05 queda `PROVISIONAL/HIGH`, con campos de aprobación nulos.
- PR #8 no se altera: `PR8_RECONCILIATION_REQUIRED` después del gate técnico.

## Alternativas descartadas

- Sobrescribir una tabla única de parámetros: rompe reproducibilidad histórica.
- Copiar valores sin identidad de versión dentro de cada servicio: duplica reglas y dificulta auditoría.
- Tratar la aclaración verbal como `APPROVED`: contradice la fuente disponible.
