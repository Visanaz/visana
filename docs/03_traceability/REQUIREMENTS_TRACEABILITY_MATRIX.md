# Matriz maestra de trazabilidad de requisitos

**Leyenda de cadena:** P=port, A=adapter, DB=persistencia, C=caller, E=evento, T=test fuente, R=evidencia runtime. `no` significa no localizado en SRC-001; `hist` identifica evidencia SRC-004/SRC-005, no implementación actual. Cada ruptura se registra sin completar enlaces por inferencia.

| BR-ID / requisito | Plan | Java actual | SQL / PHP historico | P/A/DB/C/E/T/R y ruptura | GAP / DG | Disposicion / prioridad |
|---|---|---|---|---|---|---|
| BR-DIST-001 compra inicial 200k | PDF p1,p3 | Evaluador de activacion generico | plan 200k; PHP activacion | P/A/DB/C/E: no; T unit generico; R no | GAP-001 / DG-01 | KEEP_AND_HARDEN condicional / P0 |
| BR-DIST-002 beneficio 35% | PDF p1 | no localizado | no localizado | P/A/DB/C/E/T/R: no | GAP-028 / DG-09 | IMPLEMENT_NEW tras decision / P0 |
| BR-DIST-003 invitacion | PDF 10% | no localizado | tiers 3%/5%; PHP conflicto | P/A/DB/C/E/T/R: no | GAP-002 / DG-02 | REFACTOR tras decision / P0 |
| BR-DIST-POOL-001 15M/1% | PDF p1 | no localizado | seed pool global | P/A/DB/C/E/T/R: no | GAP-009 / DG-06 | IMPLEMENT_NEW / P0 |
| BR-DIST-POOL-002 25M/1,5% | PDF p1 | no localizado | seed pool global | P/A/DB/C/E/T/R: no | GAP-009 / DG-06 | IMPLEMENT_NEW / P0 |
| BR-DIST-POOL-003 45M/2% | PDF p1 | no localizado | seed pool equipo | P/A/DB/C/E/T/R: no | GAP-009 / DG-06 | IMPLEMENT_NEW / P0 |
| BR-DIST-POOL-004 100M/2,5% | PDF p1 | no localizado | seed pool equipo | P/A/DB/C/E/T/R: no | GAP-009 / DG-06 | IMPLEMENT_NEW / P0 |
| BR-DIST-CONS-001 seis meses | PDF p2 | no localizado | seed bonus; PHP sin uso probado | P/A/DB/C/E/T/R: no | GAP-010 / DG-06 | IMPLEMENT_NEW / P0 |
| BR-DIST-CONS-002 doce meses | PDF p2 | no localizado | seed bonus; PHP sin uso probado | P/A/DB/C/E/T/R: no | GAP-010 / DG-06 | IMPLEMENT_NEW / P0 |
| BR-AFF-UNI-001 L1 15% | PDF p2 | Calculador parametrico | base/override SQL; PHP motor | P si; A no; DB Commission si; C listener; E paid; T unit; R no | GAP-004 / DG-09 | KEEP_AND_HARDEN / P0 |
| BR-AFF-UNI-002 L2 10% | PDF p2 | Igual L1 | base/override SQL; PHP motor | P si; A no; DB Commission si; C listener; E paid; T unit; R no | GAP-004 / DG-09 | KEEP_AND_HARDEN / P0 |
| BR-AFF-UNI-003 L3 5% | PDF p2 | Igual L1 | base/override SQL; PHP motor | P si; A no; DB Commission si; C listener; E paid; T unit; R no | GAP-004 / DG-09 | KEEP_AND_HARDEN / P0 |
| BR-AFF-UNI-004 L4 4% | PDF p2 | Igual L1 | base/override SQL; PHP motor | P si; A no; DB Commission si; C listener; E paid; T unit; R no | GAP-004 / DG-09 | KEEP_AND_HARDEN / P0 |
| BR-AFF-UNI-005 L5 3% | PDF p2 | Igual L1 | base/override SQL; PHP motor | P si; A no; DB Commission si; C listener; E paid; T unit; R no | GAP-004 / DG-09 | KEEP_AND_HARDEN / P0 |
| BR-AFF-UNI-006 L6 2% | PDF p2 | Igual L1 | base/override SQL; PHP motor | P si; A no; DB Commission si; C listener; E paid; T unit; R no | GAP-004 / DG-09 | KEEP_AND_HARDEN / P0 |
| BR-AFF-UNI-007 L7 1% | PDF p2 | Igual L1 | base/override SQL; PHP motor | P si; A no; DB Commission si; C listener; E paid; T unit; R no | GAP-004 / DG-09 | KEEP_AND_HARDEN / P0 |
| BR-AFF-UNI-008 L8 2% | PDF bolsa equitativa | Porcentaje individual por ancestro | base 2%, sin bolsa | P plan si; A no; DB Commission si; C/E/T unit individual; R no | GAP-005 / DG-09 | Condicional A/B / P0 |
| BR-AFF-PRIZE-001 premio 1% L1-L7 | PDF p4 | no localizado | no localizado | P/A/DB/C/E/T/R: no | GAP-006 / DG-07 | IMPLEMENT_NEW / P0 |
| BR-ACT-001 activacion | PDF p3,p4 | Parametros de evaluador | 30 dias; PHP 29/mes | P/A/DB/C/E: no; T unit; R no | GAP-001 / DG-01,DG-11 | KEEP_AND_HARDEN condicional / P0 |
| BR-ACT-002 recompra 100k | PDF p3 | no localizado | seed 100k; PHP parcial | P/A/DB/C/E/T/R: no | GAP-001 / DG-01 | KEEP_AND_HARDEN condicional / P0 |
| BR-QUAL-001 N1 | PDF p3 | Evaluador generico | JSON rol afiliado | P/A/DB: no; C compensation; E paid; T unit; R no | GAP-008 / DG-03 | REFACTOR / P0 |
| BR-QUAL-002 N2 | PDF p3 | AND generico | JSON 5/0/3M | P/A/DB: no; C/E compensation; T unit; R no | GAP-008 / DG-03 | REFACTOR / P0 |
| BR-QUAL-003 N3 | PDF p3 | AND generico | JSON 7/25/25M | P/A/DB: no; C/E compensation; T unit; R no | GAP-008 / DG-03 | REFACTOR / P0 |
| BR-QUAL-004 N4 | PDF p3 | AND generico | JSON 9/110/80M | P/A/DB: no; C/E compensation; T unit; R no | GAP-008 / DG-03 | REFACTOR / P0 |
| BR-QUAL-005 N5 | PDF p3 | AND generico | JSON 12/350/250M | P/A/DB: no; C/E compensation; T unit; R no | GAP-008 / DG-03 | REFACTOR / P0 |
| BR-QUAL-006 N6 | PDF p3 | AND generico | JSON 15/1200/1B | P/A/DB: no; C/E compensation; T unit; R no | GAP-008 / DG-03 | REFACTOR / P0 |
| BR-QUAL-007 N7 | PDF p3 | AND generico | JSON 20/3500/2,5B | P/A/DB: no; C/E compensation; T unit; R no | GAP-008 / DG-03 | REFACTOR / P0 |
| BR-QUAL-008 N8 | PDF p3 | AND generico | JSON 25/15000/30B | P/A/DB: no; C/E compensation; T unit; R no | GAP-008 / DG-03 | REFACTOR / P0 |
| BR-AFF-RANK-001 rama/rango | PDF p4 | no localizado | no localizado | P/A/DB/C/E/T/R: no | GAP-007 / DG-08 | IMPLEMENT_NEW / P0 |
| BR-PER-001 cortes/pagos | PDF p4 | `Period` mensual; payout aislado | `periods`, ordenes historicas | P/A/DB/C/E: no; T unit periodo; R no | GAP-012 / DG-05 | REFACTOR / P0 |
| BR-TAX-001 impuestos | PDF p4 | no localizado | no localizado | P/A/DB/C/E/T/R: no | GAP-028 / DG-12 | IMPLEMENT_NEW / P0 |
| BR-SHIP-001 envio >3 | PDF p4 | no localizado | no localizado | P/A/DB/C/E/T/R: no | GAP-028 / DG-13 | IMPLEMENT_NEW condicionado / P1 |
| BR-COMP-CONF-001 precedencia | Implicita en PDF | Puerto devuelve mapa global | plan, overrides y JSON SQL | P si; A no; DB legado; C calculo; E paid; T mock; R no | GAP-003 / DG-09 | IMPLEMENT_NEW resolver / P0 |
| BR-PAY-001 pago confirmado | No documentada | Controller/servicio/evento simulado | V1 orders; PHP no acredita gateway | P repo orden si; A JPA si; DB si; C HTTP; E si; T unit; R no | GAP-011 / sin DG | REFACTOR / P0 |
| BR-LEDGER-001 comision a pago | PDF p4 pago | Commission/Payout aislados | ledger/pagos PHP parcial | P/A/DB/C/E: no; T unit aislada; R no | GAP-012 / DG-04,DG-05 | REFACTOR + nuevo / P0 |

## Hallazgos transversales de traza

- Los tres puertos que consume `CalculateCommissionsService` (`CommissionPlanProviderPort`, `GenealogyProviderPort`, `QualificationProviderPort`) existen en fuente, pero no tienen adaptador fuente localizado. Su inyeccion queda como [GAP-MISSING-ADAPTER].
- El `CommissionRepository` y su persistencia actual son el unico tramo de salida localizado en compensacion. No conecta ledger, settlement, impuesto o pago.
- `OrderPaidEvent` es un evento Spring local. No prueba proveedor, webhook, firma, idempotencia externa ni transaccion atomica con compensacion.
- Los 17 archivos de test fuente y 101 anotaciones `@Test` son evidencia de unidades no ejecutadas en esta iteracion. Los 19 XML/102 resultados Surefire son historicos y no validan el HEAD.
