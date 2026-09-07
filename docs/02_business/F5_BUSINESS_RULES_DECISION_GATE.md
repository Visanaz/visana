# F5 Business Rules Decision Gate

**Fecha de corte:** 2026-09-07
**Estado:** `CLIENT_DECISION_REQUIRED / NO_RULE_APPROVED`
**Alcance:** gate documental previo a Frontend F5. Este documento no autoriza implementación, uso productivo ni efectos financieros.

## Resultado del gate

`F5 = READ_ONLY_SKELETON_ONLY`.

`F5_IMPLEMENTATION_AUTHORIZATION = NOT_AUTHORIZED`.

La decisión de ejecución del PM es `WAIT_FOR_CLIENT_DECISIONS`: construir incluso el esqueleto visual antes de cerrar las reglas mínimas produciría retrabajo sin valor funcional suficiente. Es una decisión de secuencia del proyecto, no una regla de negocio.

La foundation backend de Sprint 5 modela reglas versionadas y resultados históricos, pero no expone endpoints públicos de activación, volumen, Team Sales, calificación o rango. Además, DG-01, DG-03, DG-09, DG-11, DG-18 y DG-19 carecen de aprobación oficial. Por ello no existe evidencia suficiente para mostrar métricas o estados de negocio con significado canónico.

## Fuentes revisadas y precedencia

Se contrastaron:

- `BUSINESS_RULES_WORKING_BASELINE_2026-09-05_PROVISIONAL.md`;
- `BUSINESS_RULES_BASELINE_V1_DRAFT.md`;
- `DECISION_REGISTER.md`;
- `BUSINESS_RULE_EXAMPLES_V1.md`;
- `VISANA_DECISION_FORM_V1.md`;
- `SPRINT_5_READINESS.md` y `SPRINT_5_QUALIFICATION_VOLUME_FOUNDATION_REPORT.md`;
- `QUALIFICATION_VOLUME_ARCHITECTURE.md`;
- `ADR-027-VERSIONED-BUSINESS-RULES-AND-HISTORICAL-CALCULATION-INTEGRITY.md`;
- `SOURCE_REGISTER.md`, `BUSINESS_RULE_CATALOG.md`, `OPEN_BUSINESS_DECISIONS.md` y `DECISION_DEPENDENCY_MAP.md`;
- contrato `openapi/visana-api-v1.json`, SHA-256 `F75B41FAECC92EDFC3974366047D81979147CFF00FB1124471D5BB9EEE9DB720`.

La precedencia aplicada es: (1) documento oficial firmado por VISANA, (2) formulario oficial diligenciado por VISANA, (3) aclaración escrita explícita de VISANA, (4) aclaración verbal documentada como provisional, (5) código/datos históricos y (6) inferencia técnica. Ninguna fuente inferior puede reemplazar una superior. Los niveles 4 a 6 nunca se promueven a `APPROVED`.

No se encontró una respuesta escrita/firmada, un formulario diligenciado ni una aclaración escrita que cierre estas seis decisiones. SRC-003 documenta el plan histórico, pero no resuelve de forma canónica los puntos pendientes de este gate.

## Clasificación de las reglas

| Regla | Evidencia vigente | Estado evidenciario | Clasificación del gate | Razón |
|---|---|---|---|---|
| DG-01 | Aclaración verbal: `PAYMENT_CONFIRMED`, inicio en `paymentConfirmedAt`, un mes calendario | `PROVISIONAL_VERBAL_CLARIFICATION` / `NO_APROBADA` | `PARTIAL_CLIENT_DECISION_REQUIRED` | Faltan aprobación oficial, instante exacto de expiración y recompra solapada. |
| DG-03 | Entendimiento provisional: propias + todos los descendientes | `SIGUE_ABIERTO` / `NO_APROBADA` | `BLOCKED` | No está definido qué venta califica, cuándo entra ni cómo se ajusta. |
| DG-09 | Entendimiento verbal: después de descuentos y antes de impuestos para qualification | `PROVISIONAL_VERBAL_CLARIFICATION` / `NO_APROBADA` | `PARTIAL_CLIENT_DECISION_REQUIRED` | Falta fórmula oficial y tratamiento de impuestos, envío, pago, cancelación y reembolso. |
| DG-11 | No hay período canónico | `SIGUE_ABIERTO` / `NO_APROBADA` | `BLOCKED` | No puede atribuirse una venta ni una métrica a un período de calificación oficial. |
| DG-18 | Admin-only, justificación y auditoría son provisionales; subárbol abierto | `PROVISIONAL_VERBAL_CLARIFICATION` + decisión parcial abierta / `NO_APROBADA` | `PARTIAL_CLIENT_DECISION_REQUIRED` | Faltan subárbol, vigencia y tratamiento histórico/financiero. |
| DG-19 | No se determinó quién crea o activa la afiliación | `SIGUE_ABIERTO` / `NO_APROBADA` | `BLOCKED` | Cuenta, afiliación, activación y autoridad siguen sin regla canónica. |

Ninguna regla se clasifica como `READY_FOR_CLIENT_APPROVAL` porque todas requieren que VISANA seleccione o precise al menos una decisión material. Ninguna se clasifica como `TECHNICALLY_IMPLEMENTABLE_PROVISIONALLY` para F5: que la foundation admita evaluación provisional no autoriza semántica de UI.

## Matriz de preparación F5

| Rule | Current evidence | Current status | Blocks F5? | What part of F5 it blocks | Client decision required | Can architecture proceed without it? |
|---|---|---|---|---|---|---|
| DG-01 | Pago confirmado, fecha de confirmación y un mes calendario, todo provisional | `PARTIAL_CLIENT_DECISION_REQUIRED` | Sí, parcialmente | Estado activo, fecha de vencimiento y cuenta regresiva | Confirmar trigger/inicio/duración; elegir expiración y solapamiento | Sí, como política reemplazable; no como UI con valores de negocio |
| DG-03 | Propias + descendientes como entendimiento provisional | `BLOCKED` | Sí | Team Sales y cualquier progreso que la use | Definir venta calificable, estado, cancelación, reembolso y momento de inclusión | Sí, agregador/política; no una cifra mostrable |
| DG-09 | Neto después de descuentos y antes de impuestos, provisional | `PARTIAL_CLIENT_DECISION_REQUIRED` | Sí | Volumen personal, Team Sales monetaria, progreso y base de qualification | Aprobar fórmula componente por componente y ajustes | Sí, componentes/política; no métricas monetarias canónicas |
| DG-11 | Alternativas documentadas sin selección | `BLOCKED` | Sí | Período mostrado, acumulados, rank y next-rank progress | Elegir período único, límites, zona horaria y vigencia | Sí, abstracción temporal; no resultados por período |
| DG-18 | Operación administrativa/auditable provisional; subárbol abierto | `PARTIAL_CLIENT_DECISION_REQUIRED` | No para un esqueleto sólo lectura | Consistencia futura de red y de métricas tras cambio de sponsor | Elegir movimiento, fecha efectiva e historia | Sí, manteniendo re-parenting bloqueado |
| DG-19 | Sin autoridad/evento oficial | `BLOCKED` | No para un esqueleto sólo lectura de una identidad ya existente | Origen y activación de afiliación; mensajes de elegibilidad | Definir actor/evento autorizador y controles | Sí, manteniendo provisioning no autorizado bloqueado |

## Alcance seguro y no seguro de F5

Mientras el gate siga abierto, una futura rama F5 sólo podría contener:

- ruta, navegación, título y texto explícito de “información pendiente de definición/aprobación”;
- estructura visual sin valores, fórmulas, porcentajes, fechas calculadas ni estados derivados;
- estados técnicos de carga, vacío, indisponibilidad y error sin datos de negocio inventados;
- identidad básica ya contratada y enlaces a la red existente, si se reutilizan exactamente los endpoints aprobados y sin derivar calificación.

No puede mostrar ni calcular:

- rank actual, progreso de rango o siguiente rango;
- Team Sales, volumen personal o base de calificación;
- período de calificación o acumulados dentro de un período;
- estado activo, vencimiento o cuenta regresiva;
- simulaciones presentadas como datos reales;
- resultados provisionales sin una etiqueta y autorización explícitas, que actualmente no existen para frontend.

## Dependencias de decisión

| Decisión | Determina en F5 | Condición actual |
|---|---|---|
| DG-01 | Estado de activación y vencimiento | No aprobada |
| DG-03 | Composición de ventas calificables y Team Sales | No aprobada |
| DG-09 | Contribución monetaria a volumen/calificación | No aprobada |
| DG-11 | Ventana temporal de calificación | No aprobada |
| Confirmación de umbrales L1–L8 | Cálculo y progreso de rango | No aprobada; datos técnicos provisionales |
| DG-18 | Mutación futura de red por cambio de patrocinador | No bloquea F5 sólo lectura; operación no autorizada |
| DG-19 | Ciclo de vida y autoridad de afiliación | No bloquea F5 sólo lectura sobre identidad existente; operación no autorizada |

## Gate mínimo para autorizar F5

F5 sólo podrá pasar a `READY_TO_START` cuando DG-01, DG-03, DG-09, DG-11 y los umbrales L1–L8 estén aprobados con metadatos completos y criterios de aceptación trazables. No se exige cerrar todas las reglas abiertas de Plan 3.

DG-18 y DG-19 pueden permanecer abiertas únicamente si el alcance autorizado continúa siendo de sólo lectura y no expone cambios de patrocinador, creación/activación de afiliaciones ni interpretaciones que dependan de esas operaciones.

Además del gate de negocio, cualquier UI funcional requerirá un contrato público aprobado que exponga los datos necesarios; el contrato vigente no los expone.

## Reconciliación de una futura respuesta oficial

Cuando VISANA entregue un documento oficial o el formulario diligenciado:

1. conservar el archivo recibido como evidencia nueva, sin reemplazar fuentes anteriores;
2. comparar cada campo del paquete por código de decisión;
3. clasificar el resultado como `MATCH`, `PARTIAL_MATCH`, `CONFLICT` o `NEW_INFORMATION`;
4. registrar diferencias, fuente, responsable y fecha sin resolver silenciosamente conflictos;
5. actualizar el Decision Register sólo después de la revisión de gobernanza;
6. para toda regla aprobada registrar código, versión, `effectiveFrom`, evidencia, aprobador y fecha de aprobación;
7. crear una nueva versión para efectos futuros y preservar resultados históricos conforme a ADR-027.

## Condición de salida

El gate sólo puede reevaluarse cuando las decisiones mínimas indicadas tengan respuesta canónica, responsable, fecha, versión, `effectiveFrom` y fuente oficial; cuando sus casos de borde y criterios de aceptación estén definidos; y cuando el contrato público habilite los datos requeridos. La aprobación debe crear una nueva versión con vigencia explícita y no reinterpretar resultados históricos, conforme a ADR-027.

**Invariantes:** reglas `APPROVED`: 0; sin cambio de producción; sin implementación financiera; sin cambio de comportamiento backend; sin cambio frontend; sin cambio de base de datos.
