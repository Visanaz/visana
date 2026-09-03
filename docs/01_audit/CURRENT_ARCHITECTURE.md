# Arquitectura actual AS-IS

## Vista confirmada

```text
Cliente HTTP autenticado con JWT
  -> Spring Security / Keycloak converter
  -> REST controllers: orders, network-nodes
  -> casos de uso application
  -> modelos de dominio y puertos
  -> adaptadores JPA localizados (orders, commissions)
  -> MySQL / Flyway declarados

Confirmacion de orden
  -> evento Spring local OrderPaidEvent
  -> OrderPaidEventListener
  -> CalculateCommissionsService
  -> UnilevelCompensationCalculatorService
  -> CommissionRepository
```

No se ejecuto la cadena. El diagrama expresa dependencias observadas en el codigo, no comportamiento runtime validado.

## Entradas HTTP localizadas

| Ruta | Accion | Cadena demostrada |
|---|---|---|
| `POST /api/v1/orders` | Crear orden | `OrderController.java:21-47` -> `CreateOrderService` -> `OrderRepository` -> `OrderPersistenceAdapter`/JPA. |
| `POST /api/v1/orders/{orderId}/pay` | Confirmar pago | `OrderController.java:52-68` -> `ConfirmOrderPaymentService` -> `Order.confirmPayment()` -> publica evento. El texto OpenAPI declara simulacion del pago. |
| `POST /api/v1/network-nodes` | Crear nodo | `NetworkNodeController.java:16-40` -> `CreateNetworkNodeService` -> `NetworkNodeRepository` importado pero no localizado. |

## Reglas tecnicas observadas

- `Order.confirmPayment()` no permite orden vacia, ya pagada o cancelada (`Order.java:68-84`).
- La confirmacion publica `OrderPaidEvent` despues de persistir la orden (`ConfirmOrderPaymentService.java:29-34`).
- El listener consume el evento de Spring (`OrderPaidEventListener.java:17-19`).
- El calculador itera como maximo ocho ancestros y usa un mapa de porcentajes externo (`UnilevelCompensationCalculatorService.java:31,59-93`).
- El servicio solicita upline con profundidad 8 y persiste comisiones no vacias (`CalculateCommissionsService.java:49,77`).
- `SettlementService` puede generar un `Payout` desde una cuenta con saldo positivo, pero no se localizo ruta, repositorio ni disparador que lo conecte a la cadena de orden/comision.

## Seguridad y control de acceso - evidencia estatica

`SecurityConfig.java:24-32` deshabilita CSRF, establece sesion stateless, deja publicos OpenAPI/Swagger y exige autenticacion en `/api/v1/**`. El emisor JWT se configura en `application.properties:16`. `visana-realm-export.json` declara los roles `SUPER_ADMIN`, `DISTRIBUTOR`, `AFFILIATE` y `EXTERNAL_CUSTOMER`.

HECHO: los dos controllers localizados reciben un JWT y extraen `tenant_id` al crear orden/nodo. PENDIENTE: no se valido emision de token, mapeo efectivo de roles, aislamiento por tenant, autorizacion por recurso ni comportamiento CSRF en entorno desplegado. No se evaluo explotacion.

## Persistencia demostrada y limites

`OrderPersistenceAdapter` y `CommissionPersistenceAdapter` son componentes JPA. La migracion `V1` solo estructura ordenes, items y comisiones. El codigo de compensacion requiere los puertos `GenealogyProviderPort`, `QualificationProviderPort` y `CommissionPlanProviderPort`; no se localizaron adaptadores de infraestructura para estos puertos en `src/main/java`.

Adicionalmente, `CreateOrderService.java:15`, `CreateNetworkNodeService.java:6` y `UseCaseBeanConfig.java:61,68` importan o declaran `NetworkNodeRepository`, pero no se localizo ese archivo bajo `src/main/java/com/visana/erp/network/application`. [TRAZABILIDAD_INCOMPLETA]: la compilacion/arranque no se ejecutaron; por tanto se registra como vacio estatico, no como fallo runtime confirmado.

`target/classes` conserva el `.class` de ese puerto y de un adaptador de persistencia de red, aunque sus fuentes no estan en HEAD. [CONTRADICCION]: el artefacto compilado y el arbol fuente versionado no representan el mismo conjunto de tipos demostrable. Los informes de pruebas siguen siendo historicos y no sustituyen una compilacion controlada posterior.

## Aislamiento por tenant y autorizacion de recursos - limite estatico

HECHO: `OrderController` introduce `tenant_id` del JWT al crear la orden; `CreateOrderService` localiza el afiliado por ID y construye la orden con el tenant del comando (`OrderController.java:40-41`, `CreateOrderService.java:33-42`). No se observa una comparacion entre el `tenantId` del nodo recuperado y el tenant del comando. Para confirmar pago, el controller recibe JWT pero no usa su `tenant_id`; el servicio busca solo por `orderId` (`OrderController.java:54-65`, `ConfirmOrderPaymentService.java:24-34`). La creacion de nodo verifica que el patrocinador este activo, pero tampoco se observa comparacion de tenant (`CreateNetworkNodeService.java:27-50`).

INFERENCIA: si una implementacion futura de los puertos permite recuperar IDs de otros tenants, estas rutas necesitarian una decision de aislamiento por recurso. No se intento acceso cruzado ni se declara una vulnerabilidad explotada. Tampoco se localizaron anotaciones de autorizacion por rol (`@PreAuthorize`, `@Secured` o `@RolesAllowed`) en el fuente inspeccionado; la configuracion demostrada exige autenticacion general para `/api/v1/**`.

## Trazabilidad negocio - implementacion nueva

| Etapa | Evidencia | Estado |
|---|---|---|
| Usuario / rol | JWT y realm Keycloak | [PARCIALMENTE_IMPLEMENTADA] |
| Afiliado / patrocinador / genealogia | `GenealogyNode`, `SponsorId`, servicio de nodo | [PARCIALMENTE_IMPLEMENTADA] por puerto faltante |
| Venta / orden | Orden, items, controller y JPA | [PARCIALMENTE_IMPLEMENTADA] |
| Pago | Metodo que confirma y documenta simulacion | [PARCIALMENTE_IMPLEMENTADA] |
| Volumen / calificacion | Evaluador y modelos puros | [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO] |
| Comision | Evento, calculador y repositorio JPA | [PARCIALMENTE_IMPLEMENTADA] |
| Ledger / liquidacion / pago | Dominio y settlement aislados | [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO] |

No se localizo frontend que permita demostrar la traza `FRONTEND -> API -> BACKEND` dentro de SRC-001.

## Documentacion interna frente al fuente

SRC-008 declara el flujo `SALE -> ORDER -> PAYMENT CONFIRMED -> VOLUME ENGINE -> QUALIFICATION -> GENEALOGY -> COMPENSATION ENGINE -> LEDGER -> SETTLEMENT -> PAYOUT`. El fuente localizado enlaza la confirmacion directamente al listener de compensacion; consulta genealogia y calificacion mediante puertos, pero no localiza `Volume Engine`, persistencia de calificacion, ledger ni settlement como parte de ese evento. [CONTRADICCION] documental/implementacion.

Tambien, `.agents/modules/compensation-domain.md` describe una validacion de estado `PAID` de una `Order`, mientras que `UnilevelCompensationCalculatorService.calculateUnilevelCommissions(...)` recibe `OrderId` y `Money`, no una `Order` ni `OrderStatus`. Hay incluso un caso de prueba de orden pendiente comentado. Esta observacion no determina la regla correcta; muestra que la documentacion de modulo no es evidencia suficiente de la implementacion vigente.
