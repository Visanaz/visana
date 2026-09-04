# Frontend Security Review — estático y no intrusivo

Este documento no es una prueba de penetración. Severidad significa prioridad para validar/diseñar y no confirma explotación.

| ID | Prioridad | Hallazgo | Evidencia | Límite / acción de decisión |
|---|---|---|---|---|
| FS-001 | P1 | Inserción de datos de red en `innerHTML` | `organizacion.js` interpola nombre, apellido, correo y conteo | Riesgo DOM-XSS si servidor aporta texto no confiable; codificación/validación real `[NO VERIFICADA]`. |
| FS-002 | P1 | Inserción extensa de PII/finanzas en `innerHTML` | `bonificaciones.js` interpola nombres, totales y detalle | Riesgo DOM-XSS/exposición visual; permisos de servidor `[NO VERIFICADOS]`. |
| FS-003 | P1 | Reglas monetarias en navegador | IVA 19%, 10%, 35% y bandas de incentivo en bonificaciones | La UI puede ser manipulada; no se afirma que backend acepte esos valores. Cálculo autoritativo debe decidirse/validarse en servidor. |
| FS-004 | P2 | CSRF parcial y no trazable | Sólo HeroSearch adjunta input CSRF si existe | Vistas y validación backend ausentes; cobertura real `[NO EVIDENCIADA]`. |
| FS-005 | P2 | Upload sin contrato recuperable | `upload.js` usa `upload.php`, sin fuente servidor | Tipo, tamaño, almacenamiento, autorización y escaneo `[NO EVIDENCIADOS]`. |
| FS-006 | P2 | Lista CORS de fuentes con orígenes heredados | `.htaccess` enumera IP privada y dominios | Aplica sólo a fuentes si Apache lo activa; necesidad, ownership y entorno `[NO VERIFICADOS]`. |
| FS-007 | P2 | Dependencias sin manifiesto/SBOM | JS minificado y sin `package.json` | No es posible identificar versiones/CVEs de manera defendible. |
| FS-008 | P3 | Redirección `www` a HTTP | `.htaccess` usa `http://%1%{REQUEST_URI}` cuando HTTPS no está activo | Efecto depende de proxy/servidor; requiere validación operativa antes de calificar impacto. |

## Secretos

La búsqueda estática por patrones de API key, client secret, private key y access token en PHP/JS/CSS/JSON/.htaccess arrojó **0 archivos coincidentes**. No se reproducen valores. Este resultado no cubre contenido no entregado, secretos del servidor, runtime, PDFs ni proveedores externos.

## Controles observados y no demostrados

- `Options -Indexes` y `ServerSignature Off` aparecen en `.htaccess`; efecto real `[NO VERIFICADO]`.
- `.htaccess` preserva `Authorization`; no hay prueba de que los módulos envíen bearer token.
- No se observó token almacenado en `localStorage`/`sessionStorage` dentro de los archivos revisados.
- El estado de autenticación, roles, ownership, CSP, cabeceras, cookies (`Secure`/`HttpOnly`/`SameSite`), TLS, rate limiting, logs y autorización de API no es recuperable desde este artefacto.
