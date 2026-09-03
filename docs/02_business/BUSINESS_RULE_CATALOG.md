# Catalogo formal de reglas de negocio

Cada fila condensa el formato obligatorio. `N/D` significa no demostrado por las fuentes inspeccionadas; no significa valor cero.

| BR-ID | Categoria / beneficiario | Input y precondiciones | Formula, periodo y output | Persistencia, ledger, pago, reverso | Fuentes / prueba / estado / gate |
|---|---|---|---|---|---|
| BR-DIST-001 | DISTRIBUTOR / distribuidor | Primera compra >= COP 200.000 | Activa acceso/beneficios; vigencia en DG-01 | SQL plan; no ledger/pago/reverso J | P p1, S plan, J evaluador generico, T unitario; [PARCIALMENTE_IMPLEMENTADA]; DG-01 |
| BR-DIST-002 | DISTRIBUTOR / distribuidor | Compra de producto | Beneficio comercial 35%; base no definida | N/D | P p1; NONE; [DOCUMENTADA_NO_VERIFICADA]; DG-09 |
| BR-DIST-003 | DISTRIBUTOR / invitador | Invitacion de distribuidor | P=10%; S=3% hasta 5 activos, 5% desde 6 | Seed tiers; no J/ledger/pago | P p1, S tiers, H AUD-002; [CONTRADICCION]; DG-02 |
| BR-DIST-POOL-001 | POOL / todos distribuidores | Ventas mensuales distribuidor >15M | 1%; PDF indica reparto equitativo | tabla pools `all_distributors`; sin writer, ledger/pago/reverso | P p1, S pools; [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO]; DG-06,DG-09 |
| BR-DIST-POOL-002 | POOL / todos distribuidores | Ventas mensuales distribuidor >25M | 1,5%; PDF indica reparto equitativo | tabla pools `all_distributors`; sin writer, ledger/pago/reverso | P p1, S pools; [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO]; DG-06,DG-09 |
| BR-DIST-POOL-003 | POOL / distribuidores del equipo | Ventas mensuales del equipo distribuidor >45M | 2%; PDF indica reparto equitativo | tabla pools `distributor_team`; sin writer, ledger/pago/reverso | P p1, S pools; [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO]; DG-06,DG-09 |
| BR-DIST-POOL-004 | POOL / distribuidores del equipo | Ventas mensuales del equipo distribuidor >100M | 2,5%; PDF indica reparto equitativo | tabla pools `distributor_team`; sin writer, ledger/pago/reverso | P p1, S pools; [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO]; DG-06,DG-09 |
| BR-DIST-CONS-001 | CONSISTENCY / distribuidores | 6 meses sosteniendo/aumentando; definicion de racha N/D | 0,5% | tabla consistency; sin motor, ledger/pago/reverso | P p2, S bonus; [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO]; DG-06 |
| BR-DIST-CONS-002 | CONSISTENCY / distribuidores | 12 meses sosteniendo/aumentando; definicion de racha N/D | 1% | tabla consistency; sin motor, ledger/pago/reverso | P p2, S bonus; [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO]; DG-06 |
| BR-AFF-UNI-001 | AFFILIATE / ancestro calificado L1 | Orden confirmada, upline L1, mapa, calificacion | total orden * 15%; `YearMonth.now()` | `commissions`; sin ledger/pago; reverso aislado | P p2, S base/override, J calculador, T unitario; [TRAZABILIDAD_INCOMPLETA]; DG-09 |
| BR-AFF-UNI-002 | AFFILIATE / ancestro calificado L2 | Orden confirmada, upline L2, mapa, calificacion | total orden * 10%; `YearMonth.now()` | `commissions`; sin ledger/pago; reverso aislado | P p2, S base/override, J calculador, T unitario; [TRAZABILIDAD_INCOMPLETA]; DG-09 |
| BR-AFF-UNI-003 | AFFILIATE / ancestro calificado L3 | Orden confirmada, upline L3, mapa, calificacion | total orden * 5%; `YearMonth.now()` | `commissions`; sin ledger/pago; reverso aislado | P p2, S base/override, J calculador, T unitario; [TRAZABILIDAD_INCOMPLETA]; DG-09 |
| BR-AFF-UNI-004 | AFFILIATE / ancestro calificado L4 | Orden confirmada, upline L4, mapa, calificacion | total orden * 4%; `YearMonth.now()` | `commissions`; sin ledger/pago; reverso aislado | P p2, S base/override, J calculador, T unitario; [TRAZABILIDAD_INCOMPLETA]; DG-09 |
| BR-AFF-UNI-005 | AFFILIATE / ancestro calificado L5 | Orden confirmada, upline L5, mapa, calificacion | total orden * 3%; `YearMonth.now()` | `commissions`; sin ledger/pago; reverso aislado | P p2, S base/override, J calculador, T unitario; [TRAZABILIDAD_INCOMPLETA]; DG-09 |
| BR-AFF-UNI-006 | AFFILIATE / ancestro calificado L6 | Orden confirmada, upline L6, mapa, calificacion | total orden * 2%; `YearMonth.now()` | `commissions`; sin ledger/pago; reverso aislado | P p2, S base/override, J calculador, T unitario; [TRAZABILIDAD_INCOMPLETA]; DG-09 |
| BR-AFF-UNI-007 | AFFILIATE / ancestro calificado L7 | Orden confirmada, upline L7, mapa, calificacion | total orden * 1%; `YearMonth.now()` | `commissions`; sin ledger/pago; reverso aislado | P p2, S base/override, J calculador, T unitario; [TRAZABILIDAD_INCOMPLETA]; DG-09 |
| BR-AFF-UNI-008 | AFFILIATE / nivel 8 | PDF: activacion de nivel; J: ancestro calificado | P bolsa 2% equitativa; J total * 2% individual | `commissions`; sin bolsa/ledger/pago | P p2, S L8=2, J calculador, T individual; [CONTRADICCION]; DG-09 |
| BR-AFF-PRIZE-001 | AFFILIATE / participantes por nivel | Participacion nivel 1-7 | PDF: 1% mensual por nivel, 7% teorico; semantica N/D | N/D | P p4; NONE; [DOCUMENTADA_NO_VERIFICADA]; DG-07 |
| BR-ACT-001 | ACTIVATION / afiliado o distribuidor | Primera compra >= COP 200.000 | Activo; PDF mes de compra y 30 dias; J recibe dias externos | S plan; no flujo J de persistencia/ledger/pago | P p3-p4, S 30 dias, J evaluador, H 29/mes, T unidad; [CONTRADICCION]; DG-01,DG-11 |
| BR-ACT-002 | ACTIVATION / afiliado o distribuidor | Recompra mensual >= COP 100.000 | Mantener activo/calificado; mes valido N/D | S plan; no J writer/ledger/pago | P p3, S 100k; [DOCUMENTADA_NO_VERIFICADA]; DG-01 |
| BR-QUAL-001 | QUALIFICATION / afiliado N1 | Afiliacion; umbrales SQL en cero | Nivel 1 | SQL JSON rol afiliado; no writer/reader ni ledger/pago | P p3, S JSON, J evaluador, T unidad; [TRAZABILIDAD_INCOMPLETA]; DG-03 |
| BR-QUAL-002 | QUALIFICATION / afiliado N2 | 5 directos, 0 indirectos, 3M team sales | Nivel 2 si las tres condiciones pasan (AND Java) | SQL JSON rol afiliado; no writer/reader ni ledger/pago | P p3, S JSON, J evaluador, T unidad; [TRAZABILIDAD_INCOMPLETA]; DG-03 |
| BR-QUAL-003 | QUALIFICATION / afiliado N3 | 7 directos, 25 indirectos, 25M team sales | Nivel 3 si las tres condiciones pasan (AND Java) | SQL JSON rol afiliado; no writer/reader ni ledger/pago | P p3, S JSON, J evaluador, T unidad; [TRAZABILIDAD_INCOMPLETA]; DG-03 |
| BR-QUAL-004 | QUALIFICATION / afiliado N4 | 9 directos, 110 indirectos, 80M team sales | Nivel 4 si las tres condiciones pasan (AND Java) | SQL JSON rol afiliado; no writer/reader ni ledger/pago | P p3, S JSON, J evaluador, T unidad; [TRAZABILIDAD_INCOMPLETA]; DG-03 |
| BR-QUAL-005 | QUALIFICATION / afiliado N5 | 12 directos, 350 indirectos, 250M team sales | Nivel 5 si las tres condiciones pasan (AND Java) | SQL JSON rol afiliado; no writer/reader ni ledger/pago | P p3, S JSON, J evaluador, T unidad; [TRAZABILIDAD_INCOMPLETA]; DG-03 |
| BR-QUAL-006 | QUALIFICATION / afiliado N6 | 15 directos, 1.200 indirectos, 1.000M team sales | Nivel 6 si las tres condiciones pasan (AND Java) | SQL JSON rol afiliado; no writer/reader ni ledger/pago | P p3, S JSON, J evaluador, T unidad; [TRAZABILIDAD_INCOMPLETA]; DG-03 |
| BR-QUAL-007 | QUALIFICATION / afiliado N7 | 20 directos, 3.500 indirectos, 2.500M team sales | Nivel 7 si las tres condiciones pasan (AND Java) | SQL JSON rol afiliado; no writer/reader ni ledger/pago | P p3, S JSON, J evaluador, T unidad; [TRAZABILIDAD_INCOMPLETA]; DG-03 |
| BR-QUAL-008 | QUALIFICATION / afiliado N8 | 25 directos, 15.000 indirectos, 30.000M team sales | Nivel 8 si las tres condiciones pasan (AND Java) | SQL JSON rol afiliado; no writer/reader ni ledger/pago | P p3, S JSON, J evaluador, T unidad; [TRAZABILIDAD_INCOMPLETA]; DG-03 |
| BR-AFF-RANK-001 | QUALIFICATION / upline-rama | Descendiente alcanza/supera nivel upline | Pagos de rama se desactivan; comparador, fecha y reactivacion N/D | N/D | P p4; NONE; [DOCUMENTADA_NO_VERIFICADA]; DG-08 |
| BR-PER-001 | SETTLEMENT / beneficiario | Venta 1-15 o 16-fin | Pago 25 o 10; fecha calculo/aprobacion N/D | S `periods`, orden pago legado; J `Period` mensual y `Payout` aislado | P p4, S periods, J Period/Payout, T unidad; [TRAZABILIDAD_INCOMPLETA]; DG-05 |
| BR-TAX-001 | TAX / comisionista | Pago de comision | Retencion/Rete-ICA; tasas, base y responsable N/D | N/D | P p4; NONE; [DOCUMENTADA_NO_VERIFICADA]; DG-12 |
| BR-SHIP-001 | SHIPPING / comprador | Mas de 3 productos | Compania asume envio; definicion de conteo/orden N/D | N/D | P p4; NONE; [DOCUMENTADA_NO_VERIFICADA]; DG-13 |
| BR-COMP-CONF-001 | COMPENSATION / rol-nivel | Plan default, rol, requisitos | SQL: override no nulo reemplaza default; Java recibe mapa global sin rol | SQL planes/roles; sin adaptador/caller Java | S 117-188,1128-1190; T mocks; [TRAZABILIDAD_INCOMPLETA]; DG-09 |
| BR-PAY-001 | PAYMENT / orden | POST `/orders/{id}/pay`, orden no vacia/no pagada/no cancelada | Marca PAID, persiste y publica evento; es simulacion | `orders`, `commissions`; no callback/idempotency/gateway | J controller/servicio, T unidad; [IMPLEMENTADA] simulacion; gate P0 |
| BR-LEDGER-001 | LEDGER/SETTLEMENT / afiliado | Saldo positivo de `LedgerAccount` | Debita todo y crea `Payout PENDING`; PENDING->PROCESSING->PAID | No repositorio, caller, gateway ni enlace desde Commission | J ledger, T unidad, H pending/approved/paid; [TRAZABILIDAD_INCOMPLETA]; DG-04,DG-05,DG-10 |

## Formula efectiva que si puede demostrarse

Para un ancestro `a` en posicion `n` de la lista upline, Java genera una `Commission` solo si `n <= 8`, el mapa tiene `n` y `qualification[a].isQualified()` es verdadero. El monto es `orderTotal.multiply(map[n])`. No hay evidencia fuente de que `map[n]` provenga de la tabla SQL, de que el mapa sea por rol, de un pool, de una base neta o de volumen calificable.

## Preguntas transversales

Q-COMP-004: ¿el total usado por Java equivale a venta bruta, neta, IVA excluido o volumen calificable?
Q-COMP-005: ¿un usuario puede recibir simultaneamente Unilevel, premio 1%, pool y bono de consistencia sobre el mismo evento?
Q-LEDGER-001: ¿que evento autoriza `CALCULATED` a convertirse en saldo liquidable y despues pagado?
Q-VOL-001: ¿que tipos de venta incrementan volumen personal/equipo y en que periodo?
