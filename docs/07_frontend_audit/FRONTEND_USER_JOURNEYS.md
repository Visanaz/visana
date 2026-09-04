# Frontend User Journeys — reconstrucción limitada

## J-01 — búsqueda por ciudad y capacidad

`HeroSearch` lee ciudad y pasajeros desde la vista ausente. Al pulsar `.hero_search`, arma `FormData`, agrega un CSRF si encuentra un input oculto, llama `POST /viajero/search` y, si recibe `success`, entrega `propiedades` a un objeto `explorer`. Guarda ciudad, identificador y pasajeros en `localStorage` bajo `filtroPrimario`.

**Resultado:** la interacción cliente está evidenciada; página, identidad del usuario, fuente de propiedades, contrato servidor y navegación posterior son `[NO EVIDENCIADO]`. No se atribuye este flujo a MLM ni a comercio VISANA por el nombre `viajero`.

## J-02 — consulta de red

Al cargar el módulo de organización, se llama `GET /api/consultar_afiliados_nivel_inferior`. La respuesta se inserta en tarjetas de afiliados y distribuidores, mostrando nombre, apellido, correo y número de afiliados. La etiqueta visual `NIVEL: 1` está escrita de forma fija.

**Resultado:** hay evidencia de lectura de PII y conteo de directos en la interfaz. No hay evidencia de controles de rol, de navegación a niveles posteriores, de genealogía completa ni de que “nivel 1” refleje datos reales.

## J-03 — consulta de bonificación/orden pendiente

Al cargar el módulo de bonificaciones se consulta primero la fuente de retenciones y luego órdenes de pago de distribuidores para el mes local actual. Al abrir un botón de detalle se solicitan compras/detalles y se presentan periodos, totales, IVA, retenciones, compras invitadas, compras de distribuidores e incentivos.

**Resultado:** existe presentación y cálculo cliente de datos financieros. No hay POST de “generar pago” en el JS revisado, ni evidencia de aprobación, persistencia de liquidación, idempotencia, pago externo, reverso o conciliación. El rótulo del botón/modal no demuestra una operación financiera.

## J-04 — carga de archivo

`upload.js` acepta drag-and-drop, crea `FormData(file)` y usa `Library.post('upload.php', ...)`. `Library`, la vista, validaciones de tipo/tamaño y el receptor PHP no están en la entrega.

**Resultado:** `FLOW_INCOMPLETE`; no debe portarse como función aprobada sin contrato y requisitos de seguridad.

## Sesión y almacenamiento local

Sólo se observan preferencias/filtros de UI: `filtroPrimario`, `preferredLanguage` y `activeMenu`. No se encontró almacenamiento local de JWT, credenciales o secretos. Ello no prueba que no existan en vistas o código omitido.
