# VISANA Core API

Backend existente de VISANA: Java/Spring Boot, persistencia y migraciones, módulos de comercio, identidad, red/genealogía, calificación/volumen, compensación y ledger. La existencia de un módulo no demuestra aprobación de sus reglas ni funcionamiento productivo completo. El contrato revisado está en `openapi/visana-api-v1.json`. El frontend pertenece a otro repositorio.

## Estado

- **IMPLEMENTADO:** CI de backend/contrato y empaquetado; CD DEV condicionado al éxito de esa verificación y a configuración externa explícita.
- **VERIFICADO HISTÓRICAMENTE:** el CI del SHA `040ec3f929c15e546571f48fa425736dd83c3b36` pasó el 25 de septiembre de 2026. La imagen fue publicada; el despliegue falló al crear la revisión `visana-api-dev-00001-dwp`.
- **VERIFICADO EN PR:** [CI 36277138752](https://github.com/Visanaz/visana/actions/runs/36277138752) asociado al head `72bebe3f4b3110a3895b3bf37b89a327d56683d7`, ejecutado sobre su merge temporal: 153 pruebas sin fallos, errores ni omisiones, PostgreSQL aislado, contrato y Docker Linux amd64. CD omitido por tratarse de PR.
- **PENDIENTE:** configuración efectiva cloud y validación del CD. [PR #20](https://github.com/Visanaz/visana/pull/20) permanece Draft; comprobar sus checks si cambia el SHA. La ausencia de logs antiguos limita el diagnóstico y no bloquea por sí sola un nuevo despliegue aprobado.

## Stack y requisitos

Versiones del repositorio: Java 21, Spring Boot 3.4.0, Maven Wrapper 3.3.2 con Maven 3.9.9, PostgreSQL JDBC y Flyway administrados por Spring Boot, Keycloak Admin Client 26.0.0 y Springdoc 2.7.0. El driver y las migraciones MySQL se conservan para el contexto legacy; no ejecutar contra evidencia histórica.

Desarrollo: JDK 21, Maven Wrapper y configuración externa del entorno. Docker con motor Linux es necesario para las cinco suites PostgreSQL con Testcontainers y para construir la imagen. Los archivos `.env` no son cargados automáticamente por Maven/Spring: exportar las variables al proceso sin publicarlas.

## Desarrollo y pruebas

El proyecto permite aislar la salida Maven. Es obligatorio hacerlo si existen cambios ajenos o artefactos históricos pendientes bajo `target/`:

```powershell
# Con JAVA_HOME apuntando a un JDK 21 y su bin en el PATH de este proceso:
.\mvnw.cmd --batch-mode "-Dvisana.build.directory=$env:TEMP\visana-build" verify
.\mvnw.cmd --batch-mode "-Dvisana.build.directory=$env:TEMP\visana-build" "-Dtest=ServerPortConfigurationTest" test
python -B -m unittest discover -s scripts/ci -p 'test_*.py'
```

En CI se usa `./mvnw --batch-mode clean verify` exclusivamente en un runner independiente. CI exige Docker y comprueba que las suites PostgreSQL ejecutaron pruebas sin saltos; la ausencia de Docker local puede producir pruebas omitidas y no equivale a validación PostgreSQL.

Verificación local de esta corrección (2026-09-26): Maven verify con Microsoft OpenJDK 21.0.12 y salida externa, 153 pruebas, cero errores/fallos, seis omitidas por Docker ausente. Las tres pruebas de puerto pasaron; también seis pruebas de guards y actionlint 1.7.7. La construcción Docker y PostgreSQL se verifican por separado en el CI del PR, sin sustituir este límite local.

Arranque de desarrollo, después de configurar una BD aislada y un issuer real accesible (requiere esas dependencias; no se acredita aquí su arranque):

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
| `DB_URL` | Variable GitHub del mismo nombre; URL JDBC PostgreSQL TCP, BD real y ruta de red verificadas por operador | Obligatorio, actualmente pendiente |
| `DB_USER` | Variable GitHub del mismo nombre; usuario PostgreSQL real | Obligatorio, actualmente pendiente |
| `DB_PASSWORD` | Secret GitHub existente; si el servicio ya utiliza Secret Manager, se conserva su referencia y se omite el valor literal | Obligatorio según origen existente |
| `KEYCLOAK_ISSUER_URI` | Variable GitHub del mismo nombre; issuer HTTPS externo verificado | Obligatorio, actualmente pendiente |
| `GCP_PROJECT_ID` | Secret GitHub existente, usado como proyecto explícito | Existente; valor no reproducido |
| `GCP_CREDENTIALS` | Secret GitHub existente para la identidad que publica/despliega | Existente; clave no reproducida |
| `DEV_RUNTIME_SERVICE_ACCOUNT` | Variable GitHub con la cuenta **actual** de ejecución, no la cuenta de despliegue ni el usuario de BD | Pendiente de metadata real |
| `DEV_HEALTHCHECK_CLIENT_ID` | Variable GitHub de un cliente técnico OIDC autorizado para comprobar salud | Pendiente de identificar/proveer por responsable |
| `DEV_HEALTHCHECK_CLIENT_SECRET` | Secret GitHub de ese cliente; se usa solo para obtener un bearer efímero | Pendiente; no enviar por chat |

El preflight falla sin mostrar valores si faltan fuentes externas. Rechaza URLs localhost, credenciales en JDBC, cambios de identidad y overrides de DataSource/Flyway/servidor o argumentos que requieren revisión. Las variables y secretos existentes ajenos se conservan mediante `merge`. Una referencia existente a Secret Manager no se reemplaza por contraseña literal. La migración de una contraseña literal a Secret Manager requiere recurso, versión y permisos verificados fuera de esta tarea.

Consulta de GitHub del 26 de septiembre de 2026: **cero variables de repositorio, cero Environments y solo tres secretos** (`GCP_PROJECT_ID`, `GCP_CREDENTIALS`, `DB_PASSWORD`, metadata de actualización del 25 de septiembre). Están ausentes las cinco variables de la tabla y `DEV_HEALTHCHECK_CLIENT_SECRET`; la existencia de un secreto no verifica su contenido ni sus permisos. Los workflows no declaran `environment:`: la carga propuesta corresponde a **Settings → Secrets and variables → Actions → Repository**, con los cuatro secretos pasados explícitamente desde `build.yml`. No crear un Environment llamado `dev` por suposición.

La [matriz de cierre DEV](docs/04_architecture/CLOUD_ARCHITECTURE.md#cierre-de-configuración-dev--26-de-septiembre-de-2026) identifica consumidores/líneas, etapas A/B/C, fuentes consultadas, permisos por recurso y acciones concretas para aprobación. Digest, URL y tokens efímeros se producen durante el run; no son variables ni secretos permanentes que deba cargar Cristian.

## CI, ramas y CD

1. `build.yml` es la entrada para PR y push de `dev`, `qa` y `main`. Un PR prueba su commit de integración propuesto; no cambia el checkout a `dev`.
2. Invoca `openapi-contract.yml`, ahora reutilizable: actionlint, pruebas de guards, Maven verify, suites PostgreSQL obligatorias, generación OpenAPI con clases compiladas y comprobación de drift.
3. Produce el JAR ejecutable, SHA de origen y checksum; comprueba launcher/driver PostgreSQL y construye una imagen Linux amd64. El artefacto tiene nombre `backend-<SHA>` y pertenece al mismo run.
4. Solo un **push integrado en dev**, después de `needs: verify`, invoca el workflow reutilizable `deploy-dev.yml`. PR, ramas de corrección, `qa` y `main` no ejecutan CD DEV. No hay trigger manual ni `pull_request_target`.
5. CD verifica el artefacto, consulta el servicio existente, confirma su cuenta de ejecución, publica y despliega por digest. La concurrencia del servicio evita despliegues simultáneos. Conserva la autenticación existente por clave; no agrega permisos OIDC ni configura WIF.
6. Comprueba la revisión específica, readiness, digest, perfil/configuración, cuenta de ejecución y tráfico hacia esa revisión. Después exige HTTP 200 y JSON `status=UP` en `/actuator/health` con autenticación de Spring y Cloud Run separadas.

Trabajar en una rama de corrección y abrir PR hacia `dev`; integrar únicamente con aprobación. Las identidades de checks cambian por la llamada reutilizable; el responsable debe verificar cualquier regla de protección configurada antes de integrar. No se modifican protecciones desde esta tarea.

## Imagen y despliegue existente

El Dockerfile empaqueta únicamente `.ci-artifact/app.jar`, previamente validado. No recompila con `skipTests` ni usa el `target` local. `.dockerignore` permite solo Dockerfile y ese JAR; excluye fuente, evidencia, credenciales, `.git`, `.env`, archivos de autenticación y respaldos. Para una construcción local se necesita primero generar y verificar un JAR limpio en una salida aislada, colocarlo en `.ci-artifact/app.jar` y ejecutar `docker build --platform linux/amd64 -t visana-local .` con Docker disponible. CI automatiza esa preparación.

Destino comprobado en workflow/logs: servicio `visana-api-dev`, región `us-central1`, Artifact Registry `visana-repo`. Proyecto real y cuenta runtime aún requieren consulta de metadata; no se deducen del repositorio. El workflow exige que el servicio exista antes de desplegar y conserva IAM/exposición pública, redes y conectividad. Eliminó el flag que imponía `--allow-unauthenticated`.

La instancia reportada `visana-db-dev` **no es el nombre de la BD PostgreSQL**. Instancia, connectionName, BD, usuario y región de Cloud SQL deben verificarse por separado. El código utiliza JDBC TCP con driver PostgreSQL; no incluye Java Connector ni proxy implementado. La viabilidad de TCP/VPC/TLS queda pendiente de infraestructura real. No se añadió `postgres-socket-factory`, `--add-cloudsql-instances` ni autenticación IAM de BD por hipótesis.

## Identidades y permisos

- Identidad GitHub: lee/publica Git/PR; no equivale a una identidad cloud.
- Cuenta de despliegue de `GCP_CREDENTIALS`: debe disponer de los permisos existentes para Artifact Registry y Cloud Run y `iam.serviceAccounts.actAs` sobre la cuenta runtime; para invocación privada, `run.routes.invoke`. Revisar concesiones concretas sin ampliarlas automáticamente.
- Cuenta runtime: debe coincidir con el servicio consultado. Secret Manager requiere `secretmanager.versions.access` sobre el secreto referenciado. `cloudsql.instances.connect` corresponde si la estrategia aprobada emplea integración Cloud SQL/proxy/connector; no crea conectividad VPC ni autentica al usuario PostgreSQL.
- Usuario PostgreSQL: autentica a BD y requiere los permisos de aplicación/Flyway aprobados, independientes de IAM.
- Cliente de salud OIDC: principal técnico autorizado; la tarea no crea clientes ni cambia realm. `/actuator/health` continúa protegido. Se obtiene un token por `client_credentials` usando discovery HTTPS; no se persisten tokens de aplicación. `X-Serverless-Authorization` lleva la identidad Google y `Authorization` el bearer OIDC. Un 401 no pasa la validación.

El endpoint de salud no exige rol de negocio; tampoco implementa un permiso exclusivo de salud ni validación explícita de audience del JWT de aplicación. Usar un cliente técnico existente si satisface el contrato; crear uno requiere decisión del responsable. La audience del ID token Google es la URL del servicio, sin `/actuator/health`. Las probes de plataforma son distintas: una probe HTTP sin bearer válido hacia este endpoint protegido falla; consultar las probes heredadas antes de aprobar. El runner público necesita alcanzar el issuer y la URL directa del servicio: IAM correcto no elimina las restricciones de ingress. No cambiar ingress ni exposición para facilitar la prueba.

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

No se dispone aquí de gcloud; Google Cloud Console pidió iniciar sesión. El usuario indicó continuar con acceso cloud pendiente. No se consultaron recursos cloud autenticados ni se recibió un 403 cloud: su existencia/configuración actual sigue **sin acceso**, no ausente. El proyecto aparece enmascarado en Actions; los logs de la revisión, usuario/BD/conectividad Cloud SQL, IAM, ingress, probes y cuenta runtime permanecen pendientes. GitHub sí fue consultado y sus faltantes son concretos. La protección de `dev` devolvió 404 y rulesets 403 con limitación de plan; no se confirmó un gate obligatorio ni se cambió. El PR permanece Draft. La alineación del puerto corrige un defecto demostrado en archivos; no confirma por sí sola la causa histórica.

Fuentes técnicas: [contrato del contenedor Cloud Run](https://docs.cloud.google.com/run/docs/container-contract), [inputs de deploy-cloudrun v2](https://raw.githubusercontent.com/google-github-actions/deploy-cloudrun/v2/action.yml), [workflows reutilizables](https://docs.github.com/en/actions/how-tos/reuse-automations/reuse-workflows), [autenticación entre servicios Cloud Run](https://docs.cloud.google.com/run/docs/authenticating/service-to-service), [endpoints y client credentials de Keycloak](https://www.keycloak.org/securing-apps/oidc-layers).
