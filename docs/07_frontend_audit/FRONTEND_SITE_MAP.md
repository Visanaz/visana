# Frontend Site Map — recuperado de evidencia parcial

## Límite de rutas

El rewrite de `.htaccess` entrega al front controller cualquier URL que no sea archivo/directorio físico, pero no incluye la tabla de rutas CodeIgniter. Al faltar `app/Config/Routes.php` y las vistas, las rutas URL exactas son `[NO EVIDENCIADO]`. Este mapa nombra módulos observables, no URLs inventadas.

| ID | Módulo/página candidata | Público / autenticado / admin | Evidencia | Ruta | Estado |
|---|---|---|---|---|---|
| F-P01 | Entrada web CodeIgniter | Indeterminado | `index.php`, `.htaccess` | No recuperable | `[ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO]` |
| F-P02 | Búsqueda de viajeros/propiedades | Público candidato | `HeroSearch.js`, selectores `.hero-filter`, `#ciudad`, `#pasajeros` | Endpoint `/viajero/search`; página no recuperable | `[PARCIALMENTE_IMPLEMENTADA]` |
| F-P03 | Organización de afiliados/distribuidores | Backoffice candidato | directorio `backoffice/organizacion`, IDs `contenedor-afiliados`/`contenedor-distribuidores` | Página no recuperable | `[PARCIALMENTE_IMPLEMENTADA]` |
| F-P04 | Bonificaciones y detalle de orden pendiente de pago | Backoffice candidato | directorio `backoffice/bonificaciones`, modal y tabla | Página no recuperable | `[PARCIALMENTE_IMPLEMENTADA]` |
| F-P05 | Carga de archivos | Indeterminado | `upload.js`, `#drop-area` | Página y servidor `upload.php` no entregados | `[PARCIALMENTE_IMPLEMENTADA]` |
| F-P06 | Documentos legales descargables | Público candidato | 10 PDFs bajo `docs/` | URL/página de enlace no recuperable | `[ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO]` |

## Áreas no demostradas

No hay una vista o ruta entregada que demuestre login, logout, registro, recuperación de contraseña, perfil, carrito, checkout, pago, gestión de catálogo, administración de usuarios, liquidación o reporte. Los activos `cart.svg`, `user.svg`, `avalpay.png` y similares no bastan para afirmar dichas páginas.

## Componentes transversales observables

- Idioma: `language.js` guarda `preferredLanguage` en `localStorage`; catálogo de idiomas y resultado visual completo `[NO EVIDENCIADO]`.
- Navegación lateral: `sidebar.js` guarda `activeMenu` en `localStorage`; vistas asociadas `[NO EVIDENCIADO]`.
- Búsqueda: `SearchHelper.min.js` obtiene su endpoint de un atributo/elemento de la vista ausente; por ello su contrato concreto no es recuperable.
- Service worker/caché y carrusel son componentes de presentación, no rutas.
