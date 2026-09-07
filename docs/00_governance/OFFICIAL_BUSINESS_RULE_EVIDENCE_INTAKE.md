# Official Business Rule Evidence Intake

## Propósito y estado

Checklist repetible para recibir documentos oficiales, formularios o aclaraciones futuras de VISANA sin convertirlos automáticamente en reglas aprobadas ni instrucciones de implementación.

Baseline congelado al preparar este instrumento:

- backend `dev`: `5997e89c6de5b05562cc48ea8c70d3657bc03b2a`;
- frontend `dev`: `be209d65567736402d3a5418b2580e5f209c00ae`;
- `BUSINESS_RULES_GATE = DONE`;
- `F5 = WAIT_FOR_CLIENT_DECISIONS`;
- `F5_IMPLEMENTATION_AUTHORIZATION = NOT_AUTHORIZED`;
- `PROJECT_TRIGGER = WAITING_FOR_VISANA_OFFICIAL_DECISIONS`;
- reglas `APPROVED`: 0.

Este archivo es una plantilla de ingreso. No modifica el Decision Register, no aprueba una regla y no autoriza código, migraciones, contrato, frontend, despliegue o producción.

## Precedencia de evidencia

1. Documento oficial firmado por VISANA.
2. Formulario oficial diligenciado por VISANA.
3. Aclaración escrita explícita de VISANA.
4. Aclaración verbal documentada.
5. Código o datos históricos.
6. Inferencia técnica.

Una fuente inferior no reemplaza una superior. Las fuentes de niveles 4 a 6 no pueden promover una regla a `APPROVED`. Si una fuente de mayor precedencia difiere de evidencia anterior, guía la decisión futura, pero no elimina ni reescribe la evidencia histórica.

## Checklist de registro de la fuente

Complete todos los campos antes de interpretar respuestas:

| Campo | Valor de ingreso |
|---|---|
| Nombre de la fuente | |
| Tipo de fuente | Documento firmado / formulario oficial / aclaración escrita / aclaración verbal documentada / otro |
| Versión de la fuente | |
| Fecha de la fuente | |
| Fecha y hora de recepción | |
| Autor | |
| Remitente y canal de recepción | |
| Estado de firma/aprobación declarado en la fuente | |
| Códigos DG afectados | |
| Declaraciones exactas de negocio | |
| Contradicciones identificadas | |
| Ambigüedades o campos incompletos | |
| Ubicación inmutable o referencia de evidencia | |
| Responsable interno del ingreso | |
| Notas de integridad del archivo | Nombre, extensión, páginas y hash cuando corresponda |

### Controles previos

- [ ] El PM identificó la fuente y su relación con VISANA.
- [ ] El archivo original se conserva sin edición.
- [ ] Se registró una ubicación o referencia verificable.
- [ ] Se confirmó si existe firma, aprobador o respuesta formal.
- [ ] Se separaron declaraciones textuales de interpretaciones del analista.
- [ ] Cada declaración relevante se vinculó con uno o más códigos DG o se marcó como posible información nueva.
- [ ] Se revisaron contradicciones con evidencia de mayor, igual y menor precedencia.
- [ ] Se registraron preguntas para contenido ambiguo o ausente.
- [ ] Ninguna respuesta fue trasladada automáticamente a código o estado `APPROVED`.

Un archivo local no rastreado, incluido cualquier PDF presente en el workspace, no se considera evidencia oficial nueva hasta que el PM identifique su procedencia, estado y propósito. No debe eliminarse, moverse, renombrarse o incorporarse a Git sin autorización explícita.

## Orden obligatorio de procesamiento

Cuando llegue un documento oficial o un formulario diligenciado:

1. archivar o referenciar la fuente original sin alterarla;
2. registrar los metadatos del checklist de ingreso;
3. extraer las respuestas y declaraciones exactas;
4. mapear cada declaración a su DG o marcarla como información potencialmente nueva;
5. completar `BUSINESS_RULE_RECONCILIATION_TEMPLATE.md` y asignar una clasificación;
6. identificar conflictos y ambigüedades sin resolverlos silenciosamente;
7. someter la reconciliación a revisión del PM;
8. actualizar el Decision Register sólo después de esa revisión;
9. aprobar explícitamente cada regla que cuente con autoridad y evidencia suficientes;
10. completar el análisis de impacto y obtener autorización separada antes de implementar.

Las respuestas de formularios siguen exactamente el mismo proceso. Una respuesta funcional no es una instrucción para modificar código.

## Clasificación de reconciliación

| Clasificación | Definición |
|---|---|
| `MATCH` | La respuesta se alinea completamente con el entendimiento provisional actual. |
| `PARTIAL_MATCH` | Confirma sólo una parte; permanecen componentes sin respuesta o con precisión insuficiente. |
| `CONFLICT` | La evidencia recibida contradice la baseline, otra fuente aplicable o una implementación provisional. |
| `NEW_INFORMATION` | Introduce una semántica que no estaba documentada previamente. |
| `NO_RESPONSE` | La decisión requerida no fue respondida o el contenido no permite determinar una respuesta. |

`MATCH` no equivale a `APPROVED`. Toda aprobación exige evidencia válida, autoridad identificada y revisión explícita de gobernanza.

## Tratamiento de conflictos

Si una fuente oficial contradice una aclaración verbal, código/datos históricos, Working Baseline o implementación provisional:

1. registrar `BUSINESS_RULE_CONFLICT`;
2. identificar ambas fuentes y su precedencia;
3. transcribir la diferencia sin escoger una interpretación técnica;
4. preservar la evidencia y los resultados históricos;
5. solicitar aclaración si la fuente oficial no es suficiente para una regla canónica;
6. esperar decisión del PM antes de modificar estados;
7. si se aprueba una regla nueva, crear una versión con vigencia explícita y nunca reinterpretar silenciosamente cálculos pasados.

## Prioridad para el gate mínimo F5

Reconciliar primero:

1. DG-01 — activación y vencimiento;
2. DG-03 — venta calificable y composición de Team Sales;
3. DG-09 — contribución monetaria;
4. DG-11 — período de calificación;
5. QTH-F5-01 — umbrales L1–L8.

DG-18 y DG-19 pueden permanecer abiertas sólo para un F5 estrictamente de lectura, sin mutación de patrocinador o afiliación ni semántica derivada de esas operaciones. Este criterio no cambia su estado actual.

## Cierre del ingreso

| Control | Resultado |
|---|---|
| Checklist completo | Sí / No |
| Reconciliación preparada | Sí / No |
| Conflictos señalados | Sí / No / No aplica |
| Revisión PM realizada | Sí / No |
| Decision Register actualizado | Sí / No / No autorizado todavía |
| Reglas aprobadas explícitamente | Códigos o ninguna |
| Análisis de impacto completado | Sí / No / No aplica |
| Implementación autorizada por separado | Sí / No |

