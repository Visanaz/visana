# Plan de compensacion - catalogo formal de fuentes

**Iteracion:** Master Prompt 01, 2026-09-02  
**Alcance:** reglas documentadas y su trazabilidad con Java HEAD `5f241fe`, SQL/configuracion, AS-IS historico y pruebas.  
**No es una decision de negocio:** el termino "canonical" identifica el catalogo normalizado para validacion; no selecciona una fuente ganadora cuando hay contradiccion.

## Fuentes y regla de lectura

| ID | Fuente | Uso probatorio |
|---|---|---|
| SRC-001 | `src/main/java` del HEAD | Unica fuente de implementacion mantenible actual. |
| SRC-002 | `VISANA SAS - Plan de Compensacion-3.pdf` | Regla funcional documentada, paginas 1-4. |
| SRC-003 | `visanaco_produc.sql`, migracion y configuracion | Estructura y semillas; no prueba ejecucion. |
| SRC-004 | Expediente AS-IS v2.2 bajo `C:\visana_auditoria` | Evidencia historica PHP/CodeIgniter, no prueba Java. |
| SRC-005 | `target/classes` y Surefire | `[COMPORTAMIENTO_O_COMPONENTE_HISTORICO_EVIDENCIADO]`; nunca implementacion vigente sin fuente equivalente. |

## Modelo de configuracion identificable

### Plan base y overrides SQL

`commission_plan_levels` siembra: L1 15%, L2 10%, L3 5%, L4 4%, L5 3%, L6 2%, L7 1%, L8 2%. El PDF declara la misma secuencia para la red de afiliados.

`role_level_qualifications` declara que `custom_payout_value` no nulo sustituye el valor por defecto. Al cruzarlo con `roles`:

| Rol SQL | Configuracion encontrada | Resultado estructural SQL |
|---|---|---|
| `admin` (1) | L1-L8: 10/5/4/3/2/1/1/1; `requires_qualification=0` | Override explicito. |
| `socio` (2) | L1-L8: 10/5/4/3/2/1/1/1; `requires_qualification=0` | Override explicito. |
| `afiliado` (3) | L1-L8: `custom_payout_value=NULL`; JSON de directos, indirectos y team sales | Por comentario SQL, hereda el plan base; requiere calificacion. |
| `distribuidor` (5) | Solo L1; `custom_payout_value=NULL`, sin calificacion | Por comentario SQL, heredaria L1 base; no hay filas L2-L8. |

**Limite decisivo:** no existe caller/adaptador Java actual que lea estas tablas. `CommissionPlanProviderPort.getUnilevelPlan()` no recibe rol, afiliado ni tenant. El motor Java solo puede aplicar el `Map<Integer, Percentage>` que un proveedor externo le entregue. Por tanto, el porcentaje efectivo por usuario y rol es **[TRAZABILIDAD_INCOMPLETA]**; la semilla no demuestra la regla efectivamente aplicada.

## Cadena de calculo demostrable en Java

```text
POST /api/v1/orders/{id}/pay
 -> ConfirmOrderPaymentService: Order.confirmPayment(), save, OrderPaidEvent
 -> Spring @EventListener
 -> CalculateCommissionsService
     -> CommissionPlanProviderPort.getUnilevelPlan()
     -> GenealogyProviderPort.getUpline(affiliate, 8)
     -> QualificationProviderPort.getQualification(ancestor, YearMonth.now())
     -> UnilevelCompensationCalculatorService
     -> CommissionRepository.saveAll(...)
```

El calculador aplica `orderTotal * percentage` a cada ancestro calificado hasta nivel 8. No recibe rol, no consulta SQL, no calcula volumen ni escribe ledger. Los tres proveedores de informacion no tienen adaptador fuente localizado en HEAD. La cadena no demuestra ejecucion runtime.

## Reglas normalizadas del PDF

| Familia | Regla formal documentada | Relacion con fuente actual |
|---|---|---|
| Distribuidor | Compra inicial COP 200.000; beneficio precio 35%; invitacion 10% | Solo el monto tiene estructura SQL y evaluador Java parametrico; 35% no se localizo; 10% contradice seed 3/5. |
| Pools | 15M=1%, 25M=1,5% sobre todos distribuidores; 45M=2%, 100M=2,5% sobre equipo | Las cuatro semillas y bases existen; no se localizo calculador, participantes, reparto, ledger ni pago en Java. |
| Consistencia | 6 meses=0,5%; 12 meses=1%; sostener o aumentar | Dos semillas existen; no se localizo algoritmo de racha ni flujo financiero. |
| Unilevel | L1-L8: 15/10/5/4/3/2/1/2 | Java tiene algoritmo parametrico hasta 8, pero no fuente de plan, red ni calificacion. |
| Nivel 8 | PDF describe bolsa 2% repartida equitativamente entre quienes activen el nivel | Java calcula un porcentaje individual al ancestro L8 cuando el mapa contiene L8. No implementa bolsa ni reparto; [CONTRADICCION]. |
| Premio 1% | 1% por nivel 1-7, total teorico 7% | No se localizo SQL, Java, test o flujo; no se interpreta como duplicacion de Unilevel. |
| Activacion | Primera compra COP 200.000; recompra COP 100.000; PDF tambien dice mes de compra y 30 dias | SQL siembra 200k/100k/30 dias; Java evalua monto/dias recibidos como parametros; AS-IS historico registra 29 dias y modo mes. DG-01 sigue abierto. |
| Calificacion | N1-N8 con directos, indirectos y team sales | SQL siembra los ocho umbrales para rol afiliado; Java puede evaluar una lista generica, sin proveedor/writer de metricas. |
| Rango/rama | Un descendiente que alcanza o supera al upline desactiva pagos de la rama | No se localizo comparacion, fecha, reactivacion ni historico. |
| Periodos/pago | Corte 1-15/pago 25; 16-fin/pago 10 | SQL `periods` conserva ejemplos; Java `Period` es mensual y settlement aislado. |
| Impuestos/envio | Retencion/Rete-ICA; envio por mas de 3 productos | No se localizaron implementaciones fuente. |

## Conclusiones formales solicitadas

1. **Porcentaje final por rol/nivel:** solo puede expresarse como configuracion SQL potencial; no como resultado Java efectivo. El unico calculo vigente demostrable es `Map[nivel]` para ancestro calificado.
2. **Nivel 8:** el PDF documenta una bolsa equitativa; el calculador Java modela comision individual por ancestro. Son semanticas distintas y no se deben equivaler.
3. **Premio adicional 1% L1-L7:** es una regla documental independiente o ambigua; no existe evidencia para clasificarlo como parte de la escala Unilevel ni para pagarla dos veces.
4. **Team sales:** hay umbrales SQL/PDF y un value object Java, pero no writer, reader de datos, port implementado, persistencia fuente ni prueba de integracion. Es P0.

La matriz detallada esta en `COMPENSATION_RULE_MATRIX.md`; cada BR, sus preguntas y gates estan en `BUSINESS_RULE_CATALOG.md` y `OPEN_BUSINESS_DECISIONS.md`.
