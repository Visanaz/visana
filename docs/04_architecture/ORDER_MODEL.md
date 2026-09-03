# Order Model — Sprint 3

## Flujo implementado

`JWT -> KeycloakAuthenticatedPrincipalAdapter -> ActorResolverPort -> PlatformActor linked -> POST /api/v1/orders -> catálogo activo -> snapshot de líneas -> commerce_orders -> resource_ownerships -> audit`.

No existe enlace automático entre email, username, `affiliate_id` ni un perfil de negocio. Una identidad desconocida, no vinculada, deshabilitada o sin ownership no obtiene acceso.

| Regla técnica | Implementación | Estado |
|---|---|---|
| Dueño de una orden nueva | `owner_actor_id` FK a `platform_actors` y `resource_ownerships` explícito | IMPLEMENTADA |
| Precio/totales de cliente | DTO sólo recibe `productId`, `quantity`; servicio toma `base_price` activo y calcula | IMPLEMENTADA |
| Snapshot histórico | code/name/unit price/quantity/line total por línea | IMPLEMENTADA |
| Lectura de propio recurso | filtro por `owner_actor_id` y policy ownership | IMPLEMENTADA |
| Lectura foránea | denegación fail-closed y auditoría | IMPLEMENTADA |
| Orden heredada a actor | no se inventa mapeo | LEGACY_ORDER_OWNERSHIP_UNRESOLVED |
| Cambios/cancelación Plan 3 | sin endpoint | NO_IMPLEMENTADO |
| Pago/conciliación | fuera de Sprint 3 | GAP-011/GAP-012 |

El estado inicial es `PENDING`. La tabla admite los valores de estado ya existentes `PENDING`, `PAID`, `CANCELLED` para no introducir una taxonomía nueva, pero este sprint no agrega transición de pago, cancelación ni reverso.

## Idempotencia

No existía una base de idempotencia verificable en el clon. Se evaluó `Idempotency-Key`, pero no se persiste ni se acepta aún: una semántica de reintento necesita contrato de cliente, duración, hash de request, concurrencia, manejo de respuesta y reconciliación con el proveedor de pago. Se registra como `TS-COM-003` diferida; implementarla sin esas decisiones podría crear órdenes duplicadas o una falsa garantía de pago.
