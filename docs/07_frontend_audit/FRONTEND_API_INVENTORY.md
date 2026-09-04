# Frontend API Inventory — referencias estáticas

No se invocó ningún endpoint. Método, entrada y salida se transcriben del JavaScript disponible; autorización, respuesta real, estado HTTP y existencia actual son `[NO VERIFICADO]`.

| ID | Método / referencia | Consumidor | Entrada/resultado esperado estático | Clasificación |
|---|---|---|---|---|
| FA-001 | `POST /viajero/search` | `HeroSearch.js` | `FormData`: CSRF opcional, `comunaid`, `capacidad`; espera `success` y `propiedades` | Funcionalidad de búsqueda parcial |
| FA-002 | `GET /api/consultar_afiliados_nivel_inferior` | organización | Espera `afiliados[]` y `distribuidores[]` con nombre, apellido, email y conteo | Red/backoffice parcial |
| FA-003 | `GET /api/searchOrdenesDePagoMisDistribuidores/{YYYY-MM}` | bonificaciones | Espera órdenes con periodo, total, comprador, referente y usuario | Consulta financiera/backoffice |
| FA-004 | `GET /api/searchDatosImpuestos` | bonificaciones | Espera `rete_fuente` y `rete_ica` porcentuales | Parámetros tributarios de presentación |
| FA-005 | `GET /api/searchInformacionOrdenPendientePago/{usuario}/{mes}/{año}/{inicio}/{fin}` | bonificaciones | Espera compras, detalles, compras invitadas y compras de distribuidores | Detalle financiero/backoffice |
| FA-006 | `GET ${endpoint}/{query}` | `SearchHelper.min.js` | `endpoint` proviene de la vista no entregada | Contrato dinámico no recuperable |
| FA-007 | `POST upload.php` | `upload.js` | multipart `file`; objeto `Library` y servidor no entregados | Upload incompleto |

`owl.carousel.min.js` contiene URLs de Vimeo/Vzaar/YouTube propias de la librería; se clasifican como referencias de tercero, no API VISANA. `sw.js` hace `fetch(event.request)` como comportamiento de caché, no como contrato de negocio.

## Autenticación y CSRF observables

- El wrapper Axios fija `Accept` y `Content-Type`, pero no añade `Authorization` ni `Bearer`.
- Los `fetch` de organización/bonificaciones sólo construyen `Content-Type: application/json`.
- `.htaccess` preserva un encabezado `Authorization` si llega al servidor. Eso no demuestra que el cliente lo enviara.
- `HeroSearch.js` busca un input oculto cuyo nombre empieza por `csrf` y lo agrega al `FormData`; la vista y la validación servidor no están disponibles.
- La ausencia de token en estos módulos permite como máximo una **inferencia** de sesión/cookie same-origin posible; no es prueba de sesión PHP ni de controles efectivos.

## Llamadas históricamente rotas

No se encontró evidencia documental o de ejecución que pruebe 404, error o rotura histórica de FA-001 a FA-007. Se registran como `LEGACY_REFERENCE_UNVERIFIED`, no como “históricamente rotas”.
