# COST-DEV-01 / A-07 — contrato interno de preparación

**Estado:** fuente aislada revisable; no se creó ni desplegó un Workflow. R1 SHA-256:
`1c22316416fc037327ae4c6c345f72364164b979725305af6e1101a5ebb6c861`.
La operación A-07 en R1 es **crear** el Workflow; este commit prepara su fuente y no
marca A-07 como aplicada. A-06 (guard de CI/QA), A-04 (trigger) y A-08/A-09
(Scheduler) permanecen separados.

## Límite actual

`control.py` modela decisiones y ejecuta solamente puertos inyectados. La suite
inyecta `MemoryStore` y `FakeRuntime`; no importa Google SDK ni abre red.
`workflow.json` es una definición de Workflows en JSON de **solo preflight**:
valida destino/operación, devuelve un plan dry-run y rechaza START/STOP con
`dryRun=false`. No contiene una ruta de mutación. La sintaxis JSON y su
estructura se validan localmente; el compilador de Google Workflows y los
permisos reales no se han probado. No desplegar esta fuente como solución
operativa: falta integrar los adaptadores y acreditar sus señales.

La ausencia de `dryRun` significa `true`. `STATUS` del artefacto de Workflow
devuelve `NOT_CONNECTED`, nunca una afirmación de estado cloud. El modelo
`control.py` permite simular START/STOP con puertos falsos para comprobar la
máquina de estados; no existe adaptador productivo que pueda mutar Google Cloud.

## Allowlist y estado

Destino inmutable: proyecto `visana-erp-dev`, región `us-central1`, SQL
`visana-db-dev`, Run `visana-api-dev`, bucket
`visana-erp-dev-schedule-state`, objeto `cost-dev-01/current.json`.
Los parámetros externos solo eligen una de cinco operaciones. Un destino
suministrado y distinto se rechaza antes de toda transición. Keycloak permanece
`NOT_PROVISIONED`; START no crea ni invoca Keycloak.

Estado JSON versión 1, máximo 256 KiB: entorno/proyecto, operación, estado,
executionId/eventId, fence, timestamps UTC, leaseUntil, requestedBy,
lastSuccessfulBackup, snapshot **solo de escalado**, errorCode. No contiene
tokens ni configuración completa de servicios. La primera escritura simulada
usa generationMatch=0; las siguientes usan la generación leída. Un 412
equivalente aborta y relee para identificar otro dueño; no hay last-write-wins.
Un estado parcial ERROR/MANUAL_HOLD exige recuperación manual antes de nueva
operación. La simulación no prueba CAS de Storage real ni IAM condicionado.

Estados: OFF → STARTING_SQL → WAITING_SQL → STARTING_SERVICES → ON;
ON → FREEZING → QUIESCING → WAITING_BACKUP → STOPPING_SERVICES →
STOPPING_SQL → OFF. Los fallos pasan a ERROR o MANUAL_HOLD. Cada transición
persistida registra anterior/nuevo mediante `previousState/state`, timestamp
y executionId. El ID duplicado no vuelve a ejecutar mutaciones.

## Contrato REST/API futuro, sin llamadas en este artefacto

| Operación | Método y recurso exacto | Identidad/permisos previstos | Cuerpo/campo mutable | Resultado, timeout y retry |
|---|---|---|---|---|
| Estado SQL | GET `https://sqladmin.googleapis.com/sql/v1beta4/projects/visana-erp-dev/instances/visana-db-dev` | control, R: `cloudsql.instances.get` | ninguno | leer `settings.activationPolicy` y estado; fallo/unknown bloquea |
| Start/stop SQL | PATCH misma URL | control, R: `cloudsql.instances.get/update` | solo `{"settings":{"activationPolicy":"ALWAYS" o "NEVER"}}` | operación asíncrona; obtenerla con GET `.../projects/visana-erp-dev/operations/{id}`, R `cloudsql.instances.get`; start máximo 15 min, stop nunca forzado por reloj; reintento acotado solo tras releer resultado |
| Backups SQL | GET `https://sqladmin.googleapis.com/sql/v1beta4/projects/visana-erp-dev/instances/visana-db-dev/backupRuns` | control, R: `cloudsql.backupRuns.list/get` | ninguno | SUCCESSFUL, inicio después del fence y edad ≤24 h son necesarios; esos metadatos por sí solos **no acreditan** cobertura recuperable hasta el fence |
| Run actual | GET `https://run.googleapis.com/v2/projects/visana-erp-dev/locations/us-central1/services/visana-api-dev` | control, S: `run.services.get` | ninguno | guardar solo `scalingMode/minInstanceCount/maxInstanceCount/manualInstanceCount`; no copiar env/secretos |
| Run stop | PATCH misma URL con updateMask acotado a `scaling.scalingMode,scaling.manualInstanceCount` y, si la API lo exige, `launchStage` | control, S: `run.services.update` | MANUAL, manualInstanceCount=0 | operación v2; poll GET `https://run.googleapis.com/v2/{operation.name}` con `run.operations.get`; comprobar tags/revisiones/consumidores antes de SQL |
| Run start | PATCH misma URL con updateMask solo de campos de escalado que requiera el snapshot | control, S: `run.services.update` | restaurar modo/min/max/manualCount exactos | esperar operación; preservar imagen, env, secretos, IAM, ingress y tráfico; health autenticado sigue pendiente |
| Estado CAS | GET metadata `https://storage.googleapis.com/storage/v1/b/visana-erp-dev-schedule-state/o/cost-dev-01%2Fcurrent.json`; GET contenido de la generación exacta | control, O: `storage.objects.get` condicionado al objeto | ninguno | distinguir 404 de error/permiso; no inferir ausencia con lectura fallida |
| CAS write | POST `https://storage.googleapis.com/upload/storage/v1/b/visana-erp-dev-schedule-state/o?uploadType=media&name=cost-dev-01%2Fcurrent.json&ifGenerationMatch={0 o generación leída}` | control, O: `storage.objects.create`, y `storage.objects.delete` para reemplazo | JSON de estado ≤256 KiB; solo objeto autorizado | 412: releer, comprobar ejecución/fence, abortar si hay otro dueño; sin sobrescritura ciega |

Estos endpoints requieren OAuth de la identidad del futuro Workflow. No se
incluyen headers Authorization ni respuestas completas en logs. SQL devuelve
una operación asíncrona; Run también. Una respuesta de PATCH no demuestra
readiness. Los reintentos previstos 5/15/30 s y el plazo de arranque 15 min
son límites, no autorización de operación real.

## Guardas sin fuente acreditada

STOP exige congelación de CI/QA, ausencia de despliegues y operaciones SQL,
escritores y transacciones quiescibles, fence durable, backup nuevo exitoso
que **cubra recuperablemente** la última escritura, y ausencia de escrituras
posteriores. A-06 todavía no integra el lease/fence de deploy. El endpoint
autenticado de health depende de identidad/Keycloak aún no provisionados.
La metadata de backup no prueba por sí sola restauración hasta el fence.
Sin cualquiera de estas señales, el adaptador futuro debe emitir
MANUAL_HOLD/ERROR, conservar SQL encendido y alertar. La hora 23:00
`America/Bogota` es límite de espera, nunca orden de apagado. La prueba
aislada de recuperación sigue siendo etapa B con autorización separada.

Logs admitidos: executionId, operation, dryRun, resource, previousState,
newState, status, duration, errorCode. `safe_log` usa allowlist y rechaza
valores no codificados; nunca registrar headers, tokens, secreto, DB_URL
completa ni el documento completo del store.

## Configuración horaria R1

`schedule.json` conserva las ventanas exactas de R1 y se inyecta en el
modelo puro. Una diferencia respecto al contrato aprobado aborta.

`America/Bogota`: SQL 07:30 L–V, aviso 17:30, freeze 17:45,
quiescencia objetivo 18:00, ventana de **inicio** del backup 18:00–22:00,
espera máxima 23:00. Son entradas para una futura etapa de Scheduler;
este directorio no contiene jobs ni recurrencia activa.

## Fuentes

- R1, hojas `Lote!A19:J34` y `Operacion!A5:D19`, SHA arriba.
- [Cloud SQL start/stop](https://docs.cloud.google.com/sql/docs/postgres/start-stop-restart-instance).
- [Permisos Cloud SQL](https://docs.cloud.google.com/sql/docs/postgres/iam-permissions).
- [Cloud Run manual scaling](https://docs.cloud.google.com/run/docs/configuring/services/manual-scaling).
- [Cloud Run patch](https://docs.cloud.google.com/run/docs/reference/rest/v2/projects.locations.services/patch).
- [Storage objects.insert CAS](https://docs.cloud.google.com/storage/docs/json_api/v1/objects/insert).

No se solicita ampliación de R/S/O/T. Antes de completar el adaptador live se
deben acreditar las señales de quiescencia, cobertura recuperable y health,
la expresión O a nivel bucket de forma independiente y el contrato exacto de
PATCH de escalado de Run. A-07, B y C siguen sin ejecutarse.
