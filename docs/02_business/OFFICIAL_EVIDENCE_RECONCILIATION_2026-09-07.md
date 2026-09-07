# Reconciliación de evidencia oficial — 2026-09-07

## Control y precedencia

- Fuente funcional vigente: respuesta oficial de Google Forms de Daniel Reina, Gerente Financiero, Gerencia y Contabilidad.
- Fecha declarada de respuesta: 2026-09-05. Marca de tiempo del formulario: 2026-09-07 09:34:03.
- Alcance de la declaración de aprobación: todo el formulario. La fuente autoriza su uso como entrada de Business Rules Baseline v1.0.
- El documento manual de Gerencia se conserva como evidencia histórica. Cuando contradice el formulario posterior, prevalece el formulario.
- La respuesta de prueba atribuida a Cristian queda excluida.
- Estado de la evidencia: `DIGITAL_FUNCTIONAL_DECISION`. La ratificación física permanece `PENDING`; no se afirma que exista PDF firmado.
- La fecha de entrada en vigor no fue informada: `EFFECTIVE_FROM_PENDING`.

## Reconciliación por regla

| Regla | Respuesta manual | Respuesta Daniel | Clasificación | Interpretación canónica | Ambigüedad restante | Impacto de implementación |
|---|---|---|---|---|---|---|
| DG-19 | Compra inicial mínima COP 200.000 como condición de afiliado | Afiliación después de aprobación administrativa | `CONFLICT_FORM_WINS` | La afiliación formal requiere aprobación administrativa | Separación exacta entre cuenta, compra, afiliación, activación y calificación | Provisioning administrativo y trazabilidad futuros; no implementado aquí |
| DG-17 | Perfiles excluyentes e identidad/documento único | Una identidad puede reunir Cliente, Afiliado y Distribuidor | `CONFLICT_FORM_WINS` | Una identidad puede poseer varios perfiles sin duplicar identidad/documento | Matriz de permisos y transiciones | El modelo de perfiles múltiples debe conservar unicidad de identidad |
| DG-18 | Administrador, justificación auditada y condiciones específicas | Administrador con justificación auditada; efecto sólo futuro | `REFINED_BY_FORM / PARTIAL` | Cambio sólo administrativo, auditado, prospectivo y sin reescribir genealogía histórica | Regla separada `GENEALOGY_INACTIVITY_COMPRESSION_RULE` | Mantener sponsor mutation bloqueada hasta definir compresión/inactividad |
| DG-01 | 30 días desde compra | Aniversario mensual de compra/recompra | `CONFLICT_FORM_WINS` | `CALENDAR_ANNIVERSARY_MONTH`; la inactividad excluye comisiones del período inactivo | Recompra anticipada, instante de expiración y fin de mes | `plusMonths(1)` se alinea con el núcleo; política de solapamiento sigue abierta |
| DG-11 | No resuelto de forma independiente | Los cortes quincenales aplican a pago; calificación dice “Otro / requiere detalle” | `PARTIAL / NO_RESPONSE_FOR_QUALIFICATION` | Período de calificación no definido | Ventana, límites, zona y ajustes | Bloquea el núcleo F5 de calificación |
| DG-09 | “Comisiones antes de impuestos” | Valor realmente pagado por producto, incluyendo IVA | `CONFLICT_FORM_WINS` | Para Qualification/Volume: `ACTUAL_PRODUCT_AMOUNT_PAID_INCLUDING_IVA` | No se extiende a comisiones, pools, beneficios ni impuestos | Requiere política concreta futura; la abstracción actual no fija la base |
| DG-03 | Team Sales incompleto | Ventas propias + directos + toda la descendencia; sólo pagadas/confirmadas; cancelación y devolución restan | `REFINED_BY_FORM` | Team Sales incluye vendedor y toda su downline con ajustes por cancelación/devolución | Momento contable exacto y devolución parcial | El agregador actual se alinea con el alcance; faltan adaptadores/políticas operativas |
| DG-02 | 10% y referencia separada a 25% + 10% | Bono de invitación fijo 10%; distribuidor activo | `REFINED_BY_FORM` | `INVITATION_BONUS = 10%`, beneficiario activo | Base monetaria y período | No mezclar con margen/beneficio comercial del distribuidor |
| BR-L8 | Pool global 2% distribuido entre L8 | Comisión individual por ventas de descendientes en nivel 8 | `CONFLICT_FORM_WINS` | Nivel 8 es comisión individual, no pool global | Base, elegibilidad y reversos | La semántica provisional previa queda superada; no implementar aún |
| DG-07 | 1% con semántica incompleta | Es adicional al Unilevel; observación habla de pool entre elegibles | `PARTIAL / APPROVED_CORE_WITH_OBSERVATIONS` | El 1% es adicional | Base, elegibles, fórmula, período y ventas fuente | Motor financiero bloqueado hasta cerrar mecánica |
| DG-06A | Pools con escalas 15M/25M/45M/100M | Ventas globales de todos los distribuidores; >15M recibe 1% | `PARTIAL` | Alcance global confirmado como núcleo | “Bruto descontando IVA”, demás tiers, acumulación y reparto | `DG06A_MONETARY_BASE_CLARIFICATION_REQUIRED` |
| DG-06B | Mantener/superar el mes anterior | Igual | `MATCH` | Comparación mensual contra el mes anterior | Caída, reinicio, 6/12 meses y base monetaria | Núcleo aprobado; algoritmo completo pendiente |
| DG-08 | Se detiene toda comisión de la rama | Toda comisión de esa rama | `MATCH` | Restricción sobre toda la comisión de la rama | Inicio, reversibilidad y tratamiento de volumen | Núcleo aprobado; algoritmo parcial |
| DG-04 | Aprobación automática | Sistema automáticamente | `MATCH` | Aprobación de comisión automática | Bloqueo, excepción manual, rechazo y reversión | Flujo operacional futuro |
| DG-05 | Created → Approved → Processing → Paid/Failed | Igual | `MATCH` | Ciclo de orden de pago confirmado | Cancelación, reintento, conciliación y comprobante | No autoriza payout ni integración financiera |
| DG-10 | Ventana histórica de confirmación de 5 días | Antes de pagar: cancelar; después de pagar: descontar del siguiente período | `CONFLICT_FORM_WINS / PARTIAL` | Reverso antes del pago; compensación futura después del pago | Devolución parcial, rango, períodos cerrados | `HISTORICAL_UNCONFIRMED_5_DAY_HOLD` se conserva sin carácter canónico |
| DG-12 | Descontar impuestos legalmente aplicables | VISANA entregará matriz oficial de Contabilidad | `REFINED_BY_FORM` | El marco tributario aplica, sin tasas codificables todavía | Matriz completa, vigencias, sujetos y bases | `BLOCKED_BY_ACCOUNTING_MATRIX` |
| DG-13 | Desde 3 unidades totales | Desde 3 productos, un solo destino | `MATCH / REFINED_BY_FORM` | Cantidad total >= 3 unidades habilita beneficio de envío VISANA a un destino | Geografía, valor, peso y excepciones | Regla candidata; implementación pendiente |
| DG-15 | Gateway Banco de Occidente | Banco de Occidente / portal Occired | `REFINED_BY_FORM` | Dirección de proveedor: `BANCO_DE_OCCIDENTE / OCCIRED` | API/checkout, sandbox, credenciales, webhook, refund y conciliación | No autoriza integración técnica |
| DG-14 | Mantener Keycloak | VISANA delega recomendación al equipo | `CONFLICT_FORM_WINS / TECHNICAL_DECISION_REQUIRED` | `CURRENT_TECHNICAL_RECOMMENDATION = KEEP_KEYCLOAK_FOR_CURRENT_STAGE` | Adopción formal de la recomendación | Keycloak sigue siendo arquitectura técnica vigente, no aprobación de cliente |
| DG-16 | Fuente frontend/historia por confirmar | Existe repositorio y será entregado; migrar histórico para no perderlo | `MATCH / NEW_INFORMATION` | Auditar fuente futura y separar workstream de migración histórica | Inventario, calidad, alcance y estrategia | No cambia Angular ni migra datos aquí |
| QTH-F5-01 | Umbrales L1–L8 coincidentes | Aprobados tal como están | `MATCH` | Umbrales L1–L8 confirmados digitalmente | `effectiveFrom` | Valores actuales alineados; la versión persistida sigue provisional |
| BR-CANON | Montos, Unilevel y cortes documentados | Aprobación explícita | `APPROVED_DIGITAL` | Inicial >= COP 200.000; recompra >= COP 100.000; Unilevel 15/10/5/4/3/2/1/2%; cortes 1–15→25 y 16–fin→10 | Bases, elegibilidad, reversos y vigencia por regla | Porcentajes no autorizan por sí solos un motor financiero |

## Normalizaciones y retroactividad

- La frase “mayor de 200.000” se normaliza a `>= COP 200.000` porque el campo BR-CANON dedicado aprueba un mínimo de COP 200.000. Estado: `INTRA_FORM_WORDING_NORMALIZATION`.
- Toda decisión que reemplaza semántica provisional se marca `SUPERSEDES_PROVISIONAL_VERSION`.
- Conforme a ADR-027, una versión nueva no recalcula ni reinterpreta resultados históricos. No se asigna vigencia sin una fecha aprobada.

## Conteo de estados

- `APPROVED_DIGITAL`: 9 reglas.
- `APPROVED_WITH_OBSERVATIONS/PARTIAL`: 11 reglas.
- `OPEN/BLOCKED`: 3 reglas.
- Decisiones con contenido digital confirmado y ratificación física pendiente: 20.

## Impacto sobre la implementación provisional Sprint 5

| Área | Clasificación | Evidencia actual | Cambio futuro requerido |
|---|---|---|---|
| DG-01 activación | `PARTIALLY_ALIGNED` | `CalendarMonthActivationDuration` usa `plusMonths(1)` y `ActivationCalculator` modela alternativas de solapamiento sin escoger la pendiente | Elegir política de recompra anticipada, expiración exacta y fin de mes en una nueva versión |
| DG-03 Team Sales | `ALIGNED` en alcance; integración parcial | `CalculateTeamVolumeService` consulta miembro + descendientes y `TeamSalesAggregator` aplica políticas de elegibilidad, base y ajustes | Conectar ventas reales y política pagada/confirmada cuando DG-11 permita una ventana canónica |
| DG-09 base monetaria | `NOT_IMPLEMENTED` como política canónica | Existe `QualificationBasePolicy`, pero no una política aprobada que seleccione importe pagado incluyendo IVA | Implementar/adaptar la política sólo tras autorización técnica y versionado |
| QTH-F5-01 umbrales | `ALIGNED` en valores; versión aún provisional | V8 contiene exactamente L1–L8 bajo la versión Catherine `PROVISIONAL` | Crear nueva versión `APPROVED` sólo con `effectiveFrom` y metadata completas; no alterar historia |

No se realizaron cambios Java, Flyway, base de datos, OpenAPI ni frontend.
