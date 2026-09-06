# Frontend Gap Register

| ID | Prioridad | Brecha | Evidencia | Estado / dependencia |
|---|---|---|---|---|
| FG-001 | P0 | Fuente de vistas, rutas, controladores y modelos legado ausente | `index.php` requiere árbol no entregado | `BLOCKED`; obtener fuente/versionado o aceptar reconstrucción nueva. |
| FG-002 | P0 | Contrato de login/sesión/roles no recuperable | No hay vista auth; JS no adjunta bearer | `BLOCKED_BY_DG-14/DG-17`; OIDC/roles/UX requieren fuente aprobada. |
| FG-003 | P0 | Liquidación, pago, reverso y conciliación no demostrados | Sólo consultas/cálculos visuales en bonificaciones | `BLOCKED_BY_DG-12/DG-15` y reglas de compensación. |
| FG-004 | P0 | Reglas 10%, 35%, IVA y bandas de incentivo no son canónicas | Literales en navegador | `BLOCKED_BY_BUSINESS_RULE_APPROVAL`; no migrar literalmente. |
| FG-005 | P1 | PII y datos financieros renderizados sin codificación visible | `innerHTML` en módulos de red/bonificaciones | Diseñar contratos mínimos, salida segura y autorización/ownership. |
| FG-006 | P1 | Red legado no corresponde al modelo mínimo Plan 3 | Front pide PII/conteo; Plan 3 entrega IDs/profundidad | Definir UX, roles y datos permitidos. |
| FG-007 | P1 | Upload no tiene fuente servidor ni política | `upload.php` y `Library` ausentes | Caso de uso, seguridad, almacenamiento y retención pendientes. |
| FG-008 | P2 | Dependencias/build no reproducibles | `package-lock` vacío; sin manifiesto/configuración | Inventariar SBOM/artefactos originales o reemplazar como proyecto nuevo. |
| FG-009 | P2 | CORS/orígenes y recursos externos heredados sin ownership probado | `.htaccess`, CSS y worker | Validar propietarios, CSP y política de terceros. |
| FG-010 | P2 | Ruta HTTP condicional en rewrite | `.htaccess` | Validar tras proxy/TLS antes de intervenir. |

## Decisión de alcance

DG-16 permanece `SIGUE_ABIERTO`. La entrega elimina el supuesto anterior de “ninguna evidencia frontend”, pero aporta sólo fuente parcial y artefactos desplegados; no decide automáticamente que Angular sea obligatorio, ni autoriza portar reglas legado.
