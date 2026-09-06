# Frontend Source Register

| ID | Fuente | Tipo | Uso en esta auditoría | Confianza y límites |
|---|---|---|---|---|
| FSRC-001 | `C:\visana_auditoria\visana_frontend\index.php` | Front controller PHP | Identificación CodeIgniter/PHP y dependencia ausente | Alta para el contenido; no prueba ejecución. |
| FSRC-002 | `C:\visana_auditoria\visana_frontend\.htaccess` | Configuración Apache | Rewrite, header Authorization, CORS de fuentes y handler cPanel | Alta para texto; efecto de servidor no verificado. |
| FSRC-003 | `css/main.css`, `css/organigrama.css` | Artefacto CSS | Huellas Tailwind, recursos visuales y presentación | Alta para artefacto; fuente/build ausente. |
| FSRC-004 | `js/axios.js`, `Service.min*.js`, `helper.js` | JavaScript compartido | Transporte, FormData, CSRF auxiliar, caché y presentación | Alta para llamadas escritas; no se ejecutó. |
| FSRC-005 | `js/HeroSearch.js` | Módulo JS | Flujo de búsqueda y almacenamiento de filtro | Parcial: vista/endpoint ausentes. |
| FSRC-006 | `js/backoffice/organizacion/organizacion.js` | Módulo JS | Red, PII mostrada y contrato legado referido | Parcial: ruta, autorización y backend ausentes. |
| FSRC-007 | `js/backoffice/bonificaciones/bonificaciones.js` | Módulo JS | Consultas financieras y fórmulas de presentación | Parcial: no prueba cálculo/pago servidor. |
| FSRC-008 | `js/upload.js`, `SearchHelper.min.js` | Módulos JS incompletos | Upload y búsqueda dinámica | Baja/media: dependencias/vistas/receptor ausentes. |
| FSRC-009 | `img/`, `uploads/`, `fonts/`, `vid/`, `docs/` | Activos desplegados | Clasificación visual/legal y alcance de contenido | Los nombres no prueban función ni vigencia. |
| FSRC-010 | `package-lock.json`, `robots.txt` | Metadatos de entrega | Límite del build y directiva de rastreo | No hay `package.json` ni prueba de despliegue. |

## Exclusiones y tratamiento de secretos

- No se ejecutó ni cargó contenido desde URLs, assets, scripts o PDFs.
- La búsqueda por patrones de secreto informó sólo conteo de archivos coincidentes: cero; no se exponen valores.
- No se reclama integridad criptográfica, fecha de despliegue ni equivalencia con un entorno productivo porque el directorio no contiene Git.
