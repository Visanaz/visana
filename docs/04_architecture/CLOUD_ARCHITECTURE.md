# Arquitectura cloud objetivo

Propuesta de despliegue gestionado y contenido para pyme: servicio Spring en Cloud Run, PostgreSQL en Cloud SQL, imágenes en Artifact Registry, secretos en Secret Manager, logs/métricas en Cloud Logging/Monitoring y archivos en Cloud Storage si el alcance lo requiere. Cloud Run permite ejecutar servicios HTTP en contenedores gestionados; Cloud SQL ofrece PostgreSQL gestionado. [Cloud Run](https://docs.cloud.google.com/run/docs/overview/what-is-cloud-run), [Cloud SQL PostgreSQL](https://docs.cloud.google.com/sql/docs/postgres).

## Implementación del primer pipeline DEV — 26 de septiembre de 2026

La propuesta anterior no acredita provisión de todos esos recursos. La guía operativa vigente está en el [README](../../README.md); las evidencias históricas y el legacy permanecen inmutables.

### Evidencia del incidente histórico

| Elemento | Evidencia comprobada |
|---|---|
| Commit | `040ec3f929c15e546571f48fa425736dd83c3b36` |
| Ejecución / intento | [36188945156](https://github.com/Visanaz/visana/actions/runs/36188945156), intento 1, 2026-09-25 20:58:54–21:02:09 UTC |
| Imagen | `us-central1-docker.pkg.dev/<proyecto enmascarado>/visana-repo/visana-api-dev:<SHA>` |
| Digest publicado | `sha256:b242ce81b91692d20e5cc5e80de44482e90b174689c444cf08b1671ba4eaa4a4` |
| Servicio / región | `visana-api-dev` / `us-central1` |
| Revisión fallida | `visana-api-dev-00001-dwp` |
| Resultado | Construcción/publicación exitosa; despliegue fallido por mensaje genérico PORT=8080 |
| Excepción de aplicación / cuenta runtime / proyecto | PENDIENTE: no obtenidos de metadata ni logs internos de revisión |

### Clasificación de hallazgos

- **A — causa respaldada por logs de aplicación:** pendiente. Los logs de Actions no contienen la cadena de excepciones del contenedor.
- **B — defectos comprobados en archivos:** puerto fijo 8084 pese a PORT=8080; CD independiente de CI/contrato; Docker recompilaba sin pruebas; ausencia de contexto Docker restringido; tag sin verificación de digest; flag que imponía exposición pública. La corrección hace explícitos puerto, gates, artefacto, digest, preservación IAM y comprobación autenticada.
- **C — hipótesis pendiente de configuración efectiva:** DataSource/Flyway/OIDC y conectividad SQL. El YAML anterior solo añadía perfil y contraseña, pero eso no demuestra ausencia de otras variables ya existentes en el servicio.
- **D — mejora opcional:** sustituir la clave histórica de despliegue por WIF y adoptar Secret Manager si el servicio no lo utiliza. Ninguna está implementada como requisito nuevo de este cierre; no crear una clave JSON alternativa. No se crearon recursos, claves, secretos o permisos en esta tarea.

### Cierre de configuración DEV — 26 de septiembre de 2026

**Alcance:** consulta y propuesta para aprobación; no configuración aplicada. Misma rama `fix/cloud-run-dev-ci-cd`, [PR #20](https://github.com/Visanaz/visana/pull/20) abierto y Draft. Punto de partida consultado: head `72bebe3f4b3110a3895b3bf37b89a327d56683d7`, base `040ec3f929c15e546571f48fa425736dd83c3b36`. [CI 36277138752](https://github.com/Visanaz/visana/actions/runs/36277138752) pasó 153 pruebas sin fallos/errores/omisiones sobre merge temporal `0d0d209a0c3d5aa91b1ba042c48e1bdc8c517886`; CD omitido. Un nuevo SHA requiere comprobar sus propios checks.

**Fuentes externas consultadas:** API GitHub autenticada como `KmDRF`: propietario `Visanaz` de tipo User; `push=true`, `admin=false`; variables de repositorio `[]`; secretos solo `GCP_PROJECT_ID` (2026-09-25 20:26:54 UTC), `GCP_CREDENTIALS` (20:26:08) y `DB_PASSWORD` (20:27:21); Environments `total_count=0`. Se consultó metadata, nunca contenido de secretos. Protección de `dev`: HTTP 404; rulesets: HTTP 403 con mensaje de restricción de plan. No se interpreta 404 como prueba definitiva de ausencia de protección ni 403 como recurso inexistente.

**Límite cloud comprobado:** gcloud no está disponible en PATH ni en las dos ubicaciones estándar revisadas; no había `GOOGLE_APPLICATION_CREDENTIALS`/`CLOUDSDK_CONFIG` en el proceso. El navegador disponible llevó Console a inicio de sesión. Se solicitó login manual; el usuario decidió continuar con acceso cloud pendiente. No hubo consultas autenticadas de recursos ni prueba de permisos cloud. No instalar SDK, copiar credenciales o crear accesos como atajo.

#### Matriz A — entradas antes del despliegue

Referencias de código: **B** = `.github/workflows/build.yml`; **D** = `.github/workflows/deploy-dev.yml`; **S** = `scripts/ci/cloud_run_dev.py`. Los números son líneas del código revisado, que no cambió durante este cierre. `AP` = `src/main/resources/application.properties`; `AD` = `src/main/resources/application-dev.properties`.

| Nombre exacto | Consumidor / finalidad | Ámbito y fuente real consultada | Valor o referencia segura / estado |
|---|---|---|---|
| `GCP_PROJECT_ID` → `PROJECT_ID` | B:20 → D:6,26,51,77; proyecto explícito, S:29 | Secret de repositorio, metadata API GitHub | **VERIFICADO** existente; contenido y proyecto real **SIN ACCESO** |
| `GCP_CREDENTIALS` | B:21 → D:8,52; autenticación | Secret de repositorio, metadata API GitHub; histórico Actions | **VERIFICADO** existente; principal/validez/permisos **SIN ACCESO**; no descargar clave |
| `DB_URL` | D:30 → S:29,32-42,73 → AD:1 | Variable repositorio → env contenedor; lista API vacía | **AUSENTE**; JDBC PostgreSQL TCP con host/puerto/BD reales pendientes; instancia no equivale a BD |
| `DB_USER` | D:31 → S:29,74 → AD:2 | Variable repositorio → env contenedor; lista API vacía | **AUSENTE**; usuario PostgreSQL previsto sin comprobar |
| `DB_PASSWORD` | B:22 → D:10,32 → S:75-84 → AD:3 | Secret GitHub, o referencia `secretKeyRef` del servicio | Metadata GitHub **VERIFICADA**; origen efectivo cloud **SIN ACCESO**; no leer valor |
| `KEYCLOAK_ISSUER_URI` | D:33 → S:43,102-108 → AP:16 | Variable repositorio → contenedor y discovery; API vacía | **AUSENTE**; issuer HTTPS real pendiente; fallback localhost no es válido DEV |
| `DEV_RUNTIME_SERVICE_ACCOUNT` → `RUNTIME_SERVICE_ACCOUNT` | D:34,86 → S:44,51,129; cuenta actual de ejecución | Variable repositorio; API vacía; metadata Cloud Run no accesible | **AUSENTE**; correo exacto de SA runtime **SIN ACCESO**; no deducir de despliegue |
| `DEV_HEALTHCHECK_CLIENT_ID` → `HEALTHCHECK_CLIENT_ID` | D:35 → S:30,110; client credentials | Variable repositorio; API vacía; cliente IdP no consultado | **AUSENTE**; identificar cliente técnico existente apto antes de proponer creación |
| `DEV_HEALTHCHECK_CLIENT_SECRET` → `HEALTHCHECK_CLIENT_SECRET` | B:23 → D:12,36 → S:30,111 | Secret repositorio, lista de metadata API | **AUSENTE**; carga segura por responsable, nunca token temporal |
| `REGION`, `SERVICE_NAME`, `REPO_NAME` | D:27-29,78-79; destino | Literales workflow + logs Actions históricos | `us-central1`, `visana-api-dev`, `visana-repo`: **VERIFICADOS** como destino/historia; existencia/configuración actual **SIN ACCESO** |
| Servicio previo y `DB_PASSWORD` Secret Manager, si existe | D:58-59 → S:48-84,155 → D:84 | Metadata del servicio: identidad, env, secret name/version, overrides | **SIN ACCESO**; no inventar nombre/proyecto/versión; conservar referencia efectiva |
| `SPRING_PROFILES_ACTIVE`, `PORT` | S:73; D:86 → AP:2 | Perfil generado y puerto proporcionado por Cloud Run | `dev`, `8080`: contrato **VERIFICADO**; no cargarlos como vars GitHub; fallback local 8084 |

No existe `environment:` en estos workflows ni Environment seleccionado; **las cinco variables y el secreto faltantes se proponen a nivel repositorio**, no en un Environment supuesto. B:19-23 pasa explícitamente cuatro secretos al `workflow_call` de D:5-13. No hay inputs ni outputs de workflow; los outputs siguientes pertenecen a steps del mismo job. El dueño es cuenta personal, por lo que no se buscan variables de organización por suposición.

`DB_PASSWORD` sigue declarado `required:true` aunque el script admite referencia Secret Manager sin consumir su valor GitHub. El secreto histórico existe: no se demostró bloqueo actual que justifique modificar ese contrato en este cierre. Su metadata no acredita que sea la contraseña correcta.

#### Matrices B/C — valores producidos por el run

| Etapa / dato exacto | Origen y consumidor | Ámbito / estado |
|---|---|---|
| B `github.sha` / `GITHUB_SHA` | Contexto del evento; openapi-contract.yml:37-46, D:44,47,66 | Automático: JAR, `source-sha.txt`, checksum, artefacto `backend-<SHA>` y tag imagen; **VERIFICADO en CI**, no cargar en Settings |
| B `.ci-artifact/app.jar`, `app.jar.sha256` | openapi-contract.yml:37-39; check_artifact.py:10-14; Dockerfile:4 | Artefacto del mismo run verificado antes de Docker; **VERIFICADO en CI** |
| A/B `dev-env.json`, `db_password_secret` | S:151-155 → D:82,84 | Temporal privado / output de referencia existente; se generan en ejecución; metadata real **SIN ACCESO** |
| B `steps.image.outputs.reference`, `steps.image.outputs.digest` | **Después** de push: D:66-72 → D:81,89 | `imagen@sha256:…` / digest AR; contrato **VERIFICADO**, nueva publicación DEV pendiente |
| A/B token aplicación previo | D:60 → S:101-116,156-157 | OIDC obtenido en ejecución antes de publicar; no pide un token almacenado; IdP real pendiente |
| C `SERVICE_URL` | `steps.deploy.outputs.url`, D:90 → D:97/S:161 | Output después de deploy; no entrada preflight; nueva URL **PENDIENTE** |
| C revisión `visana-api-dev-ci-<run_id>-<attempt>` | Contextos GitHub D:80,93-95 → S:120-141 | Describe revisión específica: Ready/digest/cuenta/puerto/env/tráfico; **PENDIENTE** deploy aprobado |
| C token Google y nuevo token aplicación | D:97 → S:162-167 | Efímeros; Google en archivo privado temporal, OIDC en memoria; no guardar en Settings |
| C salud | S:161-169, `<SERVICE_URL>/actuator/health` | HTTP 200 y JSON `status=UP`; **PENDIENTE**; 401/403 abortan |

**Sin dependencia circular demostrada:** preflight no exige digest/URL nuevos, token permanente ni revisión previa saludable. Sí describe un **servicio existente**; el histórico demuestra que existió, pero su existencia actual sigue por consultar. Si se borró, no recrearlo unilateralmente. El cliente OIDC se verifica antes de publicar; la salud nueva se verifica después de desplegar.

#### Recursos reales y consulta pendiente acotada

| Recurso / dato | Evidencia disponible | Consulta concreta en Console y permiso de lectura |
|---|---|---|
| Proyecto ID / número | ID enmascarado en Actions; secret metadata no revela valor | Selector del proyecto VISANA autorizado → información del proyecto; `resourcemanager.projects.get`. ID necesario en D; número solo si se necesita identificar service agent/caso cross-project, no nuevo input |
| Cloud Run servicio/revisión | Nombres/región y digest históricos anteriores | Cloud Run → `visana-api-dev` → revisiones/configuración/seguridad/red: runtime, Ready, ingress, URL habilitada, tráfico, probes (protocolo/puerto/path sin headers sensibles), env names y referencias; `run.services.get`, `run.revisions.get` |
| Artifact Registry | `visana-repo`, `us-central1`, publicación histórica | Artifact Registry → repositorio indicado: proyecto/ubicación/formato e imagen/digest; `artifactregistry.repositories.get`, `artifactregistry.dockerimages.get/list` |
| Despliegue / IAM | JSON auth existente, sin identidad visible; WIF no implementada | Cuenta de servicio autorizada para ese secreto, sin abrir/descargar clave; política del servicio, SA y repositorio y bindings heredados/condiciones; permisos getIamPolicy de cada recurso y `resourcemanager.projects.getIamPolicy` si la concesión es de proyecto |
| Cloud SQL | `visana-db-dev` solo **reportada por solicitante**, no metadata | Cloud SQL → esa instancia dentro del proyecto SQL confirmado: región, connectionName, IP/TLS/VPC; bases y usuarios: `cloudsql.instances.get`, `cloudsql.databases.list`, `cloudsql.users.list` |
| Red SQL | JDBC TCP implementado; proxy/Java Connector no implementados | Cloud Run → Networking y Cloud SQL → Connections; comparar VPC/egress/subred/ruta/TLS con endpoint JDBC; leer connector/subred/rutas/firewall aplicables, sin cambiar red |
| Secret Manager | Fuente efectiva DB_PASSWORD sin acceso | Solo si el servicio referencia secreto: comprobar proyecto/name/version/estado mediante `secretmanager.secrets.get`, `secretmanager.versions.get`; **no** `versions.access` para auditoría |
| IdP | Keycloak client credentials en código; issuer real ausente | Responsable realm → cliente técnico existente: client ID, issuer/discovery, autenticación y service accounts, permisos/scopes; no mostrar credencial ni inventar audience |

El workflow fija el mismo `GCP_PROJECT_ID` para Cloud Run y Artifact Registry; esa es una **restricción preparada**, no prueba de ubicación real. Cloud SQL, SA runtime y secreto pueden pertenecer a otros proyectos: sus IDs deben registrarse en el canal interno autorizado al consultar; no asumir coincidencia ni recorrer proyectos ajenos. Configuración coherente en lectura todavía no acredita conexión JDBC real.

#### Principal → recurso → permiso → motivo → evidencia

Los nombres de principales cloud quedan sin acceso; se identifican por su función y fuente exacta, no por correos inventados. No conceder permisos hasta contrastar bindings efectivos y condiciones. Los roles acotados siguientes son opciones cuando falta el permiso, no concesiones comprobadas.

| Principal | Recurso exacto o dato pendiente | Permiso necesario / opción acotada | Motivo / estado |
|---|---|---|---|
| Operador local | Recursos anteriores | Solo permisos de lectura de la tabla anterior; `logging.logEntries.list` para logs | **SIN ACCESO**; éxito local futuro no prueba permisos del pipeline |
| Cuenta activada por `GCP_CREDENTIALS` | `projects/<ID comprobado>/locations/us-central1/services/visana-api-dev` | `run.services.get/update`, `run.revisions.get`; `roles/run.developer` para despliegue habitual | Consultar y actualizar servicio/revisión; **SIN ACCESO** |
| Misma cuenta (también comprobador Google) | Servicio anterior | `run.routes.invoke`; `roles/run.invoker` solo en servicio | Invocación de salud si IAM exige identidad; **SIN ACCESO**, IAM efectivo no consultado |
| Misma cuenta | SA runtime real obtenida del servicio | `iam.serviceAccounts.actAs`; `roles/iam.serviceAccountUser` solo esa SA | Conservar/adjuntar runtime; **SIN ACCESO** |
| Misma cuenta | `projects/<ID comprobado>/locations/us-central1/repositories/visana-repo` | `artifactregistry.repositories.uploadArtifacts/downloadArtifacts`, `artifactregistry.dockerimages.get`; `roles/artifactregistry.writer` solo repositorio | Publicar/leer digest; **SIN ACCESO** |
| SA runtime | Secreto y proyecto reales, solo si hay referencia | `secretmanager.versions.access`; `roles/secretmanager.secretAccessor` solo secreto | Resolver versión DB_PASSWORD al arrancar; **SIN ACCESO** |
| Cloud Run service agent, si AR es cross-project | Repositorio real de imagen; correo derivado solo de número confirmado | `artifactregistry.repositories.downloadArtifacts`; Reader sobre ese repo | Condicional a ubicación real, **PENDIENTE DE DECISIÓN/EVIDENCIA**; no confundir con runtime |
| Usuario PostgreSQL de `DB_USER` | BD/schema reales | Autenticación y privilegios SQL de aplicación/Flyway aprobados | Independiente de IAM; identidad/privilegios/esquema **SIN ACCESO**; no otorgar superuser por defecto |
| Cliente técnico `DEV_HEALTHCHECK_CLIENT_ID` | Issuer/realm real, endpoint aplicación | JWT válido; SecurityConfig.java:33-39 no exige rol/scope extra | **AUSENTE** en GitHub; cliente real pendiente. No usar persona/admin/actor comercial |
| Administrador de bindings | Solo recurso del binding faltante aprobado | Servicio `run.services.getIamPolicy/setIamPolicy`; SA `iam.serviceAccounts.getIamPolicy/setIamPolicy`; AR `artifactregistry.repositories.getIamPolicy/setIamPolicy`; secreto `secretmanager.secrets.getIamPolicy/setIamPolicy` | Agregar/quitar únicamente binding exacto; **SIN ACCESO**; no reemplazar políticas completas |
| Cristian o colaborador autorizado GitHub | Repository Actions de `Visanaz/visana` | Colaborador del repo personal; por API `Variables:write` y `Secrets:write` respectivamente | Preparar futura carga; lectura/push KmDRF comprobados, escritura Settings no ejercida; administración de Environment/protección no inferida |

El código usa clave JSON propia, **sin WIF, impersonación ni `id-token: write`**; por eso no hay claims GitHub/Environment que validar ni se pide Token Creator por hipótesis. Si se decide WIF más adelante, validar el evento real `push`/`refs/heads/dev`/repositorio y el subject/Environment que efectivamente use ese diseño. TCP PostgreSQL por IP no requiere por sí mismo `cloudsql.instances.connect`; ese permiso corresponde a integración proxy/connector aprobada y no reemplaza VPC, TLS ni privilegios SQL.

**Referencia Secret Manager:** D:84 vuelve a pasar la referencia existente cuando la hay. La [guía de configuración de secretos Cloud Run](https://docs.cloud.google.com/run/docs/configuring/services/secrets) enumera `roles/run.admin` para ese recorrido, mientras la guía de despliegue ordinario enumera Developer. Tener `run.services.update` no demuestra por sí solo que la ruta concreta de actualización de secretos esté cubierta: comprobar política/operación efectiva y autorizar solo la diferencia necesaria; no agregar Admin automáticamente ni afirmar que Developer fue probado suficiente.

**Privilegios PostgreSQL para Flyway:** AD:1-3 no define credencial de migración separada; el mismo `DB_USER` utiliza DataSource y Flyway. Los SQL vigentes `src/main/resources/db/migration/V1`–`V8` contienen 23 CREATE TABLE, 15 CREATE INDEX, dos INSERT (V8:121,139) y constraints/FK, sin GRANT ni operaciones de reversión. El DBA debe contrastar LOGIN/autenticación, CONNECT a BD real, USAGE/CREATE en esquema efectivo existente, propiedad para índices/constraints y REFERENCES si hay objetos ajenos, INSERT para semillas e historial, SELECT para validar `flyway_schema_history`. Flyway crea su propia tabla/PK/índice si falta. No asumir esquema `public`; CREATE en BD solo sería necesario si se debe crear un esquema ausente. No se demuestra necesidad de SUPERUSER, CREATEDB/CREATEROLE, ALL ni repair. DML de todos los casos de negocio no se deriva de ese DDL; salud JDBC no ejecuta DML comercial. [Privilegios PostgreSQL](https://www.postgresql.org/docs/current/ddl-priv.html), [historial Flyway PostgreSQL](https://raw.githubusercontent.com/flyway/flyway/flyway-10.20.1/flyway-database/flyway-database-postgresql/src/main/java/org/flywaydb/database/postgresql/PostgreSQLDatabase.java).

Fuentes de mínimos/alcances: [despliegue Cloud Run](https://docs.cloud.google.com/run/docs/deploying), [identidad runtime](https://docs.cloud.google.com/run/docs/configuring/services/service-identity), [Artifact Registry IAM](https://docs.cloud.google.com/artifact-registry/docs/access-control), [Secret Manager IAM](https://docs.cloud.google.com/secret-manager/docs/manage-access-to-secrets), [variables GitHub](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-variables), [API Variables](https://docs.github.com/en/rest/actions/variables#create-a-repository-variable), [API Secrets](https://docs.github.com/en/rest/actions/secrets#create-or-update-a-repository-secret). Configuración nueva de secretos/casos cross-project requiere contrastar permisos específicos; esta propuesta no cambia referencias ni IAM.

#### Salud: contrato y acceso

- Ruta `/actuator/health`, respuesta exigida HTTP **200** y JSON `status=UP`; detalles `never`, AP:22-24. DataSource Health de Spring Boot 3.4.0 contribuye comprobación JDBC cuando hay DataSource; los guards rechazan overrides de health/datasource. `UP` acredita esa comprobación agregada, no integridad financiera ni reglas de negocio.
- Spring requiere JWT válido del issuer (SecurityConfig.java:33-39). No hay audience de aplicación configurada ni autorización exclusiva para salud: un cliente sin roles no debe describirse como incapaz de acceder a otros endpoints autenticados. Revisar alcance del cliente existente con el responsable; no ampliar seguridad ni inventar `aud` en este cierre. [Spring JWT](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html).
- `Authorization` transporta bearer Keycloak y `X-Serverless-Authorization` el ID token Google. Audience Google = URL exacta de servicio devuelta, sin path. IAM Cloud Run efectivo no se consultó: `run.routes.invoke` es necesario cuando IAM exige identidad; no se declara público/privado por el flag histórico. [Autenticación Google](https://docs.cloud.google.com/run/docs/authenticating/service-to-service).
- El pipeline usa `client_credentials`, por lo que necesita un cliente técnico confidencial con service accounts habilitadas; reutilizar uno apto si existe. Crear uno solo tras decisión y permiso fino de gestión del cliente, no administración completa de realm. El script obtiene tokens efímeros, nunca un token guardado. [Keycloak](https://www.keycloak.org/docs/latest/server_admin/index.html#_service_accounts).
- No se configuran probes en el repositorio; deben consultarse las heredadas. Una probe HTTP hacia health protegido sin JWT válido recibiría 401; headers estáticos no obtienen tokens. No poner token/secret de cliente permanente en probe. Una probe TCP de plataforma no consulta JDBC y no equivale al test posterior autenticado. [Probes](https://docs.cloud.google.com/run/docs/configuring/healthchecks).
- Desde `ubuntu-latest` se necesita alcance al discovery/token endpoint y URL directa del servicio; Cloud Run necesita alcanzar issuer/JWKS. Ingress `internal` o `internal-and-cloud-load-balancing` impide llamada directa pública `run.app` aun con IAM correcto. Si así está configurado, la ruta del comprobador exige decisión específica; no abrir ingress ni exposición automáticamente. El runner no necesita conectarse a la BD real. [Ingress](https://docs.cloud.google.com/run/docs/securing/ingress).

#### Incidente: consulta que quedó sin realizar

Cloud Logging, proyecto **confirmado** → Logs Explorer, intervalo UTC **2026-09-25 20:58:54–21:02:09** (ampliar solo para capturar arranque causal):

```text
resource.type="cloud_run_revision"
resource.labels.service_name="visana-api-dev"
resource.labels.revision_name="visana-api-dev-00001-dwp"
timestamp>="2026-09-25T20:58:54Z"
timestamp<="2026-09-25T21:02:09Z"
```

Permiso `logging.logEntries.list` en el proyecto/log view autorizado; si la entrada está en bucket/vista restringida, verificar acceso a esa vista. Revisar solo excepción relevante en canal privado, relacionando revisión/digest/run de la tabla histórica. **No ejecutado por falta de sesión/proyecto comprobado**, no un 403 ni logs vacíos. No hay causa confirmada por aplicación; el defecto de puerto corregido es evidencia de archivos, y DataSource/Flyway/OIDC/red siguen hipótesis. La ausencia de logs antiguos por sí sola no impide el nuevo despliegue si sus prerrequisitos y evidencia posterior quedan cerrados.

#### Acciones externas para aprobación — orden de Cristian

Cada fila es una operación **futura**, no autorizada/aplicada por esta ejecución. No tocar los tres secretos históricos ni cargar credenciales cloud al computador. Los valores de infraestructura aún desconocidos deben mantenerse en el canal interno autorizado al verificarlos.

| Orden / pantalla | Actual comprobado → propuesta concreta | Executor / permiso | Efecto, comprobación posterior y reversión |
|---|---|---|---|
| 1. Console, proyecto VISANA autorizado; Cloud Run/AR/SQL/Secret Manager | Sin sesión cloud → completar lectura acotada de tablas anteriores: proyecto ID, runtime, desplegador, SQL connectionName/región/BD/usuario, ruta TCP/TLS, refs/versiones, IAM/ingress/probes | Operador existente con permisos de lectura enumerados; login manual, sin clave nueva | No cambia recursos. Registrar solo campos sanitizados; ante 403 marcar sin acceso, no ausencia. Lectura no requiere reversión |
| 2. GitHub → Settings → Secrets and variables → Actions → Variables → New repository variable | Cero variables → **cinco**: `DB_URL`, `DB_USER`, `KEYCLOAK_ISSUER_URI`, `DEV_RUNTIME_SERVICE_ACCOUNT`, `DEV_HEALTHCHECK_CLIENT_ID`, con valores derivados de paso 1/IdP | Cristian/colaborador autorizado; Variables:write por API | Preflight puede consumir config; comprobar lista/nombres/ámbito y valores no sensibles contra fuente interna. Revertir eliminando solo nuevas vars antes de autorizar deploy; si ya hubo deploy, eso no revierte contenedor |
| 3. IdP → realm/cliente técnico existente → GitHub Actions → Secrets → New repository secret | Secret ausente; cliente no comprobado → identificar cliente apto y cargar **solo** `DEV_HEALTHCHECK_CLIENT_SECRET` por canal seguro | Gestor del cliente (`manage` fino); colaborador Secrets:write para GitHub. `create-client` únicamente si falta cliente y se aprueba | Habilita obtención de bearer técnico, sin roles admin/negocio. Comprobar metadata y autenticación en preflight posterior. Revertir nuevo secret/binding/config exactos; registrar estado previo; no divulgar credencial |
| 4. Google IAM en servicio, SA runtime, repo AR y secreto real | Bindings desconocidos → **solo si falta**, agregar principal/permiso/recurso de la matriz anterior tras confirmar identidad y condiciones | Administrador con get/setIamPolicy del recurso específico | Permite deploy/actAs/publicación/invocación o acceso runtime al secreto. Comprobar binding efectivo/herencia/condición; revertir solo binding añadido, sin borrar política ni identidades |
| 5. Cloud Run Networking/Probes y Cloud SQL Connections | Estado desconocido → confirmar compatibilidad del diseño TCP y ruta de salud; **no se propone cambio de red/ingress amplio** | Responsable técnico con lectura; cambio específico solo tras diagnóstico y nueva aprobación | Si falta ruta o probe incompatible, cerrar decisión concreta antes de merge. Verificar configuración sin arrancar backend/migrar BD. No hay cambio aprobado que revertir |
| 6. PostgreSQL/Flyway, luego PR #20 | BD/privilegios/revisión saludable anterior no comprobados → responsable confirma esquema/migraciones existentes, permisos y reversión; revisor evalúa SHA/checks vigentes | DBA de la BD confirmada y aprobador del proyecto; sin ejecutar SQL en este cierre | Autorizar o abortar según compatibilidad y plan. No asumir rollback funcional a `00001-dwp`; imagen/tráfico no revierte esquema ni datos |

**Ya comprobado:** CI aislado del SHA inicial, contrato sin circularidad, nombres de destino históricos, tres secretos metadata y ausencia exacta de cinco variables/secreto/Environment. **Cambios necesarios pendientes de aprobación:** cargas GitHub de filas 2/3 una vez conocidos sus valores; bindings fila 4 solo si lectura demuestra faltantes. **Datos/decisiones no resueltos:** filas 1/5/6 y cliente IdP. **Opcional, no gate DEV añadido:** migración WIF/Secret Manager y actualización de majors de acciones; no crear otro pipeline.

#### Recorrido preparado después de aprobación

1. Cerrar config externa y revisión del PR; conservar Draft hasta instrucción explícita. Antes de integrar, verificar checks requeridos con administrador si las API de protección siguen sin dar evidencia suficiente.
2. Integración autorizada a `dev` → CI del **SHA integrado**, 153 pruebas/contrato/artefacto; si falla, no publica/despliega. No reutilizar el CI del merge temporal del PR como evidencia del integrado.
3. CD del mismo run valida fuentes, artefacto, servicio/identidad/overrides e IdP; luego publica por SHA y obtiene digest; despliega por digest conservando IAM/red/secretos.
4. Consulta revisión `ci-<run>-<attempt>`, exige Ready/digest/cuenta/puerto/perfil/config/tráfico 100%; solo después obtiene los tokens de salud y exige HTTP 200/UP. Registrar run/SHA/digest/revisión/resultado sanitizados. La comprobación JDBC ocurre en el contenedor, no en el runner.
5. Abortar ante preflight, IAM/ingress, despliegue, revisión/tráfico o salud fallidos. El workflow **no realiza rollback automático**: el despliegue puede haber alterado tráfico antes de fallar el test. El operador evalúa estado real con autorización antes de actuar.
6. No está demostrada ninguna revisión anterior saludable. Si existe una validada y compatible con esquema, una reversión de tráfico requiere aprobación específica. Si no existe, no hay rollback funcional acreditado: detener promoción y preparar corrección/revert revisado en el flujo autorizado. Nunca asumir que revertir imagen revierte Flyway; no down migrations ni repair.

**Estados:** APTO PARA REVISIÓN de propuesta técnica/documental; PENDIENTE DE CONFIGURACIÓN EXTERNA por cargas exactas; BLOQUEADO POR ACCESO para validación cloud/IAM/SQL/logs. No equivalen a permiso de merge/deploy ni a CD funcionando.

| Ambiente | Propósito | Controles |
|---|---|---|
| Local | desarrollo aislado | secretos locales separados, datos sintéticos |
| DEV | integración temprana | DB/identity separados, observabilidad básica |
| QA | validación P0 | datos controlados, pruebas contract/E2E aprobadas |
| PROD | operación | cuentas de servicio mínimas, alertas, backups y auditoría |

STAGING independiente queda por decidir según riesgo y capacidad. No se crean recursos, pipelines ni contenedores en esta fase.
