# Commerce Model — Sprint 3

## Disposición de evidencia existente

| Elemento previo | Evidencia | Disposición |
|---|---|---|
| `Product` | Objeto aislado con `TenantId`, precio y `isCommissionable`; no hay repositorio, API ni catálogo | KEEP_AS_LEGACY_REFERENCE. No se reutiliza `isCommissionable`, porque no hay fuente que autorice conectar catálogo con compensación. |
| `orders` / `order_items` V1 | Requieren `tenant_id`, `affiliate_id`, tipo y estado; su confirmación de pago publica un evento de dominio | KEEP_AS_LEGACY. No se mapea `affiliate_id` a `PlatformActor`. |
| `OrderController` de creación | Aceptaba `affiliateId` del cliente; no existe fuente para resolver actor a afiliado | REFACTOR: el endpoint de creación Plan 3 es actor-owned; el caso de uso legacy permanece sin exponer creación. |
| Confirmación simulada | Sigue en `POST /api/v1/orders/{id}/pay`, con gate de identidad/ownership de Sprint 2 | KEEP_AND_HARDEN. Sprint 3 no cambia pago, estados financieros ni eventos de compensación. |

## Modelo Plan 3 implementado

`product_categories` y `catalog_products` son catálogo de lectura autenticada. `ACTIVE` y `INACTIVE` son los únicos estados de visibilidad implementados. No se expone mutación porque no existe matriz fuente de roles administrativos.

`commerce_orders` pertenece obligatoriamente a `platform_actors`; `commerce_order_lines` conserva snapshots de código, nombre, precio unitario, cantidad y total. El request de creación admite exclusivamente `productId` y `quantity`; precio y totales se resuelven y calculan en servidor con producto activo.

La separación de las tablas V5/V6 de V1 es deliberada: V1 impone `affiliate_id` y representa semántica de pago/compensación heredada. Reutilizarla habría requerido inferir un vínculo actor-afiliado que DG-17 mantiene abierto.

## Contratos HTTP

Todos están bajo `/api/v1` y siguen la autenticación JWT existente. Por ausencia de evidencia actual que justifique catálogo público, la visibilidad queda `AUTHENTICATED_CATALOG_DEFAULT`.

- `GET /products`, `GET /categories`: paginación `page`/`size` y orden limitado.
- `POST /orders`: identidad vinculada; crea orden `PENDING` actor-owned, asigna ownership y emite auditoría.
- `GET /orders`, `GET /orders/{id}`: sólo el actor propietario; acceso foráneo o ownership ausente falla cerrado.
- No hay PATCH/PUT/DELETE de producto, categoría u orden. No hay hard delete.

Las respuestas de error se resuelven mediante `ProblemDetail` global y preservan `correlationId` cuando está disponible. Eventos de auditoría de este sprint: `ORDER_CREATED`, `OWNERSHIP_ASSIGNED`, `ORDER_READ_DENIED` y `AUTHORIZATION_DENIED`; no incluyen precio, subject OIDC ni PII.

## Límites explícitos

COP se representa como `DECIMAL(19,4)` coherente con `Money`; no se implementa impuesto, envío, descuentos, POS, proveedor de pago, callback, liquidación, commissionable-volume, red ni compensación. Una orden Plan 3 no confirma pago ni emite evento financiero.
