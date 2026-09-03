# Frontend Baseline — evidencia estática

## Dictamen

**Clasificación: B — fuente parcial disponible.** `C:\visana_auditoria\visana_frontend` contiene el front controller de CodeIgniter, reglas Apache, CSS y JavaScript legible/desplegado, pero no contiene `app/Config/Paths.php`, controladores, vistas, modelos, rutas de servidor ni `package.json`. El propio `index.php` exige `../visana.com.co/app/Config/Paths.php`, ruta que no está presente en la entrega. Por tanto no es el fuente original completo ni permite reconstruir o ejecutar la aplicación legado.

| Hecho | Evidencia estática | Límite |
|---|---|---|
| No es repositorio Git | No existe `.git` en la raíz inspeccionada | No identifica la versión de despliegue. |
| Entrada PHP CodeIgniter | `index.php`, mínimo PHP `8.1`, bootstrap `CodeIgniter\Boot::bootWeb` | No se ejecutó. |
| Despliegue Apache/cPanel | `.htaccess`, rewrite al front controller y handler `ea-php82` | No demuestra servidor activo ni configuración efectiva. |
| Fuente de aplicación incompleta | Falta el árbol requerido `../visana.com.co/app` y no hay vistas PHP/HTML | Las URLs/páginas completas no son recuperables. |
| Artefactos de interfaz disponibles | `css/`, `js/`, `img/`, `uploads/`, `docs/`, `fonts/`, `vid/` | No prueba que cada activo se cargara en producción. |

## Alcance y método

La inspección fue exclusivamente de lectura estática. No se ejecutaron PHP, JavaScript, npm, Composer, Docker, migraciones, peticiones HTTP, autenticación ni conexiones a dominios externos. Una llamada encontrada en JavaScript es una **referencia histórica**, no prueba de endpoint vivo, autorización efectiva ni respuesta real.

Las rutas de vistas, login, registro, catálogo, checkout y administración sólo pueden afirmarse cuando existe evidencia en los archivos entregados. Donde no existe, se registra `[NO EVIDENCIADO]` y no se completa por convención de CodeIgniter o por el nombre de un activo.

## Cobertura recuperable

| Área | Estado | Evidencia |
|---|---|---|
| Búsqueda pública candidata | `[PARCIALMENTE_IMPLEMENTADA]` como módulo JS | `js/HeroSearch.js` y sus selectores DOM; falta la vista que los contiene. |
| Organización/red | `[PARCIALMENTE_IMPLEMENTADA]` como módulo de backoffice | `js/backoffice/organizacion/organizacion.js`; falta ruta, plantilla y controlador. |
| Bonificaciones/órdenes de pago | `[PARCIALMENTE_IMPLEMENTADA]` como módulo de backoffice | `js/backoffice/bonificaciones/bonificaciones.js`; falta fuente servidor y evidencia de pago. |
| Login, registro y recuperación | `[NO EVIDENCIADO]` | No hay vistas, formularios ni controlador correspondiente en la entrega. |
| Catálogo, carrito, checkout y pago | `[NO EVIDENCIADO]` como flujo end-to-end | Hay activos de marketing/comercio y una referencia de pago visual, no fuente de flujo. |

## Restricciones de reconstrucción

1. No se puede derivar el routing legado desde `.htaccess`: sólo demuestra que Apache delegaba rutas no físicas a `index.php`.
2. El `package-lock.json` tiene `packages` vacío y no existe `package.json`; no acredita una cadena de build recuperable.
3. Ninguna regla financiera calculada en el navegador prueba persistencia, liquidación, pago o autorización en backend.
4. El contenido de `docs/*.pdf` se inventaría como activo legal; no se reproduce ni se usa como prueba de comportamiento web sin una revisión documental separada.

## Referencias

- Fuente formal: `FRONTEND_SOURCE_REGISTER.md`.
- Inventario: `FRONTEND_TECHNICAL_INVENTORY.md`.
- Hallazgos y decisiones pendientes: `FRONTEND_GAP_REGISTER.md` y `docs/00_governance/DECISION_REGISTER.md` (DG-16).
