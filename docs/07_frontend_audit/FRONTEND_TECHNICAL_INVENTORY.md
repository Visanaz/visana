# Frontend Technical Inventory

## Conteo estático

La entrega contiene **242 archivos**. La distribución observada es: 145 WebP, 32 PNG, 18 JavaScript, 13 SVG, 10 PDF, 9 AVIF, 4 WOFF2, 3 JPG, 2 CSS y archivos individuales `.htaccess`, `.ico`, `.jpeg`, `.json`, `.php`, `.txt` y `.webm`. Los directorios `uploads/` (133 archivos) e `img/` (69) concentran activos; `docs/` contiene 10 PDFs de políticas/contratos.

| Componente | Evidencia | Estado |
|---|---|---|
| PHP / CodeIgniter | `index.php` declara mínimo PHP 8.1 y carga `CodeIgniter\Boot` | `[ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO]` |
| Apache / cPanel | `.htaccess`, rewrite y `ea-php82` | `[ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO]` |
| CSS compilado Tailwind | variables `--tw-*` en `css/main.css` | `[IMPLEMENTADA]` como artefacto CSS; build `[NO EVIDENCIADO]` |
| jQuery / Bootstrap | `$`, `$(document).ready`, modal/collapse/tooltip en módulos propios | `[IMPLEMENTADA]` como dependencia de tiempo de ejecución inferida del código |
| Axios | `js/axios.js`, `Service.min.js` / `Service.min2.js` | `[IMPLEMENTADA]` como wrapper cliente |
| Fetch / FormData | módulos de búsqueda, organización, bonificaciones y upload | `[IMPLEMENTADA]` en código estático |
| Moment | cálculos/formato de fechas en bonificaciones | `[IMPLEMENTADA]` como referencia global; origen de carga `[NO EVIDENCIADO]` |
| Angular, React o Vue | No hay manifiesto, fuente ni configuración | `[NO EVIDENCIADO]` |

## Build y empaquetado

`package-lock.json` declara `name: "css"`, lockfile v3 y `packages: {}`. Sin `package.json`, `node_modules`, configuración Tailwind, Vite, Webpack, Angular CLI ni source maps, no se puede recuperar un comando de build ni las dependencias exactas. El CSS con huellas Tailwind indica un resultado compilado, no el proyecto fuente que lo produjo.

## JavaScript relevante

| Archivo | Rol estático |
|---|---|
| `js/axios.js` | Instancia Axios con `baseURL: root`, encabezados JSON y helper multipart. |
| `js/Service.min.js`, `Service.min2.js` | Fachada `Service.exec`; manejo de error cliente y carga dinámica de CSS de Plyr. |
| `js/HeroSearch.js` | Búsqueda por ciudad/capacidad, CSRF desde input oculto y persistencia de filtro no sensible. |
| `js/backoffice/organizacion/organizacion.js` | Consulta y render de afiliados/distribuidores de nivel visual 1. |
| `js/backoffice/bonificaciones/bonificaciones.js` | Consulta de órdenes/impuestos y cálculos/visualización de incentivos y comisiones. |
| `js/helper.js` | Utilidades, formato COP, manipulación DOM y refresco de inputs CSRF. |
| `js/upload.js` | Drag-and-drop y POST a `upload.php`; objeto `Library` no aparece definido en la entrega. |
| `js/owl.carousel.min.js`, `datatable.js`, `Toast.min.js`, `Validate.min.js`, `Modal.min.js` | Librerías/ayudas incluidas; versiones exactas `[NO EVIDENCIADO]`. |

## Activos e integraciones estáticas

- Branding, banners, salud/producto y ecommerce: nombres como `logo.png`, `banner*.webp`, `COLAGENO.avif`, `eCommerce.avif` y `avalpay.png`. El nombre de activo no prueba integración comercial ni gateway.
- Cargas CSS/worker apuntan estáticamente a `visana.com.co`, `board.nicedev90.pro`, `cdn.nicedev90.pro` y `cdn.plyr.io`; no se hizo ninguna conexión.
- El worker `js/sw.js` define caché `cache-v2` para dos fuentes de `visana.com.co`. Registro o activación del worker `[NO EVIDENCIADO]`.
- `.htaccess` permite CORS sólo a extensiones de fuentes para tres orígenes configurados, incluido un IP privado. Alcance efectivo y necesidad funcional `[NO VERIFICADO]`.

## Obsolescencia y mantenibilidad

Las librerías minificadas carecen de manifiesto/versiones verificables. El uso simultáneo de jQuery, Bootstrap, JavaScript directo, Axios/fetch y CSS compilado constituye una superficie heterogénea de mantenimiento; no se afirma vulnerabilidad de versión sin un SBOM o ejecución de análisis de dependencias.
