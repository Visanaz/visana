# Flujo de compensacion AS-IS - Java fuente vigente

## PAYMENT_SIMULATION_PATH

| Paso | Fuente vigente | Estado |
|---|---|---|
| Interfaz HTTP | `POST /api/v1/orders/{orderId}/pay` | [IMPLEMENTADA] como endpoint autenticado. |
| Confirmacion | `OrderController` denomina la accion simulacion; crea `ConfirmOrderCommand` | [IMPLEMENTADA] como simulacion. |
| Estado | `ConfirmOrderPaymentService` recupera orden, llama `order.confirmPayment()` y guarda | [IMPLEMENTADA] en fuente. |
| Callback/gateway | No localizado | [NO_EVIDENCIADO]. |
| Idempotencia externa | Orden rechaza segundo `PAID`; no hay clave/proveedor/webhook | [PARCIALMENTE_IMPLEMENTADA]. |
| Trigger | Crea/publica `OrderPaidEvent` mediante Spring `ApplicationEventPublisher` | [IMPLEMENTADA] en fuente. |

## Compensacion

`OrderPaidEventListener` invoca `CalculateCommissionsUseCase`. El servicio consulta plan, upline hasta 8 y calificacion mensual; el calculador produce `Commission(CALCULATED, UNILEVEL_BONUS)` para cada ancestro calificado y `CommissionPersistenceAdapter` puede guardar la lista.

| Dependencia requerida | Interface fuente | Implementacion fuente HEAD | Consecuencia |
|---|---|---|---|
| Plan | `CommissionPlanProviderPort` | No localizada | Porcentaje efectivo no trazable. |
| Upline | `GenealogyProviderPort` | No localizada | Ancestros, directos/indirectos y depth no trazables. |
| Calificacion | `QualificationProviderPort` | No localizada | Regla/estado calificatorio no trazable. |
| Comision | `CommissionRepository` | `CommissionPersistenceAdapter` | Persistencia de comision es la unica salida localizada. |
| Nodo red | `NetworkNodeRepository` importado | Fuente ausente; solo class historico | Flujo de red no reconstruible desde HEAD. |

## TEAM_SALES_PATH

```text
Sale -> qualifying sale? -> personal volume -> team volume -> genealogy -> period -> qualification
```

En Java solo existen `AffiliateMetrics.teamSales`, `LevelQualificationRule.isSatisfiedBy(...)` y `QualificationEvaluatorService`; no se localizo writer, reader real, repository, adapter, tabla Flyway ni test de integracion de la ruta. En SQL legado hay `user_volumes` y `distributor_monthly_volumes`, pero `commission_plan_sale_types` siembra `counts_for_team_sales=0` para compra, recompra y afiliacion. Resultado: [TRAZABILIDAD_INCOMPLETA], DG-03 P0.

## Ledger, settlement y payment

```text
Java demostrado: Commission (CALCULATED/REVERSED)     [sin caller a Ledger]
Java demostrado: LedgerAccount -> SettlementService -> Payout(PENDING -> PROCESSING -> PAID)
No demostrado: Commission -> LedgerTransaction -> saldo -> Payout persistido -> proveedor de pago
```

El AS-IS historico registra estados `pending`, `approved`, `paid` de ledger estandar; Java no conserva esa misma cadena de estados para la comision. No se equivalen automaticamente.

## Matriz de pruebas

| Area | Unit fuente | Integracion fuente | E2E fuente | Surefire historico | Cobertura de regla |
|---|---|---|---|---|---|
| Orden/pago simulado | Si | No | No | Si | Transiciones de estado; no gateway. |
| Unilevel parametrico | Si | No | No | Si | Mapa, calificacion, profundidad, reverso; no proveedor SQL. |
| Activacion/calificacion | Si | No | No | Si | Evaluacion generica; no monto/periodo configurado real. |
| Team sales | No | No | No | No demostrable | P0 sin ruta. |
| Pools/consistencia | No | No | No | No demostrable | P0 sin calculador. |
| Ledger/settlement | Si | No | No | Si | Saldo/Payout en memoria; no persistencia/pago. |
| Pago real, impuestos, envio, rango | No | No | No | No demostrable | Sin cobertura fuente. |

Los 102 resultados Surefire no se ejecutaron en esta fase y no deben reinterpretarse como pruebas actuales del HEAD.
