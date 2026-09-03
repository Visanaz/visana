# VISANA Plan 3 - Project Charter de auditoria

**Fecha de baseline:** 2026-09-02
**Modo:** auditoria read-only; se autorizaron exclusivamente los documentos de gobierno y auditoria bajo `docs/`.
**Unidad auditada:** clon `C:\visana_auditoria\visana`.

## Proposito

Establecer una linea base verificable del clon que sera analizado en Plan 3. Este documento no valida que el clon sea produccion, que sus reglas sean aprobadas por VISANA ni que su ejecucion sea correcta.

## Objetivos de esta entrega

1. Preservar el estado Git previo a la documentacion.
2. Inventariar el codigo propio, configuracion, BD, pruebas y documentos disponibles.
3. Reconstruir la arquitectura AS-IS demostrable.
4. Separar el repositorio nuevo, el SQL legado, el plan de compensacion y la auditoria previa como fuentes independientes.
5. Registrar hallazgos sin corregir codigo, datos, configuracion ni reglas de negocio.

## Limites

Incluido: inspeccion estatica de archivos versionados y del PDF no trackeado, configuracion sin reproducir secretos, y reportes de pruebas ya presentes.

Excluido: ejecucion de Maven, Docker, migraciones, conexiones a BD/Keycloak/pagos, pruebas de seguridad activas, cambios de codigo y diseno TO-BE. Por ello, `IMPLEMENTADA` describe evidencia estatica; no equivale a `FUNCIONA` ni a regla aprobada.

## Criterios de evidencia

| Etiqueta | Uso en estos informes |
|---|---|
| HECHO | Archivo, configuracion o codigo localizado con ruta y referencia. |
| INFERENCIA | Lectura tecnica plausible, marcada expresamente y no usada como hecho. |
| PENDIENTE | Requiere ejecucion, informacion de negocio o fuente no disponible. |
| [IMPLEMENTADA] | Existe una cadena de codigo o estructura localizada. |
| [PARCIALMENTE_IMPLEMENTADA] | La cadena tiene partes sin implementacion o trazabilidad demostrada. |
| [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO] | Tabla, modelo o configuracion existe, sin uso en flujo demostrado. |
| [DOCUMENTADA_NO_VERIFICADA] | Regla localizada solo en documento. |
| [NO_EVIDENCIADO] | No se localizo evidencia dentro del alcance inspeccionado. |

## Entregables

| Area | Documento |
|---|---|
| Gobierno | `SOURCE_REGISTER.md` |
| Baseline Git | `REPO_BASELINE.md` |
| Inventario | `TECHNICAL_INVENTORY.md` |
| Arquitectura existente | `CURRENT_ARCHITECTURE.md` |
| Riesgos, vacios y legado | `LEGACY_FINDINGS.md` |

## Reglas operativas mantenidas

- No suponer equivalencia con la auditoria AS-IS anterior.
- No reproducir secretos; documentarlos como `[SECRETO_PRESENTE_NO_REPRODUCIDO]`.
- No resolver contradicciones entre documento, SQL y codigo.
- No crear implementacion, arquitectura futura, migraciones ni cambios de datos.
- Registrar cada conclusion con su fuente y diferenciar evidencia estatica de validacion runtime.

## Cierre de fase

Esta entrega termina en la documentacion de baseline. La siguiente decision corresponde a VISANA: validar las fuentes, resolver contradicciones de negocio y decidir que trabajo posterior se autoriza.
