# Frontend Reconstruction Strategy — propuesta condicionada

## Posición

La evidencia no permite reparar ni reconstruir fielmente el frontend legado: falta su aplicación CodeIgniter, sus vistas, rutas y manifiesto de build. La vía factible es un **frontend nuevo** contra contratos aprobados de `/api/v1`, preservando sólo información visual o interacción cuya necesidad sea confirmada por VISANA. Esta es una recomendación de planificación, no implementación ni aprobación de arquitectura.

## Disposición de capacidades

| Capacidad legado | Disposición propuesta | Motivo basado en evidencia |
|---|---|---|
| Logos, banners, iconos y documentos legales | `PRESERVE_PENDING_RIGHTS_AND_CONTENT_REVIEW` | Activos disponibles; uso actual/licencia/contenido vigente no verificados. |
| Catálogo/productos visuales | `REDESIGN` | Plan 3 tiene catálogo autenticado; no se recuperó la UI ni reglas de comercio legado. |
| Filtros de búsqueda `/viajero/search` | `HOLD_OR_ELIMINATE` | Dominio no mapeado a Plan 3; conservar sólo si negocio confirma alcance. |
| Organización/red | `REDESIGN` | Contrato legado expone PII/conteos; Plan 3 es red mínima sin PII. |
| Bonificaciones/órdenes pendientes | `DO_NOT_PORT_AS_RULES` | Cálculos cliente y contratos financieros no canónicos; DG-12/DG-15 abiertos. |
| Upload `upload.php` | `ELIMINATE_UNTIL_APPROVED` | No hay fuente servidor ni política de seguridad. |
| Session/bearer legado | `REPLACE_WITH_OIDC_ABSTRACTION` | Contrato legado no recuperable; Plan 3 evidencia OIDC/Keycloak. |

## Arquitectura objetivo condicionada

Cuando DG-14, DG-16 y DG-17 y las reglas financieras aplicables estén aprobados, se recomienda un cliente Angular nuevo, con módulos/rutas explícitos, un adaptador de autenticación OIDC/Keycloak desacoplado y clientes tipados para `/api/v1`. Precio, impuestos, comisiones, estados de pago y autorización deben ser autoritativos del servidor; la interfaz sólo presenta contratos y validaciones de experiencia.

Esto mantiene consistencia con `TARGET_ARCHITECTURE.md` sin convertir dicha arquitectura en hecho actual ni alterar la decisión pendiente de proveedor de identidad.

## Secuencia de trabajo propuesta

1. **Descubrimiento validado:** confirmar fuente de verdad de contenido, journeys, roles, PII y alcance de módulos; cerrar o acotar DG-16.
2. **Contratos:** publicar/validar OpenAPI de catálogo, órdenes, identidad y red; registrar explícitamente las APIs faltantes de liquidación/payout, si el negocio las aprueba.
3. **Fundación UI:** shell, autenticación OIDC, manejo de errores/correlation ID, políticas de sesión, pruebas y pipeline reproducible.
4. **Módulos de menor riesgo:** catálogo y órdenes actor-owned sobre `/api/v1`; no incluir pago real sin DG-15.
5. **Red y finanzas:** implementar sólo con matriz de roles/PII, modelo de perfiles y reglas firmadas; auditoría, ownership y pruebas de autorización son precondiciones.
6. **Migración controlada:** inventario de URLs/redirecciones, accesibilidad, contenido aprobado, observabilidad, despliegue gradual y rollback documentado.

## Criterios de salida

- Ningún endpoint legado se asume compatible por similitud de nombre.
- No hay fórmula financiera cliente como fuente de verdad.
- Cada pantalla tiene propietario de negocio, rol, contrato, errores y evidencia de autorización.
- Las dependencias se declaran en manifiesto/SBOM y el build es reproducible.
- Cualquier migración de identidad o PII tiene mapeo aprobado y no se deduce de correo o nombre.
