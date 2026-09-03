# Sprint 5 readiness - Qualification & Volume Foundation

**Estado:** `BLOCKED_BY_BUSINESS_RULES`. Este documento no inicia Sprint 5 ni autoriza implementación.

## Alcance futuro, si se autoriza

Sprint 5 sería únicamente foundation de Qualification + Volume. No incluye cálculo de comisiones ni Rewards/Compensation.

Flujo de diseño, no implementado:

```text
CONFIRMED QUALIFYING SALE
  -> PERSONAL VOLUME
  -> UPLINE TEAM VOLUME
  -> PERIOD SNAPSHOT
  -> QUALIFICATION
```

No se decide todavía qué venta es qualifying.

## Entradas futuras

| Entrada | Estado actual | Falta antes de construir |
|---|---|---|
| Business profile / network member | Foundation Sprint 4 validada | DG-17 y DG-19 para semántica de negocio |
| Directos y descendencia | Closure table validada | Regla de conteo de afiliados activos |
| Ventas personales/volumen | Órdenes actor-owned existentes; no son volumen calificable | DG-03 y DG-09 |
| Team sales/volumen | No implementado | Definición completa DG-03 |
| Activación/elegibilidad | Importes documentados; vigencia no decidida | DG-01 y DG-11 |
| Período | Soporte técnico de fecha existe | Modelo de período aprobado |
| Reglas de rango | Tabla documental disponible | Team Sales, activación y regla de rango si aplica |

## Ready technically

- V7/Flyway y PostgreSQL en CI validan perfiles, vínculo actor-perfil, miembros, patrocinador inicial y closure table.
- Las consultas de directos, ancestros y descendientes son autenticadas y limitadas.
- La foundation preserva estructuras de profundidad mayor a ocho; no incorpora comportamiento económico.

## Blocked by business rule

- DG-01 activación/recompra.
- DG-03 Team Sales y venta calificable.
- DG-09 base de cálculo.
- DG-11 modelo de período.
- DG-17 cardinalidad de perfiles.
- DG-18 re-parenting.
- DG-19 autoridad de afiliación.

La autorización posterior debe indicar la fuente de verdad, casos de borde y criterios de aceptación por cada gate.
