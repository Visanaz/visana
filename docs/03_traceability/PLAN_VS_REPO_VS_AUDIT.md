# Plan 3 frente a Java actual y auditoria PHP historica

**Iteracion:** Master Prompt 02, 2026-09-02.  
**Regla de lectura:** SRC-001 (Java HEAD `5f241fe`) determina lo mantenible; SRC-004 (PHP/CodeIgniter) es continuidad historica, no una especificacion ni implementacion Java; SRC-005 solo demuestra componente historico. No se ejecutaron aplicaciones, pruebas, migraciones ni integraciones.

## Resumen de disposiciones recomendadas

Las disposiciones son recomendaciones de backlog, no cambios autorizados. `BLOCKED_BY_DECISION` nunca selecciona una regla de negocio.

| Unidad | Plan 3 / necesidad observada | Java actual | PHP/SQL historico | Brecha y disposicion recomendada |
|---|---|---|---|---|
| Identity y users | Identidad de actor y pertenencia requerida por orden/red | JWT y `TenantId`; no fuente actual de usuarios | `users`, roles y empresa en SQL | [GAP-PARTIAL] **KEEP_AND_HARDEN** identidad; definir mapeo legado. |
| Authorization | Autorizacion por recurso para orden, pago y patrocinador | Autenticacion general `/api/v1/**`; roles extraidos; sin regla fuente por recurso | No se equipara automaticamente | [GAP-SECURITY] **KEEP_AND_HARDEN**. |
| Roles | Resolver plan/override por rol | El calculador recibe mapa global sin rol | Semillas de roles/overrides | [GAP-BUSINESS-DECISION] **REFACTOR** tras DG-09. |
| Catalog / POS | Producto, precio, venta y POS segun alcance VISANA | `Product` es dominio aislado; no catalogo/POS fuente | Estructuras de venta legadas | [GAP-NOT-IMPLEMENTED] **IMPLEMENT_NEW** si queda en alcance. |
| Orders / commerce | Orden con estado y total | Orden, items y JPA V1; estado PAID simulado | `sales` legado | [GAP-PARTIAL] **KEEP_AND_HARDEN**. |
| Payments | Confirmacion verificable antes de compensar | Endpoint simulado publica evento local | Operaciones de pago/ordenes historicas no prueban gateway | [GAP-INTEGRATION] **REFACTOR**; proveedor sin decidir. |
| Affiliation / network | Patrocinador, directos/indirectos y red | Dominio y controller; `NetworkNodeRepository` falta en fuente | `user_genealogy` | [GAP-MISSING-PORT]/[GAP-MISSING-ADAPTER] **SOURCE_RECOVERY_REQUIRED** antes de reutilizar. |
| Genealogy | Upline ordenado para 8 niveles | `GenealogyProviderPort` sin adaptador actual | Estructura/uso PHP historico parcial | [GAP-MISSING-ADAPTER] **REIMPLEMENTATION_CANDIDATE**. |
| Activation / repurchase | COP 200.000, COP 100.000 y vigencia decidida | Evaluador generico recibe monto/dias; sin caller/persistencia | SQL 30 dias; PHP evidencia 29/current-month | [GAP-BUSINESS-DECISION] **KEEP_AND_HARDEN** condicionado a DG-01/DG-11. |
| Qualification | N1-N8 con metricas reales | Evaluador AND generico; sin reader/writer/adapter | JSON por rol y umbrales | [GAP-PARTIAL] **REFACTOR**. |
| Volume / team sales | Venta calificable a volumen personal/equipo/periodo | Solo modelos de metricas; sin ruta fuente | `user_volumes`, `distributor_monthly_volumes`; uso PHP parcial | [GAP-NOT-IMPLEMENTED] **IMPLEMENT_NEW**. |
| Compensation / Unilevel | L1-L8 con porcentaje efectivo e idempotencia | Calculador parametrico, maximo 8, persistencia de comision; dependencias sin adaptador | Plan base/overrides; motor PHP historico | [GAP-PARTIAL] **KEEP_AND_HARDEN** para semantica individual; DG-09 decide configuracion. |
| Nivel 8 | 2% individual o bolsa equitativa | Solo comision individual si el mapa tiene L8 | SQL base 2%, sin bolsa | [GAP-CONFLICT] A: **KEEP_AND_HARDEN**; B: **IMPLEMENT_NEW**. DG-09 bloquea. |
| Distribuidor | 35%, invitacion 10%, pools y consistencia | No localizado | Seeds 3%/5%, pools y bonos sin uso probado | [GAP-CONFLICT]/[GAP-NOT-IMPLEMENTED] **IMPLEMENT_NEW** tras DG-02/DG-06/DG-09. |
| Restriccion de rama | Bloqueo por rango descendente | No localizado | No localizado | [GAP-NOT-IMPLEMENTED] **IMPLEMENT_NEW** tras DG-08. |
| Ledger / settlement / payout | Comision aprobada, liquidada y pagada | Modelos aislados; sin puente desde Commission ni persistencia/pago | Ledger `pending/approved/paid`; cierre no demostrado | [GAP-PARTIAL] **REFACTOR** + componentes nuevos. |
| Reversal | Reverso coherente de todos los efectos | `Commission.reverse()` crea objeto opuesto | No demuestra rollback integral | [GAP-PARTIAL] **REFACTOR** tras DG-10. |
| Payment order / tax | Orden de pago, retencion y Rete-ICA | No localizado | Tabla de ordenes; no flujo cerrado | [GAP-NOT-IMPLEMENTED] **IMPLEMENT_NEW** tras DG-05/DG-12. |
| Shipping | Regla >3 productos | No localizado | No localizado | [GAP-NOT-IMPLEMENTED] **IMPLEMENT_NEW** tras DG-13 si queda en alcance. |
| Reporting / notifications | Consulta, reporte y notificacion operativa | No localizado | Reportes PHP historicos; no equivalencia Java | [GAP-NOT-IMPLEMENTED] **IMPLEMENT_NEW** tras alcance funcional. |
| Audit log | Evidencia auditable de eventos financieros | No se localizo modulo fuente | No se equipara automaticamente | [GAP-NOT-IMPLEMENTED] **IMPLEMENT_NEW**. |
| Security | JWT, roles, secreto/config, excepciones y trazabilidad | JWT/Keycloak converter, configuracion stateless, handler global | Secretos presentes sin reproducir | [GAP-SECURITY] **KEEP_AND_HARDEN**; no se hizo pentest. |
| Migration / data | Puente reproducible entre SQL legado y V1 UUID | Servicio ignora guardado genealogico/items y captura errores | 34 tablas con IDs enteros; V1 tres tablas UUID | [GAP-DATA] **REWRITE** sujeto a mapeo/conciliacion aprobado. |
| QA / infrastructure | Prueba trazable de reglas P0 y entorno reproducible | 17 fuentes de test/101 `@Test`; sin IT/E2E; no ejecutadas | 19 XML/102 resultados historicos | [GAP-MISSING-TEST]/[GAP-MISSING-RUNTIME] **IMPLEMENT_NEW** QA; artefactos versionados deben aislarse. |
| Frontend | Fuente para traza usuario→API | No existe fuente frontend en este clon | PHP previo no es frontend de este repo Java | [GAP-NOT-IMPLEMENTED] **BLOCKED_BY_SOURCE**; APIs requeridas deben validarse por modulo. |

## Cadena financiera: punto exacto de ruptura

| Flecha requerida | Java fuente actual | Estado | Evidencia historica | GAP |
|---|---|---|---|---|
| Payment confirmado -> evento | `ConfirmOrderPaymentService` guarda y publica `OrderPaidEvent` | PARTIAL: origen es simulacion | PHP/SQL no acredita gateway | GAP-011 |
| Evento -> calculo | Listener llama `CalculateCommissionsService` | PARTIAL: puertos plan/upline/calificacion sin adaptador | PHP `CommissionEngineService` historico | GAP-004 |
| Calculo -> Commission | Calculador crea `Commission(CALCULATED)` y repositorio la guarda | PARTIAL: no hay fuente de datos ni idempotencia demostrada | Ledger PHP historico | GAP-004 |
| Commission -> ledger | No caller localizado | MISSING | insercion legacy `pending` observada; aprobacion no demostrada | GAP-012 |
| Ledger -> approval | No localizado | MISSING | `pending/approved/paid` parcial historico | GAP-012 |
| Approval -> settlement | `SettlementService` aislado sobre `LedgerAccount` | PARTIAL | orden de pago historica sin cierre | GAP-012 |
| Settlement -> payout order/payment/tax | `Payout` de dominio aislado | MISSING | tabla historica, sin flujo cerrado | GAP-012 / GAP-028 |
| Reversal -> efectos derivados | Solo objeto `Commission` opuesto | PARTIAL | no equivalencia demostrada | GAP-013 |

## Flujo de configuracion actual y doble fuente de verdad

```text
PDF / SQL legado: plan base -> role_level_qualifications -> posible custom_payout_value
Java actual:    CommissionPlanProviderPort.getUnilevelPlan() -> Map<nivel, porcentaje>
                                                       [adaptador/caller no localizado]
```

HECHO: Java no recibe rol, afiliado o tenant en `getUnilevelPlan()`. HECHO: SQL declara un override cuando `custom_payout_value` no es nulo. CONCLUSION: no hay precedencia efectiva demostrable ni se puede afirmar que el seed sea la fuente en ejecucion. Esto es GAP-003 P0 y DG-09.

## Impacto tecnico de alternativas sin elegir negocio

| Decision abierta | Alternativa | Impacto tecnico que debera validarse |
|---|---|---|
| DG-01/DG-11 | Mes calendario o ventana de 30 dias | Fuente de fecha, limite de vigencia, snapshot de periodo y pruebas de borde. Java actual solo recibe parametros. |
| DG-02 | Invitacion fija 10% o tier 3%/5% | Resolver de regla y fuente unica; elegibilidad/periodo; prevencion de doble calculo. |
| DG-09 / L8 | 2% individual o bolsa equitativa | Individual puede usar calculador parametrico endurecido; bolsa exige acumulacion, participantes, reparto, precision, persistencia y liquidacion. |
| DG-07 | Premio 1% L1-L7 | Trigger, elegibilidad, base, posible bolsa, ledger y separacion de Unilevel. |
| DG-08 | Restriccion de rama | Snapshot de rango/red/periodo, bloqueo, reactivacion e impacto de historico. |
| DG-10 | Devoluciones/anulaciones | Regla de compensacion, volumen, calificacion, ledger, pago y reporte; el metodo aislado no decide el alcance. |

## Fuente historica: limites de continuidad

El expediente PHP acredita estructuras y partes de flujos: `CommissionEngineService`, `UserActivationService`, `user_genealogy`, volumen, `ledger_transactions` y `ordenes_pago_comisiones`. No acredita que esos comportamientos sean vigentes, que se ejecutaran correctamente ni que Java los conserve. En particular, el PHP deja pendiente la transicion estandar `pending -> approved` y el uso efectivo de ordenes de pago; por ello no es una solucion recuperable por inferencia.
