# Sprint 5 readiness - Qualification & Volume Foundation (reconciliado)

**Estado:** `FOUNDATION_IMPLEMENTED / PRODUCTION_EFFECTS_NOT_AUTHORIZED`.

La foundation técnica fue integrada en `dev` por PR #15. Las reglas de negocio permanecen provisionales o abiertas; este documento no autoriza calificación productiva, compensación, ledger, liquidación ni pago.

## Alcance implementado

Sprint 5 implementó únicamente foundation versionada de Qualification + Volume. No incluye cálculo de comisiones ni Rewards/Compensation.

Flujo de foundation implementado para desarrollo/pruebas controladas:

```text
CONFIRMED QUALIFYING SALE
  -> PERSONAL VOLUME
  -> UPLINE TEAM VOLUME
  -> PERIOD SNAPSHOT
  -> QUALIFICATION
```

No se decide todavía qué venta es qualifying.

## Entradas futuras

| Entrada | Estado actual | Falta antes de autorizar efectos productivos |
|---|---|---|
| Business profile / network member | Foundations Sprint 4/5 validadas | DG-17 y DG-19 oficiales para semántica de negocio |
| Directos y descendencia | Closure table y lector de subárbol implementados | Regla oficial de conteo/elegibilidad |
| Ventas personales/volumen | Evidencia monetaria y políticas intercambiables implementadas | DG-03 y DG-09 oficiales |
| Team sales/volumen | Agregación propia + descendientes implementada como provisional | Definición oficial completa DG-03 |
| Activación/elegibilidad | `PAYMENT_CONFIRMED` + un mes calendario implementados como provisional | DG-01 oficial; expiración y recompra |
| Período | Política intercambiable implementada | DG-11 aprobado; no se infiere calendario/rolling/quincenal |
| Reglas de rango | Umbrales L1-L8 versionados como provisionales | Aprobación de tabla y dependencias de qualification |

## Ready technically

- V7/Flyway y PostgreSQL en CI validan perfiles, vínculo actor-perfil, miembros, patrocinador inicial y closure table.
- Las consultas de directos, ancestros y descendientes son autenticadas y limitadas.
- La foundation preserva estructuras de profundidad mayor a ocho; no incorpora comportamiento económico.
- V8, reglas versionadas, resultados append-only y guard financiero están implementados; versiones no `APPROVED` no pueden habilitar efectos financieros.

## Blocked for production by business rule

- DG-01 documento oficial, expiración y recompra; la regla actual es provisional.
- DG-03 Team Sales y venta calificable.
- DG-09 fórmula oficial; después de descuentos y antes de impuestos sólo es provisional para qualification.
- DG-11 modelo de período de calificación, totalmente abierto.
- DG-17 cardinalidad de perfiles.
- DG-18 movimiento de subárbol; admin-only + justificación + auditoría sólo es provisional.
- DG-19 autoridad de afiliación.

La autorización posterior debe indicar fuente de verdad, responsable/fecha de aprobación, casos de borde y criterios de aceptación por gate. Hasta entonces no hay efectos productivos de qualification.
