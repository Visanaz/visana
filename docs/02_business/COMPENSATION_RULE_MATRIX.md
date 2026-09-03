# Matriz de trazabilidad del plan de compensacion

Leyenda de prueba: **J** = Java fuente HEAD; **S** = SQL/configuracion; **P** = PDF; **H** = AS-IS historico; **T** = test fuente. Un item presente no prueba ejecucion.

**Total catalogado:** 35 reglas de negocio (BR). La presencia de PDF, SQL y Java en una misma fila no acredita una traza ejecutable completa: debe llegar, como minimo, hasta persistencia, caller y prueba o evidencia de resultado.

| BR-ID | Regla | P | J | S / persistencia | Caller / test | Resultado |
|---|---|---|---|---|---|---|
| BR-DIST-001 | Compra inicial distribuidor COP 200.000 | p1,p3 | Evaluador parametrico | `commission_plans.activation_amount=200000` | T activacion generica | [PARCIALMENTE_IMPLEMENTADA] |
| BR-DIST-002 | Beneficio comercial 35% | p1 | No localizado | No localizado | NONE | [DOCUMENTADA_NO_VERIFICADA] |
| BR-DIST-003 | Invitacion distribuidor 10% | p1 | No localizado | `distributor_recruitment_tiers`: 3%/5% | H confirma conflicto; NONE J | [CONTRADICCION] |
| BR-DIST-POOL-001 | Pool 15M, 1%, todos distribuidores | p1 | No localizado | `distributor_sales_pools`, `all_distributors` | NONE | [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO] |
| BR-DIST-POOL-002 | Pool 25M, 1,5%, todos distribuidores | p1 | No localizado | misma tabla, `all_distributors` | NONE | [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO] |
| BR-DIST-POOL-003 | Pool 45M, 2%, equipo | p1 | No localizado | misma tabla, `distributor_team` | NONE | [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO] |
| BR-DIST-POOL-004 | Pool 100M, 2,5%, equipo | p1 | No localizado | misma tabla, `distributor_team` | NONE | [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO] |
| BR-DIST-CONS-001 | 6 meses, 0,5% | p2 | No localizado | `distributor_consistency_bonuses` | NONE | [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO] |
| BR-DIST-CONS-002 | 12 meses, 1% | p2 | No localizado | `distributor_consistency_bonuses` | NONE | [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO] |
| BR-AFF-UNI-001 | Unilevel L1 15% | p2 | Motor parametrico | plan base 15%; roles 1/2 override 10% | T mapa 15% | [TRAZABILIDAD_INCOMPLETA] |
| BR-AFF-UNI-002 | Unilevel L2 10% | p2 | Motor parametrico | base 10%; override 5% | T mapa 10% | [TRAZABILIDAD_INCOMPLETA] |
| BR-AFF-UNI-003 | Unilevel L3 5% | p2 | Motor parametrico | base 5%; override 4% | T mapa estandar | [TRAZABILIDAD_INCOMPLETA] |
| BR-AFF-UNI-004 | Unilevel L4 4% | p2 | Motor parametrico | base 4%; override 3% | T mapa estandar | [TRAZABILIDAD_INCOMPLETA] |
| BR-AFF-UNI-005 | Unilevel L5 3% | p2 | Motor parametrico | base 3%; override 2% | T mapa estandar | [TRAZABILIDAD_INCOMPLETA] |
| BR-AFF-UNI-006 | Unilevel L6 2% | p2 | Motor parametrico | base 2%; override 1% | T mapa estandar | [TRAZABILIDAD_INCOMPLETA] |
| BR-AFF-UNI-007 | Unilevel L7 1% | p2 | Motor parametrico | base/override 1% | T mapa estandar | [TRAZABILIDAD_INCOMPLETA] |
| BR-AFF-UNI-008 | Nivel 8 2% equitativo | p2 | 2% individual por ancestro si mapa L8 | base 2%; sin tabla de bolsa L8 | T solo comision individual | [CONTRADICCION] |
| BR-AFF-PRIZE-001 | Premio 1% L1-L7 | p4 | No localizado | No localizado | NONE | [DOCUMENTADA_NO_VERIFICADA] |
| BR-ACT-001 | Activacion inicial COP 200.000 | p3 | Parametros de evaluador | seed 200.000 | T activacion generica | [PARCIALMENTE_IMPLEMENTADA] |
| BR-ACT-002 | Recompra mensual COP 100.000 | p3 | No localizado | seed 100.000 | NONE | [DOCUMENTADA_NO_VERIFICADA] |
| BR-QUAL-001 | N1: afiliacion | p3 | Evaluador generico | rol afiliado L1: umbrales cero | T reglas genericas | [TRAZABILIDAD_INCOMPLETA] |
| BR-QUAL-002 | N2: 5 directos, 3M | p3 | Evaluador AND generico | JSON rol 3 coincide | T reglas genericas | [TRAZABILIDAD_INCOMPLETA] |
| BR-QUAL-003 | N3: 7, 25, 25M | p3 | Evaluador AND generico | JSON rol 3 coincide | T reglas genericas | [TRAZABILIDAD_INCOMPLETA] |
| BR-QUAL-004 | N4: 9, 110, 80M | p3 | Evaluador AND generico | JSON rol 3 coincide | T reglas genericas | [TRAZABILIDAD_INCOMPLETA] |
| BR-QUAL-005 | N5: 12, 350, 250M | p3 | Evaluador AND generico | JSON rol 3 coincide | T reglas genericas | [TRAZABILIDAD_INCOMPLETA] |
| BR-QUAL-006 | N6: 15, 1.200, 1.000M | p3 | Evaluador AND generico | JSON rol 3 coincide | T reglas genericas | [TRAZABILIDAD_INCOMPLETA] |
| BR-QUAL-007 | N7: 20, 3.500, 2.500M | p3 | Evaluador AND generico | JSON rol 3 coincide | T reglas genericas | [TRAZABILIDAD_INCOMPLETA] |
| BR-QUAL-008 | N8: 25, 15.000, 30.000M | p3 | Evaluador AND generico | JSON rol 3 coincide | T reglas genericas | [TRAZABILIDAD_INCOMPLETA] |
| BR-AFF-RANK-001 | Bloqueo por descendiente de igual/superior rango | p4 | No localizado | No localizado | NONE | [DOCUMENTADA_NO_VERIFICADA] |
| BR-PER-001 | Cortes 1-15/16-fin; pagos 25/10 | p4 | `Period` mensual; sin corte | tabla `periods` con ejemplos | T formato mensual; no corte | [TRAZABILIDAD_INCOMPLETA] |
| BR-TAX-001 | Retencion y Rete-ICA | p4 | No localizado | No localizado | NONE | [DOCUMENTADA_NO_VERIFICADA] |
| BR-SHIP-001 | Envio por mas de 3 productos | p4 | No localizado | No localizado | NONE | [DOCUMENTADA_NO_VERIFICADA] |
| BR-COMP-CONF-001 | Default plan -> override rol -> calificacion | Implícito p2-p3 | Motor solo recibe mapa/booleano | Base, overrides y JSON presentes | T inyecta mocks, no SQL | [TRAZABILIDAD_INCOMPLETA] |
| BR-PAY-001 | Confirmacion de pago simulada dispara compensacion | No documentada | Controller -> servicio -> evento | ordenes/comisiones V1 | T unidad/Mockito | [IMPLEMENTADA] como simulacion; [NO_EVIDENCIADO] gateway |
| BR-LEDGER-001 | Comision -> ledger -> settlement -> pago | p4 describe pago | `Commission.reverse`, `Payout` aislado | legacy ledger/pagos; V1 sin ledger | T reverso/settlement unitario | [TRAZABILIDAD_INCOMPLETA] |

## Lectura de cobertura

- **Fuente de test actual:** 19 clases y 100 anotaciones `@Test`; no hay `*IT.java` ni E2E localizado.
- **Surefire historico:** 19 XML declaran 102 pruebas exitosas. La diferencia 100/102 y los binarios sin fuente impiden tratarlos como validacion del HEAD.
- **Reglas financieras P0 sin test de fuente:** los cuatro pools, dos bonos de consistencia, invitacion de distribuidor, premio 1%, restriccion por rango, cortes/pagos, impuestos, envio, precedencia rol-SQL y flujo completo ledger/pago.
