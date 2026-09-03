# Estrategia de migración

```text
Legacy export -> extract -> staging -> validate -> transform -> load -> reconcile -> sign-off
```

Fases propuestas: inventario y contrato de fuente; staging inmutable; validación de tipos/fechas/FK; mapeo aprobado; carga ensayada; conciliación de conteos, red y totales; aprobación de negocio; cutover reversible por plan aprobado.

Controles mínimos: conteos por entidad, usuarios/roles, relaciones sponsor-genealogía, ventas/volumen, totales financieros por periodo, estados de ledger/payout y registro de excepciones. Las siete fechas sentinela y `1969-12` son `RIESGO_DE_CONCILIACION_DATOS`; no se transforman sin regla aprobada.

El `LegacyDataMigrationService` actual se clasifica como insumo parcial: omite persistencia de genealogía e items. No se reutiliza como pipeline productivo sin rediseño y validación.
