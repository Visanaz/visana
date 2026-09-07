# Business Rule Reconciliation Template

## Uso

Plantilla vacía para comparar evidencia oficial futura con la baseline vigente. Debe utilizarse después de completar `OFFICIAL_BUSINESS_RULE_EVIDENCE_INTAKE.md` y antes de modificar el Decision Register.

No completar con supuestos. No usar `MATCH` como aprobación automática. No implementar resultados de esta tabla sin revisión del PM, aprobación explícita y autorización técnica separada.

## Identificación de la reconciliación

| Campo | Valor |
|---|---|
| ID de reconciliación | |
| Fuente de ingreso relacionada | |
| Versión/fecha de la fuente | |
| Ubicación de evidencia | |
| Analista | |
| Fecha de análisis | |
| Revisor PM | |
| Fecha de revisión | |

## Tabla de reconciliación

No diligenciar respuestas futuras en la versión base de esta plantilla. Agregue una fila por decisión o declaración recibida.

| Decision Code | Current Status | Current Understanding | Incoming Evidence | Classification | Conflict? | Clarification Required? | Proposed New Status | Approval Evidence | Effective From | Impact Backend | Impact Frontend | Impact DB | Impact OpenAPI | Impact Tests | Impact Historical Results |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| | | | | | | | | | | | | | | | |

Valores permitidos para `Classification`: `MATCH`, `PARTIAL_MATCH`, `CONFLICT`, `NEW_INFORMATION` o `NO_RESPONSE`, definidos en el checklist de ingreso.

Si `Conflict? = Sí`, registrar `BUSINESS_RULE_CONFLICT` y detallar todas las fuentes. `Proposed New Status` es una propuesta para revisión; no modifica por sí sola el estado oficial.

## Metadatos obligatorios para una aprobación futura

| Campo | Valor |
|---|---|
| Decision Code | |
| Versión de regla | |
| `effectiveFrom` | |
| Evidencia oficial | |
| Aprobador y cargo | |
| Fecha de aprobación | |
| Alcance y casos de borde | |
| Criterios de aceptación | |
| Decisión PM de implementación | Pendiente / Autorizada / No autorizada |

Sin estos metadatos y la revisión de gobernanza, el resultado continúa no aprobado aunque la clasificación sea `MATCH`.

## Plantilla de análisis de impacto

Complete una matriz por cada regla que llegue a aprobación. Este análisis identifica alcance; no autoriza la implementación.

| Área | Evidencia actual | Cambio requerido o por confirmar | Riesgo/integridad | Validación necesaria | Autorización |
|---|---|---|---|---|---|
| Domain model | | | | | |
| Application services | | | | | |
| Persistence | | | | | |
| Rule versioning | | | | | |
| Historical calculations | | | | | |
| OpenAPI | | | | | |
| Frontend | | | | | |
| Tests | | | | | |
| Migration | | | | | |
| Production data | | | | | |
| Documentation | | | | | |

### Preguntas de integridad histórica

- [ ] ¿La regla nueva tiene una versión propia y `effectiveFrom` explícito?
- [ ] ¿Cada resultado futuro podrá referenciar la versión exacta usada?
- [ ] ¿Los resultados históricos permanecen sin sobrescritura o reinterpretación silenciosa?
- [ ] ¿Un cambio histórico excepcional está expresamente ordenado, evaluado y trazado?
- [ ] ¿Los ajustes o correcciones se representan mediante resultados nuevos o registros compensatorios?
- [ ] ¿Se documentaron impactos potenciales en backend, frontend, DB, OpenAPI y pruebas?

Estas preguntas preservan ADR-027: una aprobación nueva no cambia automáticamente los cálculos anteriores.

## Resultado de revisión

| Resultado | Valor |
|---|---|
| Clasificaciones revisadas por PM | Sí / No |
| Conflictos resueltos mediante evidencia suficiente | Sí / No / No aplica |
| Decision Register autorizado para actualización | Sí / No |
| Regla aprobada explícitamente | Sí / No |
| Análisis de impacto aceptado | Sí / No / No aplica |
| Implementación autorizada | Sí / No |
| Observaciones | |
