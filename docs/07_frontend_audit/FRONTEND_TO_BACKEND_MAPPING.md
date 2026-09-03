# Frontend to Backend Mapping

## Criterio

Se compara la referencia legado contra el código/documentación actual de Plan 3, no se supone compatibilidad. El backend actual protege `/api/v1/**` y usa JWT/OIDC con `PlatformActor`; sus endpoints de red devuelven identificadores/profundidad y no PII.

| Referencia frontend | Intención observable | API Plan 3 actual | Estado de mapeo | Brecha |
|---|---|---|---|---|
| `/api/consultar_afiliados_nivel_inferior` | Afiliados/distribuidores, PII, conteo y etiqueta nivel 1 | `GET /api/v1/network/me`, `/direct`, `/ancestors`, `/descendants` | `PARTIAL_INCOMPATIBLE` | Falta definición aprobada de vista PII, perfiles, conteos y UX de red; Plan 3 no devuelve PII. |
| `/api/searchOrdenesDePagoMisDistribuidores/{periodo}` | Órdenes para detalle de bonificación | No equivalente | `GAP` | No hay consulta de liquidaciones/beneficiarios/periodos aprobada. |
| `/api/searchDatosImpuestos` | Retenciones para cálculo visual | No equivalente | `GAP` | Impuestos/retenciones siguen DG-12; no se expone fuente de parámetros. |
| `/api/searchInformacionOrdenPendientePago/...` | Detalle de compra y pago pendiente | `GET /api/v1/orders`, `GET /api/v1/orders/{id}` sólo propios | `INCOMPATIBLE` | Actual API no es consulta de distribuidores ni detalle de comisión/payout. |
| Cálculo 10%/35%/bandas en JS | Comisión/incentivo visual | No equivalente | `GAP_BLOCKED_BY_RULES` | No se debe trasladar sin decisión de negocio, cálculo servidor y trazabilidad financiera. |
| `/viajero/search` | Búsqueda de propiedades/viajes | No equivalente | `OUT_OF_SCOPE_UNCONFIRMED` | No se halló dominio correspondiente en Plan 3. |
| `upload.php` | Carga de archivos | No equivalente | `GAP` | Requiere caso de uso, ownership, antivirus, límites y retención aprobados. |
| Activos de catálogo/carrito | Presentación de productos/compra | `GET /api/v1/products`, `/categories`; órdenes actor-owned | `VISUAL_ONLY` | No se recuperó frontend de catálogo/carrito ni contrato UI. |

## Endpoints Plan 3 relevantes para un frontend futuro

| Endpoint | Evidencia actual | Uso potencial, no implementado en este frente |
|---|---|---|
| `GET /api/v1/products`, `GET /api/v1/categories` | `CatalogController` y `COMMERCE_MODEL.md` | Catálogo autenticado. |
| `POST /api/v1/orders`, `GET /api/v1/orders`, `GET /api/v1/orders/{id}` | `OwnedOrderController` | Orden actor-owned; precio es servidor. |
| `POST /api/v1/orders/{id}/pay` | `OrderController` | Confirmación simulada, no gateway ni conciliación. |
| `GET /api/v1/network/me|direct|ancestors|descendants` | `NetworkQueryController`, `NETWORK_MODEL.md` | Red mínima sin PII. |
| `GET /api/v1/me` | `MeController` | Contrato actual de identidad técnica; UX de perfil de negocio pendiente. |

El frontend futuro no debe consumir rutas legado `/api/...` como si fueran `/api/v1`, ni deducir vínculo de email/usuario legado a `PlatformActor`.
