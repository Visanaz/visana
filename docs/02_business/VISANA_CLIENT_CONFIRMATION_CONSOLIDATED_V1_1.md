# Confirmación consolidada VISANA v1.1

**Documento fuente para futura revisión/PDF. No es un PDF firmado.**

**Evidencia digital:** formulario oficial de Daniel Reina.

**Firma física:** pendiente.

Estados para lectura del cliente:

- **CONFIRMADO:** existe una decisión digital suficientemente concreta.
- **CONFIRMADO CON OBSERVACIONES:** el núcleo está decidido, pero faltan detalles antes de implementar completamente.
- **PENDIENTE DE DEFINIR:** falta una decisión necesaria.

| Regla | Tema | Gerencia manual | Daniel Form | Reconciliación | Decisión canónica | Observación pendiente | Autorización de implementación | Evidencia digital | Firma física |
|---|---|---|---|---|---|---|---|---|---|
| DG-19 | Afiliación | Compra mínima COP 200.000 | Después de aprobación administrativa | Formulario prevalece | Afiliación formal por aprobación administrativa | Separar cuenta, compra, afiliación, activación y calificación | No, hasta definir lifecycle | CONFIRMADO CON OBSERVACIONES | Pendiente |
| DG-17 | Perfiles múltiples | Excluyentes | Combinaciones simultáneas permitidas | Formulario prevalece | Una identidad puede tener múltiples perfiles | Matriz de permisos futura | Sí para diseño futuro, sin duplicar identidad | CONFIRMADO | Pendiente |
| DG-18 | Cambio de sponsor | Admin, justificación y condiciones | Admin, justificación, sólo futuro | Refinado | Admin-only, auditado, prospectivo, sin reescribir historia | Compresión por inactividad separada | No mutation hasta definición completa | CONFIRMADO CON OBSERVACIONES | Pendiente |
| DG-01 | Activación/recompra | 30 días | Día equivalente del mes siguiente | Formulario prevalece | Aniversario mensual calendario | Anticipada, instante y fin de mes | Parcial; no countdown definitivo | CONFIRMADO CON OBSERVACIONES | Pendiente |
| DG-11 | Período de calificación | Sin decisión independiente | Otro / requiere detalle | Respuesta incompleta | No definido | Ventana, límites y zona | No | PENDIENTE DE DEFINIR | Pendiente |
| DG-09 | Base Qualification/Volume | Comisiones antes de impuestos | Valor pagado incluyendo IVA | Formulario prevalece | Importe real pagado por producto incluyendo IVA | No extender a otras fórmulas | Sí para futura política específica | CONFIRMADO | Pendiente |
| DG-03 | Team Sales | Incompleto | Propias + toda downline; pagadas; cancelación/devolución restan | Refinado | Alcance completo de red y ajustes | Momento exacto y parcialidades | Sí para diseño, no producción financiera | CONFIRMADO | Pendiente |
| DG-02 | Invitación distribuidor | 10%; 25%+10% separado | 10%, distribuidor activo | Refinado | Bono de invitación 10% | Base y período | No cálculo hasta cerrar base/período | CONFIRMADO | Pendiente |
| BR-L8 | Economía L8 | Pool global 2% | Comisión individual L8 | Formulario prevalece | Comisión individual | Base/elegibilidad/reversos | No todavía | CONFIRMADO | Pendiente |
| DG-07 | 1% adicional | Incompleto | Sí, adicional; observación de pool | Parcial | 1% adicional | Base, elegibles, fórmula, período | No | CONFIRMADO CON OBSERVACIONES | Pendiente |
| DG-06A | Pools distribuidor | Tiers documentados | Ventas globales de distribuidores | Parcial | Alcance global | Base ambigua y tiers/reparto | No | CONFIRMADO CON OBSERVACIONES | Pendiente |
| DG-06B | Consistencia | Mantener/superar mes anterior | Igual | Coincide | Comparación mensual anterior | Caída, racha, acumulación, base | No algoritmo completo | CONFIRMADO CON OBSERVACIONES | Pendiente |
| DG-08 | Restricción rama | Toda comisión de la rama | Igual | Coincide | Detener toda comisión de rama | Inicio, reversibilidad y volumen | No algoritmo completo | CONFIRMADO CON OBSERVACIONES | Pendiente |
| DG-04 | Aprobación comisión | Automática | Automática | Coincide | Sistema aprueba automáticamente | Excepciones/reversión | No efecto financiero en este gate | CONFIRMADO | Pendiente |
| DG-05 | Orden de pago | Created→Approved→Processing→Paid/Failed | Igual | Coincide | Lifecycle confirmado | Retry, conciliación, comprobante | No payout en este gate | CONFIRMADO | Pendiente |
| DG-10 | Cancelación/reverso | Hold histórico 5 días | Cancelar antes; descontar después | Formulario prevalece | Reverso según estado de pago | Parciales, rango y cierre | No algoritmo completo | CONFIRMADO CON OBSERVACIONES | Pendiente |
| DG-12 | Impuestos | Aplicar los legales | Entregar matriz de Contabilidad | Refinado | Marco aplica; matriz pendiente | Tasas, bases, sujetos, vigencias | No | PENDIENTE DE DEFINIR | Pendiente |
| DG-13 | Envío | Desde 3 unidades | Desde 3 productos a un destino | Coincide/refina | Total >=3 unidades, un destino | Geografía, valor, peso | No hasta excepciones | CONFIRMADO CON OBSERVACIONES | Pendiente |
| DG-15 | Pagos | Banco de Occidente | Banco de Occidente/Occired | Refinado | Dirección de proveedor Occired | API, sandbox, webhook, refund | No integración autorizada | CONFIRMADO CON OBSERVACIONES | Pendiente |
| DG-14 | Identidad | Mantener Keycloak | Equipo debe recomendar | Formulario prevalece | Recomendar mantener Keycloak en etapa actual | Aceptación formal | No cambio | PENDIENTE DE DEFINIR | Pendiente |
| DG-16 | Fuente/histórico | Repositorio por confirmar | Repositorio será entregado; migrar historia | Nueva información | Auditar fuente y planear migración separada | Inventario/calidad/alcance | No migración en este gate | CONFIRMADO CON OBSERVACIONES | Pendiente |
| QTH-F5-01 | Umbrales | Tabla L1–L8 | Aprobar tal como está | Coincide | Tabla canónica del baseline candidato | Fecha de vigencia | Sí después de gate/versionado | CONFIRMADO | Pendiente |
| BR-CANON | Compras, Unilevel, cortes | Valores documentados | Aprobación explícita | Coincide | 200k/100k, 15/10/5/4/3/2/1/2%, cortes 25/10 | Bases y condiciones por beneficio | No motor financiero por porcentajes solos | CONFIRMADO | Pendiente |

## Preguntas para cierre

1. Definir el período exacto de calificación.
2. Definir recompra anticipada, expiración horaria y fin de mes.
3. Entregar matriz tributaria oficial.
4. Adoptar formalmente o rechazar la recomendación técnica de mantener Keycloak.
5. Definir compresión de genealogía por inactividad.
6. Aclarar la base “bruto descontando IVA” y completar tiers/reparto DG-06A.
