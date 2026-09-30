# VISANA Core API

Backend existente de VISANA: Java/Spring Boot, persistencia y migraciones, módulos de comercio, identidad, red/genealogía, calificación/volumen, compensación y ledger. La existencia de un módulo no demuestra aprobación de sus reglas ni funcionamiento productivo completo. El contrato revisado está en `openapi/visana-api-v1.json`. El frontend pertenece a otro repositorio.

## Estado

- **IMPLEMENTADO:** CI de backend/contrato y empaquetado; CD DEV condicionado al éxito de esa verificación y a configuración externa explícita.
- **VERIFICADO HISTÓRICAMENTE:** el CI del SHA `040ec3f929c15e546571f48fa425736dd83c3b36` pasó el 25 de septiembre de 2026. La imagen fue publicada; el despliegue falló al crear la revisión `visana-api-dev-00001-dwp`.
- **VERIFICADO EN PR:** [CI 36277138752](https://github.com/Visanaz/visana/actions/runs/36277138752) asociado al head `72bebe3f4b3110a3895b3bf37b89a327d56683d7`, ejecutado sobre su merge temporal: 153 pruebas sin fallos, errores ni omisiones, PostgreSQL aislado, contrato y Docker Linux amd64. CD omitido por tratarse de PR.
- **CAPTURA CLOUD:** proyecto `visana-erp-dev`, Cloud Run/Artifact Registry/Cloud SQL consultados. Los logs históricos confirmaron URL DataSource inválida y el desajuste adicional 8084/8080. El servicio tiene ingress All y binding allUsers; no exige actualmente token Google.
- **CORRECCIÓN VALIDADA:** [CI 36291639215](https://github.com/Visanaz/visana/actions/runs/36291639215), head `fbc570d4a7b91f519326bb44dae363efe640f5fe`: 161 pruebas Java sin fallos/errores/omisiones, diez Python, seis suites PostgreSQL 18.3, mantenimiento JDBC 42.7.13, migraciones, salud y JWT/JWKS aislados. CD omitido. [PR #20](https://github.com/Visanaz/visana/pull/20) sigue Draft; no se desplegó ni verificó conexión Cloud SQL real.
- **CONFIGURACIÓN DEV:** L-01–L-06 aplicado: SQL Client para runtime, Secret Accessor únicamente en `visana-dev-db-password` y cuatro variables GitHub cargadas. Allow permite las operaciones; evaluación total desconocida por políticas no consultables, sin denegación demostrada. No se repitió el lote.
- **SQL REAL 2026-09-28:** base `visana_dev`, usuario `visana_app_dev` y PostgreSQL **18.6** comprobados. El ajuste autorizado quedó aplicado y verificado: CONNECT y public USAGE/CREATE presentes; CREATEDB/CREATEROLE false y sin membresías administrativas. La captura posterior no tenía tablas/secuencias/historial Flyway. Esto no acredita conexión desde Cloud Run ni rotación de credencial.
- **KEYCLOAK PREPARADO:** imagen oficial 26.7.5 fijada por digest, realm sanitizado `visana-erp`, cliente técnico y PostgreSQL separado. Arranque, persistencia, discovery/JWKS y JWT real contra la seguridad existente del backend pasaron en aislamiento. KC-00F: 0 CRITICAL, 5 HIGH y 0 secretos en Trivy; Gitleaks registró un finding heredado de la base, 0 en source y 0 nuevos. Compatibilidad aislada PASS. La imagen no se ha publicado ni desplegado; recursos e issuer cloud todavía no creados. [Resultados y lote futuro concreto](docs/04_architecture/CLOUD_ARCHITECTURE.md#validación-sql-y-preparación-keycloak-2026-09-28).

## Stack y requisitos

Versiones efectivas: Java 21, Spring Boot 3.4.0, Maven 3.9.9, Cloud SQL PostgreSQL Connector **1.30.0**, Flyway Core/PostgreSQL/MySQL **11.14.0**, PostgreSQL JDBC **42.7.13**, Hikari **5.1.0**, Spring Security **6.4.1**, Keycloak Admin Client 26.0.0 y Springdoc 2.7.0. Flyway 11.14.0 declara PostgreSQL probado hasta 18. JDBC cambia desde 42.7.4 mediante `postgresql.version` del parent; Boot mantiene su gestión de Spring/Security. El contexto MySQL permanece separado de la evidencia histórica.

Revisión acotada: 42.7.13 incluye correcciones posteriores a CVE-2025-49146 (SCRAM/CPU y channel binding). Su compatibilidad se verifica en CI con PostgreSQL 18.3, Connector empaquetado y Flyway; no demuestra conexión cloud ni ausencia de vulnerabilidades. Boot 3.4.x terminó soporte abierto y Security 6.4.1 conserva un aviso pertinente sobre cabeceras HTTP. La [guía cloud](docs/04_architecture/CLOUD_ARCHITECTURE.md#revisión-acotada-de-dependencias) documenta condiciones y la decisión de soporte/migración pendiente, sin actualizar Spring de forma independiente.

Desarrollo: JDK 21, Maven Wrapper y configuración externa del entorno. Docker con motor Linux es necesario para las seis suites PostgreSQL con Testcontainers y para construir la imagen. Los archivos `.env` no son cargados automáticamente por Maven/Spring: exportar las variables al proceso sin publicarlas.

## Desarrollo y pruebas

El proyecto permite aislar la salida Maven. Es obligatorio hacerlo si existen cambios ajenos o artefactos históricos pendientes bajo `target/`:

```powershell
# Con JAVA_HOME apuntando a un JDK 21 y su bin en el PATH de este proceso:
.\mvnw.cmd --batch-mode "-Dvisana.build.directory=$env:TEMP\visana-build" verify
.\mvnw.cmd --batch-mode "-Dvisana.build.directory=$env:TEMP\visana-build" "-Dtest=ServerPortConfigurationTest" test
python -B -m unittest discover -s scripts/ci -p 'test_*.py'
```

En CI se usa `./mvnw --batch-mode clean verify` exclusivamente en un runner independiente. CI exige Docker y comprueba que las suites PostgreSQL ejecutaron pruebas sin saltos; la ausencia de Docker local puede producir pruebas omitidas y no equivale a validación PostgreSQL.

Verificación local inicial: Java 21.0.11 y salida externa, 160 pruebas, cero errores/fallos, siete omitidas por Docker ausente; diez pruebas Python de guards pasaron. El candidato añade una prueba de configuración técnica DEV obligatoria; su resultado definitivo corresponde al CI del nuevo SHA. Las seis suites usan `postgres:18.3-alpine` y comprueban por JDBC su versión real. La suite de ciclo de vida verifica BD vacía, V1–V8, historial válido, segundo arranque sin reaplicar y salud UP/DOWN/UP con fallo sintético controlado. No se usa Cloud SQL ni credenciales Google.

El perfil `dev` usa exclusivamente el conector para Cloud SQL; requiere configuración externa aprobada. Para una BD TCP aislada utilizar la configuración local/test correspondiente, sin Google ADC. El siguiente comando DEV no debe ejecutarse contra la instancia real sin autorización de despliegue/migraciones:

```powershell
.\mvnw.cmd "-Dvisana.build.directory=$env:TEMP\visana-build" spring-boot:run "-Dspring-boot.run.profiles=dev"
```

`server.port=${PORT:8084}` conserva **8084 sin PORT** y usa el puerto suministrado cuando existe. Cloud Run proporciona `PORT`; no configurarlo como variable ordinaria. El test de puerto arranca un servidor servlet aislado en 8084, 8080 y 18084 sin conectar BD/OIDC ni cambiar la seguridad de la aplicación. `EXPOSE 8080` es metadata de Docker.

## Perfiles y variables

`local` conserva la referencia MySQL local histórica; `dev`, `qa` y `prod` exigen `DB_URL`, `DB_USER` y `DB_PASSWORD`. `test` acompaña las pruebas aisladas. No usar credenciales reales en pruebas ni permitir fallback localhost en cloud. No hay DataSource personalizado ni conexión Flyway independiente demostrados en esta base: se usa el DataSource de Spring Boot. Se conservan Flyway, `ddl-auto=validate` y seguridad OIDC.

| Nombre | Finalidad / origen | Requisito DEV |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Workflow fija `dev` | Obligatorio |
| `PORT` | Cloud Run lo proporciona; fallback local 8084 | No definir en GitHub |
| `DB_URL` | URL JDBC del conector oficial, sin host ni credenciales; contrato debajo | Cargada por L-03 para `visana_dev`; conexión desde Cloud Run no probada |
| `DB_USER` | Variable GitHub del mismo nombre; usuario PostgreSQL real | Cargada por L-04: `visana_app_dev`; ajuste SQL aplicado y verificado el 28 de septiembre |
| `DB_PASSWORD` | Credencial PostgreSQL rotada, consumida por Secret Manager en la revisión futura | No usar el literal expuesto ni copiarlo desde GitHub |
| `KEYCLOAK_ISSUER_URI` | Variable GitHub del mismo nombre; issuer HTTPS externo verificado | Obligatorio, actualmente pendiente |
| `GCP_PROJECT_ID` | Secret GitHub existente, usado como proyecto explícito | Existente; valor no reproducido |
| `GCP_CREDENTIALS` | Secret GitHub existente para la identidad que publica/despliega | Existente; clave no reproducida |
| `DEV_RUNTIME_SERVICE_ACCOUNT` | Cuenta actual comprobada `984938781030-compute@developer.gserviceaccount.com` | Cargada por L-05; Allow comprobado, evaluación total desconocida |
| `DEV_HEALTHCHECK_CLIENT_ID` | Variable GitHub de un cliente técnico OIDC autorizado para comprobar salud | Pendiente de identificar/proveer por responsable |
| `DEV_HEALTHCHECK_CLIENT_SECRET` | Secret GitHub de ese cliente; se usa solo para obtener un bearer efímero | Pendiente; no enviar por chat |
| `DEV_HEALTHCHECK_SUBJECT` | Sujeto firmado e inmutable del cliente técnico; permite reconocerlo aun sin scope/azp | Nuevo campo mínimo, pendiente del IdP |
| `DEV_HEALTHCHECK_AUDIENCE` | Audience dedicada para salud, sin imponerla globalmente a usuarios humanos | Nuevo campo mínimo, pendiente del IdP |
| `DEV_DB_PASSWORD_SECRET_REF` | Referencia fija `visana-dev-db-password:1`, versión habilitada comprobada | Cargada por L-06; valor no leído ni validado |
| `DEV_DB_CREDENTIAL_ROTATION_CONFIRMED` | `true` tras cierre documentado por el responsable de rotación/revocación PostgreSQL | Gate administrativo; no prueba revocación ni conexión; no probar la contraseña antigua |
| `CORS_ALLOWED_ORIGINS` | Variable GitHub con orígenes HTTPS DEV aprobados, separados por coma y sin paths/comodines | Dominio frontend pendiente; vacío deshabilita cross-origin en DEV |
| `DEV_FRONTEND_CLOUD_ENABLED` | Variable GitHub de activación explícita del frontend cloud, consumida solo por preflight | Vacío/false hasta aprobación; true exige CORS no vacío |

Contrato único de `DB_URL` (plantilla; **no cargar el placeholder**):

```text
jdbc:postgresql:///<BASE_APROBADA>?socketFactory=com.google.cloud.sql.postgres.SocketFactory&cloudSqlInstance=visana-erp-dev:us-central1:visana-db-dev&ipTypes=PUBLIC&cloudSqlRefreshStrategy=lazy&enableIamAuth=false
```

La base debe ser un identificador ASCII simple aprobado por el DBA; no elegir `postgres`. Guards Python y Java exigen exactamente esos cinco parámetros, sin duplicados, overrides, credenciales o factories alternativas. El conector recibe Google ADC de la identidad runtime y establece TLS autenticado. No se configuran opciones que desactiven TLS, archivos JSON, autenticación IAM PostgreSQL, VPC, NAT, proxy, Unix socket ni `--add-cloudsql-instances`. [Contrato oficial 1.30.0](https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory/blob/v1.30.0/docs/jdbc.md).

El CD no consume ya el secreto GitHub `DB_PASSWORD`: exige referencia Secret Manager fija y atestación de rotación, sin trasladar literales a archivos temporales. Conserva configuración ajena mediante merge y rechaza overrides de identidad/DataSource/Flyway/seguridad/JVM. Los diagnósticos capturan JSON en memoria y escriben solo campos permitidos; excluyen contraseñas, valores desconocidos, cuerpos HTTP y argumentos. Las excepciones no imprimen valores ni URLs.

Tras L-01–L-06 están cargadas `DB_URL`, `DB_USER`, `DEV_RUNTIME_SERVICE_ACCOUNT` y `DEV_DB_PASSWORD_SECRET_REF`. Faltan las entradas OIDC cloud y la confirmación administrativa de rotación; no establecerla en `true`. `build.yml` pasa tres secretos: proyecto, desplegador y cliente OIDC. No se selecciona ni crea un Environment. El workflow remoto de `dev` aún consume `secrets.DB_PASSWORD`: conservarlo; `qa` y `main` no ofrecieron un directorio de workflows consultable.

La [guía cloud](docs/04_architecture/CLOUD_ARCHITECTURE.md) identifica fuentes consultadas, permisos por recurso y decisiones externas. Digest, URL y bearer efímero se producen durante el run; no son secretos permanentes que deba cargar el operador.

## CI, ramas y CD

1. `build.yml` es la entrada para PR y push de `dev`, `qa` y `main`. Un PR prueba su commit de integración propuesto; no cambia el checkout a `dev`.
2. Invoca `openapi-contract.yml`, ahora reutilizable: actionlint, pruebas de guards, Maven verify, suites PostgreSQL obligatorias, generación OpenAPI con clases compiladas y comprobación de drift.
3. Produce el JAR ejecutable, SHA de origen y checksum; comprueba launcher, driver, Connector y Flyway coherente; construye una imagen Linux amd64. El artefacto tiene nombre `backend-<SHA>` y pertenece al mismo run.
4. Solo un **push integrado en dev**, después de verify y del gate de impacto, invoca `deploy-dev.yml`. El gate compara los árboles completos antes/después del push; incluye borrados, renombres y todos sus commits. Cambios exclusivamente documentales o en `infra/cost-dev-01/**` conservan CI y omiten CD. Rango desconocido/error Git bloquea; el reusable exige un booleano y vuelve a verificar push/dev. PR, ramas de corrección, `qa` y `main` no ejecutan CD DEV.
5. CD verifica el artefacto, consulta el servicio existente, confirma su cuenta de ejecución, publica y despliega por digest. La concurrencia del servicio evita despliegues simultáneos. Conserva la autenticación existente por clave; no agrega permisos OIDC ni configura WIF.
6. Comprueba la revisión específica, readiness, digest, perfil/configuración, cuenta de ejecución y tráfico hacia esa revisión (`PROCESS_READY`). Después exige HTTP 200, `status=UP` (`APPLICATION_UP`) y `components.db.status=UP` (`DATASOURCE_UP`) en `/actuator/health` con JWT técnico. La captura cloud del backend es pública; una futura privatización requeriría adaptar también la autenticación Google.

Las rutas que afectan artefacto/contrato/CD se enumeran en `scripts/ci/backend_changes.py`: Maven/wrapper, src, Docker, OpenAPI, scripts CI, workflows usados y preparación Keycloak que consume la verificación. El gate no sustituye el bloqueo compartido deploy/STOP de COST-DEV, que sigue pendiente. `concurrency: deploy-visana-api-dev` serializa únicamente deployments GitHub.

En DEV se transporta CORS explícitamente hasta la revisión y se comprueba su valor efectivo. Preflight rechaza localhost, comodines, HTTP, credenciales, paths/query/fragment y overrides de Spring. Sin frontend aprobado la lista queda vacía. El perfil local conserva `http://localhost:4200`; no se inventa un dominio Firebase.

Trabajar en una rama de corrección y abrir PR hacia `dev`; integrar únicamente con aprobación. Las identidades de checks cambian por la llamada reutilizable; el responsable debe verificar cualquier regla de protección configurada antes de integrar. No se modifican protecciones desde esta tarea.

## Imagen y despliegue existente

El Dockerfile empaqueta únicamente `.ci-artifact/app.jar`, previamente validado. No recompila con `skipTests` ni usa el `target` local. `.dockerignore` permite solo Dockerfile y ese JAR; excluye fuente, evidencia, credenciales, `.git`, `.env`, archivos de autenticación y respaldos. Para una construcción local se necesita primero generar y verificar un JAR limpio en una salida aislada, colocarlo en `.ci-artifact/app.jar` y ejecutar `docker build --platform linux/amd64 -t visana-local .` con Docker disponible. CI automatiza esa preparación.

Destino comprobado: proyecto `visana-erp-dev`, servicio `visana-api-dev`, región `us-central1`, Artifact Registry `visana-repo` y runtime compute indicado arriba. El workflow exige que el servicio exista antes de desplegar y conserva IAM/exposición pública, redes y conectividad. Eliminó el flag que imponía `--allow-unauthenticated`.

La instancia comprobada `visana-db-dev` **no es el nombre de la BD PostgreSQL**. La sesión SQL autorizada confirmó `visana_dev` y `visana_app_dev`; no se utiliza `postgres` como aplicación. El Connector resuelve `visana-erp-dev:us-central1:visana-db-dev` y selecciona PUBLIC; Hikari y Flyway comparten URL y credenciales. La dependencia y módulos de migración se comprueban dentro del JAR final. La autenticación de Studio no acredita conexión desde Cloud Run mediante Connector.

## Identidades y permisos

- Identidad GitHub: lee/publica Git/PR; no equivale a una identidad cloud.
- Cuenta de despliegue de `GCP_CREDENTIALS`: debe disponer de los permisos existentes para Artifact Registry y Cloud Run y `iam.serviceAccounts.actAs` sobre la cuenta runtime; para invocación privada, `run.routes.invoke`. Revisar concesiones concretas sin ampliarlas automáticamente.
- Cuenta runtime: debe coincidir con el servicio consultado. Secret Manager requiere `secretmanager.versions.access` sobre el secreto referenciado. `cloudsql.instances.connect` corresponde si la estrategia aprobada emplea integración Cloud SQL/proxy/connector; no crea conectividad VPC ni autentica al usuario PostgreSQL.
- Usuario PostgreSQL: autentica a BD y requiere los permisos de aplicación/Flyway aprobados, independientes de IAM.
- Cliente de salud OIDC: `visana-dev-health` preparado y probado con un Keycloak real aislado; no provisionado en cloud. Usa `client_credentials`, scope `visana.health` y audience exclusiva. El checker cloud exige discovery HTTPS y bearer efímero en `Authorization`. No exige token Google porque el backend observado tiene allUsers; privatizarlo exige una decisión y adaptación separadas.

El sujeto, client ID, audience o scope técnico firmados reconocen la identidad restringida. El contrato exige sujeto exacto, `azp` exacto, `client_id` consistente si aparece, audience exclusivamente técnica y scope `visana.health`. Claims incompletos/inconsistentes fallan cerrados. Solo GET exacto `/actuator/health` está permitido; negocio, docs, otros Actuator y métodos quedan rechazados incluso con roles administrativos. Se conserva la validación de firma/issuer/vigencia y las políticas previas de usuarios humanos; no se impone una audience global. El IdP debe reservar estos marcadores y mantener el sujeto estable. Las pruebas RSA/JWKS son locales. El probe real es TCP 8080 y no requiere JWT; una probe HTTP sin token hacia salud protegida sería incompatible.

## Diagnóstico, verificación y reversión

La guía [CLOUD_ARCHITECTURE](docs/04_architecture/CLOUD_ARCHITECTURE.md) registra evidencia y pendientes del primer despliegue. Consultar primero run/SHA/digest y **la revisión fallida**, no asumir que la última revisión lista es aquella. Con acceso de lectura y proyecto verificado:

```text
gcloud run revisions describe REVISION --project=PROJECT_ID --region=REGION --format='yaml(metadata.name,status.conditions,status.imageDigest)'
gcloud logging read 'resource.type="cloud_run_revision" AND resource.labels.service_name="visana-api-dev" AND resource.labels.revision_name="REVISION"' --project=PROJECT_ID --freshness=7d --limit=100
```

Los logs de aplicación pueden contener información sensible: revisarlos en un entorno privado y sanitizarlos antes de compartir. Buscar la cadena causal de DataSource/Flyway/OIDC/beans antes de atribuir el error al puerto. No imprimir configuración completa, credenciales ni valores de variables.

Después de integrar: revisar CI/CD del **nuevo SHA**, revisión Ready, digest esperado y salud autenticada. En DEV los componentes se muestran solo a `SCOPE_visana.health`, conservando `show-details=never`; se exige db UP además de UP agregado. No demuestra aprobación de reglas, funcionamiento financiero completo ni integridad de todos los datos.

Reversión de aplicación: preparar un revert de los commits propios en otra rama y PR hacia `dev`; revisar compatibilidad de esquema antes de integrar y dejar que el mismo CI/CD publique la aplicación. Una reversión operativa de tráfico a una revisión anterior exige autorización y una revisión previamente validada. **Revertir imagen/código/tráfico no revierte el esquema ni los datos**. No ejecutar down migrations ni `Flyway repair` como parte de esta corrección.

El arranque conserva Flyway y puede aplicar las migraciones existentes al ambiente configurado. No se añadieron ni ejecutaron migraciones contra BD real en esta tarea; revisar permisos y compatibilidad del esquema antes de autorizar la integración/despliegue.

## Limitaciones actuales

L-01–L-06 y el ajuste SQL están aplicados según las evidencias del 28 de septiembre; la evaluación total IAM conserva la limitación de deny. Cristian no acreditó usuario/rotación de la credencial expuesta ni correspondencia del payload versión 1 con `visana_app_dev`; la incidencia permanece abierta. No existe todavía issuer/cliente técnico cloud. Desplegador y soporte Spring conservan decisiones pendientes; soporte/migración se tratan fuera de este cierre, sin una migración mayor ni un nuevo bloqueo de DEV basado solo en esa decisión. Las pruebas aisladas no acreditan conexión backend–Cloud SQL real. PR #21/COST-DEV y A-06B/V9 siguen separados; PR #20 solo lleva V1–V8.

## Preparación de Keycloak DEV

Archivos ejecutables en `infra/keycloak`, `compose.keycloak-test.yml` y `scripts/ci/keycloak_dev.py`. El contexto Docker excluye exports con usuarios y credenciales; los secretos se resuelven al arrancar. El realm existente `visana-erp` se importa solo si no existe. `visana-backend` permanece deshabilitado en esta preparación hasta aprobar redirects y flujos humanos; el cliente confidencial `visana-dev-health` está separado. HealthClientPolicy del backend permanece intacta.

```powershell
python -B scripts/ci/keycloak_dev.py validate
# Solo contenedores propios aislados y credenciales sintéticas; salida fuera de Git:
python -B scripts/ci/keycloak_dev.py test --output "$env:TEMP\visana-keycloak-proof"
python -B scripts/ci/check_sql_privileges_proposal.py
```

La prueba necesita Docker Linux y JDK 21/Maven Wrapper en Linux; en Windows ejecuta la prueba Java enfocada en un contenedor Maven/JDK 21 con fuente montada en lectura. CI incorpora estas pruebas al job existente, sin nuevo CD. El render `python -B scripts/ci/keycloak_dev.py render --hostname HTTPS_ORIGIN --image IMMUTABLE_ARTIFACT_REGISTRY_DIGEST --output service.json` exige valores aprobados y solo escribe un manifiesto; no despliega. Ver [lote, límites operativos y fuentes oficiales](docs/04_architecture/CLOUD_ARCHITECTURE.md#lote-keycloak-dev-propuesto-no-aplicado).

Fuentes técnicas: [contrato del contenedor Cloud Run](https://docs.cloud.google.com/run/docs/container-contract), [inputs de deploy-cloudrun v2](https://raw.githubusercontent.com/google-github-actions/deploy-cloudrun/v2/action.yml), [workflows reutilizables](https://docs.github.com/en/actions/how-tos/reuse-automations/reuse-workflows), [autenticación entre servicios Cloud Run](https://docs.cloud.google.com/run/docs/authenticating/service-to-service), [endpoints y client credentials de Keycloak](https://www.keycloak.org/securing-apps/oidc-layers).
