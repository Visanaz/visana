# VISANA Core API

Backend existente de VISANA: Java/Spring Boot, persistencia y migraciones, módulos de comercio, identidad, red/genealogía, calificación/volumen, compensación y ledger. La existencia de un módulo no demuestra aprobación de sus reglas ni funcionamiento productivo completo. El contrato revisado está en `openapi/visana-api-v1.json`. El frontend pertenece a otro repositorio.

## Estado

- **IMPLEMENTADO:** CI de backend/contrato y empaquetado; CD DEV condicionado al éxito de esa verificación y a configuración externa explícita.
- **VERIFICADO HISTÓRICAMENTE:** el CI del SHA `040ec3f929c15e546571f48fa425736dd83c3b36` pasó el 25 de septiembre de 2026. La imagen fue publicada; el despliegue falló al crear la revisión `visana-api-dev-00001-dwp`.
- **VERIFICADO EN PR:** [CI 36277138752](https://github.com/Visanaz/visana/actions/runs/36277138752) asociado al head `72bebe3f4b3110a3895b3bf37b89a327d56683d7`, ejecutado sobre su merge temporal: 153 pruebas sin fallos, errores ni omisiones, PostgreSQL aislado, contrato y Docker Linux amd64. CD omitido por tratarse de PR.
- **CAPTURA CLOUD:** proyecto `visana-erp-dev`, Cloud Run/Artifact Registry/Cloud SQL consultados. Los logs históricos confirmaron URL DataSource inválida y el desajuste adicional 8084/8080. El servicio tiene ingress All y binding allUsers; no exige actualmente token Google.
- **CORRECCIÓN VALIDADA:** [CI 36291639215](https://github.com/Visanaz/visana/actions/runs/36291639215), head `fbc570d4a7b91f519326bb44dae363efe640f5fe`: 161 pruebas Java sin fallos/errores/omisiones, diez Python, seis suites PostgreSQL 18.3, mantenimiento JDBC 42.7.13, migraciones, salud y JWT/JWKS aislados. CD omitido. [PR #20](https://github.com/Visanaz/visana/pull/20) sigue Draft; no se desplegó ni verificó conexión Cloud SQL real.
- **RECONCILIACIÓN 2026-09-28:** confirmados `visana_dev`, usuario integrado `visana_app_dev` y secreto `visana-dev-db-password`, versión `1` habilitada, sin leer su valor. Runtime permanece en la cuenta compute; los roles SQL Client y Secret Accessor fueron observados en el agente de plataforma, no en runtime. [Lote pendiente de aprobación y consultas SQL](docs/04_architecture/CLOUD_ARCHITECTURE.md#reconciliación-manual-2026-09-28).

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
| `DB_URL` | URL JDBC del conector oficial, sin host ni credenciales; contrato debajo | `visana_dev` comprobada; carga GitHub y privilegios pendientes |
| `DB_USER` | Variable GitHub del mismo nombre; usuario PostgreSQL real | `visana_app_dev` comprobado; carga GitHub y privilegios pendientes |
| `DB_PASSWORD` | Credencial PostgreSQL rotada, consumida por Secret Manager en la revisión futura | No usar el literal expuesto ni copiarlo desde GitHub |
| `KEYCLOAK_ISSUER_URI` | Variable GitHub del mismo nombre; issuer HTTPS externo verificado | Obligatorio, actualmente pendiente |
| `GCP_PROJECT_ID` | Secret GitHub existente, usado como proyecto explícito | Existente; valor no reproducido |
| `GCP_CREDENTIALS` | Secret GitHub existente para la identidad que publica/despliega | Existente; clave no reproducida |
| `DEV_RUNTIME_SERVICE_ACCOUNT` | Cuenta actual comprobada `984938781030-compute@developer.gserviceaccount.com` | Carga y permisos efectivos pendientes |
| `DEV_HEALTHCHECK_CLIENT_ID` | Variable GitHub de un cliente técnico OIDC autorizado para comprobar salud | Pendiente de identificar/proveer por responsable |
| `DEV_HEALTHCHECK_CLIENT_SECRET` | Secret GitHub de ese cliente; se usa solo para obtener un bearer efímero | Pendiente; no enviar por chat |
| `DEV_HEALTHCHECK_SUBJECT` | Sujeto firmado e inmutable del cliente técnico; permite reconocerlo aun sin scope/azp | Nuevo campo mínimo, pendiente del IdP |
| `DEV_HEALTHCHECK_AUDIENCE` | Audience dedicada para salud, sin imponerla globalmente a usuarios humanos | Nuevo campo mínimo, pendiente del IdP |
| `DEV_DB_PASSWORD_SECRET_REF` | Referencia fija `visana-dev-db-password:1`, versión habilitada comprobada | Carga pendiente de aprobación; el valor no fue leído ni validado |
| `DEV_DB_CREDENTIAL_ROTATION_CONFIRMED` | `true` tras cierre documentado por el responsable de rotación/revocación PostgreSQL | Gate administrativo; no prueba revocación ni conexión; no probar la contraseña antigua |

Contrato único de `DB_URL` (plantilla; **no cargar el placeholder**):

```text
jdbc:postgresql:///<BASE_APROBADA>?socketFactory=com.google.cloud.sql.postgres.SocketFactory&cloudSqlInstance=visana-erp-dev:us-central1:visana-db-dev&ipTypes=PUBLIC&cloudSqlRefreshStrategy=lazy&enableIamAuth=false
```

La base debe ser un identificador ASCII simple aprobado por el DBA; no elegir `postgres`. Guards Python y Java exigen exactamente esos cinco parámetros, sin duplicados, overrides, credenciales o factories alternativas. El conector recibe Google ADC de la identidad runtime y establece TLS autenticado. No se configuran opciones que desactiven TLS, archivos JSON, autenticación IAM PostgreSQL, VPC, NAT, proxy, Unix socket ni `--add-cloudsql-instances`. [Contrato oficial 1.30.0](https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory/blob/v1.30.0/docs/jdbc.md).

El CD no consume ya el secreto GitHub `DB_PASSWORD`: exige referencia Secret Manager fija y atestación de rotación, sin trasladar literales a archivos temporales. Conserva configuración ajena mediante merge y rechaza overrides de identidad/DataSource/Flyway/seguridad/JVM. Los diagnósticos capturan JSON en memoria y escriben solo campos permitidos; excluyen contraseñas, valores desconocidos, cuerpos HTTP y argumentos. Las excepciones no imprimen valores ni URLs.

Consulta del 2026-09-28: cero variables de repositorio y secretos `GCP_PROJECT_ID`, `GCP_CREDENTIALS`, `DB_PASSWORD`; ninguna carga externa se realizó desde esta tarea. Las nueve variables del contrato y `DEV_HEALTHCHECK_CLIENT_SECRET` siguen pendientes. `build.yml` pasa tres secretos: proyecto, desplegador y cliente OIDC. No se selecciona ni crea un Environment. El workflow remoto de `dev` aún consume `secrets.DB_PASSWORD`: conservarlo; `qa` y `main` no ofrecieron un directorio de workflows consultable.

La [guía cloud](docs/04_architecture/CLOUD_ARCHITECTURE.md) identifica fuentes consultadas, permisos por recurso y decisiones externas. Digest, URL y bearer efímero se producen durante el run; no son secretos permanentes que deba cargar el operador.

## CI, ramas y CD

1. `build.yml` es la entrada para PR y push de `dev`, `qa` y `main`. Un PR prueba su commit de integración propuesto; no cambia el checkout a `dev`.
2. Invoca `openapi-contract.yml`, ahora reutilizable: actionlint, pruebas de guards, Maven verify, suites PostgreSQL obligatorias, generación OpenAPI con clases compiladas y comprobación de drift.
3. Produce el JAR ejecutable, SHA de origen y checksum; comprueba launcher, driver, Connector y Flyway coherente; construye una imagen Linux amd64. El artefacto tiene nombre `backend-<SHA>` y pertenece al mismo run.
4. Solo un **push integrado en dev**, después de `needs: verify`, invoca el workflow reutilizable `deploy-dev.yml`. PR, ramas de corrección, `qa` y `main` no ejecutan CD DEV. No hay trigger manual ni `pull_request_target`.
5. CD verifica el artefacto, consulta el servicio existente, confirma su cuenta de ejecución, publica y despliega por digest. La concurrencia del servicio evita despliegues simultáneos. Conserva la autenticación existente por clave; no agrega permisos OIDC ni configura WIF.
6. Comprueba la revisión específica, readiness, digest, perfil/configuración, cuenta de ejecución y tráfico hacia esa revisión. Después exige HTTP 200 y JSON `status=UP` en `/actuator/health` con autenticación de Spring y Cloud Run separadas.

Trabajar en una rama de corrección y abrir PR hacia `dev`; integrar únicamente con aprobación. Las identidades de checks cambian por la llamada reutilizable; el responsable debe verificar cualquier regla de protección configurada antes de integrar. No se modifican protecciones desde esta tarea.

## Imagen y despliegue existente

El Dockerfile empaqueta únicamente `.ci-artifact/app.jar`, previamente validado. No recompila con `skipTests` ni usa el `target` local. `.dockerignore` permite solo Dockerfile y ese JAR; excluye fuente, evidencia, credenciales, `.git`, `.env`, archivos de autenticación y respaldos. Para una construcción local se necesita primero generar y verificar un JAR limpio en una salida aislada, colocarlo en `.ci-artifact/app.jar` y ejecutar `docker build --platform linux/amd64 -t visana-local .` con Docker disponible. CI automatiza esa preparación.

Destino comprobado en workflow/logs: servicio `visana-api-dev`, región `us-central1`, Artifact Registry `visana-repo`. Proyecto real y cuenta runtime aún requieren consulta de metadata; no se deducen del repositorio. El workflow exige que el servicio exista antes de desplegar y conserva IAM/exposición pública, redes y conectividad. Eliminó el flag que imponía `--allow-unauthenticated`.

La instancia comprobada `visana-db-dev` **no es el nombre de la BD PostgreSQL**. Solo se observaron base y usuario `postgres`; no se seleccionan para VISANA. El Connector resuelve `visana-erp-dev:us-central1:visana-db-dev` y selecciona PUBLIC; Hikari y Flyway comparten URL y credenciales. La dependencia y módulos de migración se comprueban dentro del JAR final. Esto no acredita conexión real.

## Identidades y permisos

- Identidad GitHub: lee/publica Git/PR; no equivale a una identidad cloud.
- Cuenta de despliegue de `GCP_CREDENTIALS`: debe disponer de los permisos existentes para Artifact Registry y Cloud Run y `iam.serviceAccounts.actAs` sobre la cuenta runtime; para invocación privada, `run.routes.invoke`. Revisar concesiones concretas sin ampliarlas automáticamente.
- Cuenta runtime: debe coincidir con el servicio consultado. Secret Manager requiere `secretmanager.versions.access` sobre el secreto referenciado. `cloudsql.instances.connect` corresponde si la estrategia aprobada emplea integración Cloud SQL/proxy/connector; no crea conectividad VPC ni autentica al usuario PostgreSQL.
- Usuario PostgreSQL: autentica a BD y requiere los permisos de aplicación/Flyway aprobados, independientes de IAM.
- Cliente de salud OIDC: contrato propuesto, no provisionado ni validado en IdP real. Se obtiene bearer por `client_credentials`/discovery HTTPS y scope propuesto `visana.health`; no se persiste. `Authorization` lleva el JWT. No se exige token Google en el checker porque el servicio observado tiene allUsers; privatizarlo exige una decisión y adaptación separadas.

El sujeto, client ID, audience o scope técnico firmados reconocen la identidad restringida. El contrato exige sujeto exacto, `azp` exacto, `client_id` consistente si aparece, audience exclusivamente técnica y scope `visana.health`. Claims incompletos/inconsistentes fallan cerrados. Solo GET exacto `/actuator/health` está permitido; negocio, docs, otros Actuator y métodos quedan rechazados incluso con roles administrativos. Se conserva la validación de firma/issuer/vigencia y las políticas previas de usuarios humanos; no se impone una audience global. El IdP debe reservar estos marcadores y mantener el sujeto estable. Las pruebas RSA/JWKS son locales. El probe real es TCP 8080 y no requiere JWT; una probe HTTP sin token hacia salud protegida sería incompatible.

## Diagnóstico, verificación y reversión

La guía [CLOUD_ARCHITECTURE](docs/04_architecture/CLOUD_ARCHITECTURE.md) registra evidencia y pendientes del primer despliegue. Consultar primero run/SHA/digest y **la revisión fallida**, no asumir que la última revisión lista es aquella. Con acceso de lectura y proyecto verificado:

```text
gcloud run revisions describe REVISION --project=PROJECT_ID --region=REGION --format='yaml(metadata.name,status.conditions,status.imageDigest)'
gcloud logging read 'resource.type="cloud_run_revision" AND resource.labels.service_name="visana-api-dev" AND resource.labels.revision_name="REVISION"' --project=PROJECT_ID --freshness=7d --limit=100
```

Los logs de aplicación pueden contener información sensible: revisarlos en un entorno privado y sanitizarlos antes de compartir. Buscar la cadena causal de DataSource/Flyway/OIDC/beans antes de atribuir el error al puerto. No imprimir configuración completa, credenciales ni valores de variables.

Después de integrar: revisar CI/CD del **nuevo SHA**, revisión Ready, digest esperado y salud autenticada. El `UP` agregado usa el indicador DataSource estándar; no demuestra aprobación de reglas, funcionamiento financiero completo ni integridad de todos los datos.

Reversión de aplicación: preparar un revert de los commits propios en otra rama y PR hacia `dev`; revisar compatibilidad de esquema antes de integrar y dejar que el mismo CI/CD publique la aplicación. Una reversión operativa de tráfico a una revisión anterior exige autorización y una revisión previamente validada. **Revertir imagen/código/tráfico no revierte el esquema ni los datos**. No ejecutar down migrations ni `Flyway repair` como parte de esta corrección.

El arranque conserva Flyway y puede aplicar las migraciones existentes al ambiente configurado. No se añadieron ni ejecutaron migraciones contra BD real en esta tarea; revisar permisos y compatibilidad del esquema antes de autorizar la integración/despliegue.

## Limitaciones actuales

La base, el usuario y la versión del secreto están comprobados; faltan atributos SQL, esquema/privilegios, concesiones a runtime, issuer/contrato técnico cloud y cierre administrativo de la credencial anteriormente expuesta. SQL Studio exige autenticación y no hay sesión SQL autorizada disponible. El diagnóstico IAM no encuentra políticas de permiso para runtime en SQL/secretos; las políticas de denegación no son visibles al operador. No se aplicaron cargas, IAM, recursos, merge ni despliegue desde esta tarea. Crear otro usuario no acredita revocación de la credencial anterior. Las pruebas aisladas no acreditan conexión Cloud SQL real. Véase el lote concreto en la guía cloud.

Fuentes técnicas: [contrato del contenedor Cloud Run](https://docs.cloud.google.com/run/docs/container-contract), [inputs de deploy-cloudrun v2](https://raw.githubusercontent.com/google-github-actions/deploy-cloudrun/v2/action.yml), [workflows reutilizables](https://docs.github.com/en/actions/how-tos/reuse-automations/reuse-workflows), [autenticación entre servicios Cloud Run](https://docs.cloud.google.com/run/docs/authenticating/service-to-service), [endpoints y client credentials de Keycloak](https://www.keycloak.org/securing-apps/oidc-layers).
