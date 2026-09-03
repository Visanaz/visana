# Matriz P0 de brechas de prueba

**Evidencia actual:** 17 archivos `*Test.java` y 101 anotaciones `@Test` en fuente; no hay `*IT.java` ni E2E localizados. `target/surefire-reports` contiene 19 XML y 102 resultados declarados, clasificados como `HISTORICAL_REPORT_ONLY`. No se ejecutaron pruebas.

| P0 / BR o GAP | Unit actual | Integration actual | Contract actual | E2E actual | Security actual | Cobertura requerida para cierre | Estado |
|---|---|---|---|---|---|---|---|
| Activacion/recompra GAP-001 | Generica de evaluador | NONE | NONE | NONE | NONE | Borde mes/30 dias, monto, periodo, persistencia y cancelacion | GAP-MISSING-TEST |
| Invitacion distribuidor GAP-002 | NONE | NONE | NONE | NONE | NONE | Tiers/fijo aprobado, elegibilidad, duplicados, periodo y payout | GAP-MISSING-TEST |
| Precedencia GAP-003 | Mock de mapa global | NONE | NONE | NONE | NONE | Default/rol/override/calificacion por actor y auditabilidad | GAP-MISSING-TEST |
| Unilevel GAP-004 | Mapa/calificacion/depth | NONE | NONE | NONE | NONE | Proveedor real, upline, idempotencia, transaccion y concurrencia | GAP-MISSING-TEST |
| Nivel 8 GAP-005 | Individual parametrico | NONE | NONE | NONE | NONE | Alternativa aprobada; si bolsa, reparto y precision | GAP-MISSING-TEST |
| Premio 1% GAP-006 | NONE | NONE | NONE | NONE | NONE | Trigger, no duplicacion, ledger/pago | GAP-MISSING-TEST |
| Restriccion rama GAP-007 | NONE | NONE | NONE | NONE | NONE | Rango/snapshot/bloqueo/reactivacion/historico | GAP-MISSING-TEST |
| Team sales/calificacion GAP-008 | AND generico | NONE | NONE | NONE | NONE | Venta calificable a snapshots y N1-N8 | GAP-MISSING-TEST |
| Pools/consistencia GAP-009/010 | NONE | NONE | NONE | NONE | NONE | Acumulacion, elegibilidad, reparto, racha/reinicio y pago | GAP-MISSING-TEST |
| Origen de pago GAP-011 | Estado/servicio simulado | NONE | NONE | NONE | NONE | Contrato callback, firma, idempotencia y rechazo | GAP-MISSING-TEST |
| Cadena financiera GAP-012 | Settlement aislado | NONE | NONE | NONE | NONE | Commission→ledger→approval→settlement→payout→reconciliacion | GAP-MISSING-TEST |
| Reversal GAP-013 | Objeto Commission | NONE | NONE | NONE | NONE | Volumen, calificacion, ledger, payout y reportes | GAP-MISSING-TEST |
| Ownership/roles GAP-014..016 | JWT de controller limitado | NONE | NONE | NONE | NONE | Casos cross-resource, rol, tenant/owner y auditoria | GAP-MISSING-TEST |
| Migracion/datos GAP-018..021 | Servicio parcial | NONE | NONE | NONE | NONE | Mapeo, FK, estados, sentinelas y conciliacion | GAP-MISSING-TEST |
| Tax/payment order GAP-028 | NONE | NONE | NONE | NONE | NONE | Base/tasa, orden, aprobación, pago y reverso | GAP-MISSING-TEST |

## Clasificacion de evidencia de prueba

| Area | Current source test | Historical report only | Binary only | None |
|---|---|---|---|---|
| Orden/pago simulado | Si, unidades | Si | No aplicable | No gateway/contract/E2E |
| Unilevel parametrico | Si, unidades | Si | No aplicable | No fuente de plan/red/calificacion real |
| Activacion/calificacion generica | Si, unidades | Si | No aplicable | No regla/periodo configurado real |
| Ledger/settlement aislado | Si, unidades | Si | No aplicable | No flujo financiero integrado |
| Network persistence/plan/volume | No | Algunos reportes pueden referir artefactos previos | Si, componentes target | Si, fuente equivalente |
| Pools, consistencia, premio, rango, tax, shipping | No | No demostrado | No | Si |

**Criterio:** una prueba unitaria con mocks no acredita proveedor real, persistencia, contrato, integracion o resultado runtime. Las pruebas requeridas se definen como condicion futura de cierre, no se crean en esta fase.
