# F5 Readiness — Qualification UI

**Fecha de corte:** 2026-09-07
**TECHNICAL_READINESS:** `READ_ONLY_SKELETON_ONLY`
**PM_EXECUTION_DECISION:** `WAIT_FOR_CLIENT_DECISIONS`
**F5_IMPLEMENTATION_AUTHORIZATION:** `NOT_AUTHORIZED`
**NEXT_TRIGGER:** recepción y aprobación de las reglas mínimas requeridas para F5
**Implementación F5:** `NOT_STARTED`
**Reglas aprobadas:** 0

## Motivo

La foundation backend de Qualification + Volume está integrada, versionada y protegida contra efectos financieros con reglas no aprobadas. Sin embargo:

- DG-01, DG-09 y parte de DG-18 sólo tienen aclaración verbal provisional;
- DG-03, DG-11, DG-19 y la semántica de subárbol de DG-18 siguen abiertas;
- los umbrales L1–L8 y demás valores permanecen provisionales;
- el OpenAPI v1 vigente, SHA-256 `F75B41FAECC92EDFC3974366047D81979147CFF00FB1124471D5BB9EEE9DB720`, no expone endpoints de activación, volumen, Team Sales, calificación o rango.

Por tanto, F5 no puede consumir ni presentar información canónica de calificación.

Aunque técnicamente sería posible preparar un esqueleto sin semántica, el PM decidió no hacerlo: produciría retrabajo sin valor funcional suficiente antes de las decisiones del cliente. Esta es una decisión de secuencia de entrega y no modifica ninguna regla de negocio.

## Capacidades permitidas si posteriormente se autoriza iniciar sólo el esqueleto

| Capacidad | Estado | Límite obligatorio |
|---|---|---|
| Ruta/navegación y título | Permitible | Sin afirmar que existe una calificación calculada |
| Mensaje de información pendiente | Permitible | Debe indicar ausencia de definición/aprobación o de datos disponibles |
| Layout vacío y estados loading/empty/error | Permitible | Sin cifras, fechas, porcentajes, progreso ni ejemplos tratados como datos reales |
| Identidad básica ya cubierta por contrato | Permitible | Sólo campos contractuales existentes; no inferir afiliación activa o rango |
| Enlaces a vistas F4 Network | Permitible | No recalcular ni reinterpretar genealogía |

## Capacidades bloqueadas

| Capacidad F5 | Gate(s) | Razón |
|---|---|---|
| Estado activo, vencimiento y countdown | DG-01 | Expiración y recompra no decididas; regla no aprobada |
| Team Sales | DG-03, DG-09, DG-11 | Venta calificable, fórmula y período no canónicos |
| Volumen personal | DG-09, DG-11; DG-03 para elegibilidad de venta | No hay base monetaria ni ventana oficial |
| Rango/calificación actual | DG-01, DG-03, DG-09, DG-11 y aprobación de umbrales | Las entradas y los umbrales siguen no aprobados |
| Progreso y siguiente rango | DG-01, DG-03, DG-09, DG-11 y aprobación de umbrales | Un porcentaje o faltante sería semántica inventada |
| Mensajes de alta/activación de afiliado | DG-19 | Autoridad y evento de afiliación no definidos |
| Métricas posteriores a cambio de sponsor | DG-18 | Movimiento de subárbol y vigencia no definidos |

## Condiciones mínimas para autorizar F5

- [ ] DG-01 tiene fuente oficial, expiración y recompra solapada definidas.
- [ ] DG-03 define venta calificable, alcance de red, cancelación, reembolso y momento de inclusión.
- [ ] DG-09 tiene fórmula oficial por métrica y tratamiento de ajustes.
- [ ] DG-11 define un período canónico, límites y zona horaria.
- [ ] Umbrales y dependencias de rango tienen aprobación oficial.
- [ ] La parte de F5 correspondiente tiene criterios de aceptación trazables.
- [ ] El contrato público aprobado expone únicamente los datos requeridos y sus estados.
- [ ] Se mantiene versionado, vigencia y preservación histórica conforme a ADR-027.

Cumplidas DG-01, DG-03, DG-09, DG-11 y la aprobación de umbrales L1–L8, F5 podrá reevaluarse como `READY_TO_START`. No se exige cerrar todas las reglas abiertas de Plan 3.

DG-18 y DG-19 pueden permanecer abiertas únicamente si F5 es de sólo lectura y no expone mutaciones de patrocinador, creación/activación de afiliaciones ni significado dependiente de esas operaciones.

## Restricciones de este gate

Este gate no inicia F5, no crea rama frontend y no modifica Java, Flyway, OpenAPI, Angular, pruebas, base de datos ni comportamiento de backend. No autoriza producción, despliegue, compensación, ledger, liquidación o pagos.
