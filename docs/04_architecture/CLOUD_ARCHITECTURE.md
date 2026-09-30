# Configuración cloud DEV — VISANA

Corrección del mismo [PR #20](https://github.com/Visanaz/visana/pull/20), Draft. Fase 1 del objetivo maestro: reconciliación, cambios de código/configuración y pruebas/publicación del PR. Guía de uso: [README](../../README.md).

## Reconciliación vigente — 2026-09-30

- L-01–L-06 ya aplicados: runtime SQL Client en proyecto y Secret Accessor limitado al secreto; cuatro variables GitHub cargadas. Se comprobó su presencia por nombre en esta revisión, sin leer secretos. La evaluación IAM sigue limitada por visibilidad de deny.
- Base `visana_dev`, usuario `visana_app_dev`, Secret Manager `visana-dev-db-password:1` habilitado y PostgreSQL **18.6** constan en evidencia autenticada previa. El ajuste SQL quedó **APPLIED_AND_VERIFIED** el 28 de septiembre: CONNECT y public USAGE/CREATE; sin CREATEDB/CREATEROLE ni membresías administrativas. Las secciones posteriores conservan los snapshots previos, no una lista vigente de operaciones por repetir.
- Credencial `postgres` previamente expuesta: cierre administrativo acreditado. Cristian rotó su contraseña en Cloud SQL el 2026-09-30 11:19 America/Bogota; la operación fue confirmada en Console. `visana-dev-db-password:1` corresponde a `visana_app_dev` por declaración del responsable, sin lectura del valor. La rotación no modificó `visana_app_dev`; no se recuperaron valores ni se probó la contraseña anterior. `PHASE2_GATE_DB=PASS` y `DEV_DB_CREDENTIAL_ROTATION_CONFIRMED=true` está configurada. El workflow legacy de `origin/dev` continúa congelado y todavía referencia `secrets.DB_PASSWORD` hasta integrar PR #20; PR #20 ya no consume ese secreto como credencial PostgreSQL. La revisión histórica `visana-api-dev-00001-dwp` permanece fallida y no se modificó manualmente.
- Keycloak 26.7.5 fijado por digest, realm `visana-erp`, PostgreSQL separado y cliente de health están preparados/probados solo en aislamiento. Pendientes cloud: `KEYCLOAK_ISSUER_URI`, `DEV_HEALTHCHECK_CLIENT_ID`, `DEV_HEALTHCHECK_SUBJECT`, `DEV_HEALTHCHECK_AUDIENCE` y `DEV_HEALTHCHECK_CLIENT_SECRET`; deben derivarse del IdP real autorizado, no de fixtures locales. El cliente frontend está deshabilitado y maintenance pertenece a A-06B.
- CI se conserva para PR/push. CD exige impacto backend por diff completo y verify exitoso. CORS viaja desde variable GitHub, queda vacío para backend DEV sin frontend y exige origen HTTPS aprobado al activar `DEV_FRONTEND_CLOUD_ENABLED=true`. Health exige aplicación y componente DB UP con detalles ocultos. PORT conserva `${PORT:8084}`; tests de binding verifican 8084/8080 y el servidor de prueba liga un puerto dinámico.
- COST-DEV vive en PR #21 y A-06B/V9 continúa en rama local dependiente. No se modifican R1 ni recursos cloud, no hay nuevo deploy, merge o Ready. Soporte Spring requiere decisión comercial o migración posterior separada; esta fase no realiza una migración mayor ni declara explotación sin evidencia.
- Keycloak tendrá costo continuo desde su primer despliegue hasta la futura activación de apagados. Su plantilla min 1/CPU continua no equivale a recurrencia COST-DEV activa.

Las entradas históricas inferiores deben leerse con su fecha. La configuración externa aún no está lista para deploy: código validado no implica `CONFIG_EXTERNAL_READY`, `DEPLOYED`, `HEALTHY` ni `DATABASE_CONNECTED`.

## Evidencia histórica y captura autenticada del 2026-09-26

El [run 36188945156](https://github.com/Visanaz/visana/actions/runs/36188945156), SHA 040ec3f929c15e546571f48fa425736dd83c3b36, publicó el digest sha256:b242ce81b91692d20e5cc5e80de44482e90b174689c444cf08b1671ba4eaa4a4. Artifact Registry confirmó etiqueta y digest. La revisión visana-api-dev-00001-dwp falló:

- 2026-09-25 21:02:01.856 UTC: Tomcat inicializado en 8084.
- 21:02:02.441: Spring cancela contexto, entityManagerFactory → Flyway → DataSource; mensaje terminal “URL must start with 'jdbc'”.
- 21:02:02.825: salida del contenedor 1; 21:02:02.901: falla probe TCP 8080.

La causa de arranque está respaldada por logs; no acredita un intento de autenticación/conexión SQL. El puerto ya usa PORT con fallback 8084; DEV valida la URL antes de Hikari/Flyway.

| Recurso consultado | Configuración comprobada |
|---|---|
| Proyecto | visana-erp-dev; número 984938781030 |
| Cloud Run | visana-api-dev, us-central1; https://visana-api-dev-984938781030.us-central1.run.app; única revisión fallida, 0 % tráfico |
| Exposición | Ingress All y allUsers: público. Retirar un flag del workflow no elimina ese binding |
| Probe | Startup TCP 8080, intervalo/timeout 240 s, umbral 1; no HTTP/JWT |
| Entorno | Solo nombres SPRING_PROFILES_ACTIVE y DB_PASSWORD; contraseña literal, sin referencia Secret Manager |
| Conectividad | Sin salida VPC, volúmenes ni conexión Cloud SQL configurada |
| Runtime | 984938781030-compute@developer.gserviceaccount.com, cuenta actual comprobada |
| Desplegador histórico | github-actions-dev@visana-erp-dev.iam.gserviceaccount.com; correspondencia con secreto actual pendiente |
| Artifact Registry | visana-repo, Docker, us-central1, mismo proyecto |
| Cloud SQL | visana-db-dev, PostgreSQL 18, us-central1-f; visana-erp-dev:us-central1:visana-db-dev |
| SQL red/TLS | Pública 34.42.149.84:5432; sin privada ni redes autorizadas; TLS obligatorio, certificado cliente obligatorio desactivado, CA interna Google |
| Bases/usuarios | Solo postgres y usuario integrado postgres; no seleccionados para VISANA ni privilegios acreditados |

Fuentes: [Cloud Run](https://console.cloud.google.com/run/detail/us-central1/visana-api-dev/revisions?project=visana-erp-dev), [SQL](https://console.cloud.google.com/sql/instances/visana-db-dev/connections/networking?project=visana-erp-dev), [AR](https://console.cloud.google.com/artifacts/docker/visana-erp-dev/us-central1/visana-repo/visana-api-dev?project=visana-erp-dev). Metadata no demuestra conexión real.

## Mecanismo implementado y dependencias

TCP directo era incompatible con la IP pública sin redes autorizadas. DEV utiliza **Java Cloud SQL PostgreSQL Connector 1.30.0**: SocketFactory oficial, connection name confirmado, PUBLIC, refresh lazy y enableIamAuth=false. DB_URL contiene exclusivamente ese contrato; DB_USER/DB_PASSWORD permanecen separados. Google ADC proviene de runtime; no de la clave del desplegador. El driver predeterminado opera dentro del transporte TLS autenticado del Connector; no se añaden opciones que desactiven TLS.

Sin segunda estrategia: no proxy, Unix socket, --add-cloudsql-instances, archivos JSON, VPC/NAT ni redes autorizadas. Las pruebas usan TCP aislado sin conexiones Google. El Connector exige salida a API Google HTTPS y transporte SQL TLS TCP 3307; comprobar política efectiva antes de despliegue, sin abrir red aquí. [JDBC 1.30.0](https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory/blob/v1.30.0/docs/jdbc.md), [propiedades 1.30.0](https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory/blob/v1.30.0/docs/configuration.md), [Cloud Run/SQL](https://docs.cloud.google.com/sql/docs/postgres/connect-run).

Versiones resueltas: Spring Boot 3.4.0, Java 21, Spring Security 6.4.1, Hikari 5.1.0, PostgreSQL JDBC **42.7.13**. Flyway Core/PostgreSQL/MySQL cambian juntos de 10.20.1 a **11.14.0**. El módulo antiguo declara PostgreSQL probado hasta 17, el nuevo hasta 18: [10.20.1](https://github.com/flyway/flyway/blob/flyway-10.20.1/flyway-database/flyway-database-postgresql/src/main/java/org/flywaydb/database/postgresql/PostgreSQLDatabase.java), [11.14.0](https://github.com/flyway/flyway/blob/flyway-11.14.0/flyway-database/flyway-database-postgresql/src/main/java/org/flywaydb/database/postgresql/PostgreSQLDatabase.java). El mantenimiento JDBC usa `postgresql.version`; no se actualizan frameworks ni se modifican migraciones históricas.

Las seis suites usan postgres:18.3-alpine y consultan la versión real JDBC; emiten únicamente POSTGRESQL18_EVIDENCE. CI rechaza omisiones y falta de versión. La suite de ciclo de vida cubre BD vacía, V1–V8, historial, segundo arranque sin reaplicación y salud UP/DOWN/UP ante fallo controlado. El JAR debe contener Connector, driver y Flyway coherentes. Esto acredita PostgreSQL 18 aislado, no Cloud SQL.

## Revisión acotada de dependencias

Consulta oficial del 26 de septiembre de 2026; coincidencia de versión no equivale a explotación. El JAR anterior acredita Framework 6.2.0, Security 6.4.1 y Data JPA 3.4.0. La revisión cubre sus mecanismos pertinentes y pgJDBC; no es un inventario exhaustivo de todas las dependencias transitivas.

| Componente / aviso | Condición y evidencia en esta base | Corrección / decisión |
|---|---|---|
| JDBC 42.7.4, [CVE-2025-49146](https://github.com/pgjdbc/pgjdbc/security/advisories/GHSA-hq9p-pm7w-8p54) | Requiere `channelBinding=require` y autenticación no SASL; DEV admite solo cinco parámetros y no establece ese modo. No aplicabilidad demostrada en este contrato | Corregido desde 42.7.7; mantenimiento seleccionado 42.7.13 |
| JDBC 42.7.4, [CVE-2026-42198](https://github.com/pgjdbc/pgjdbc/security/advisories/GHSA-98qh-xjc8-98pq) | SCRAM contra servidor malicioso/comprometido con iteraciones enormes. Connector fija instancia/transporte autenticado; no servidor comprometido demostrado | Corregido desde 42.7.11; 42.7.13 incorpora el límite |
| JDBC 42.7.4, [CVE-2026-54291](https://github.com/pgjdbc/pgjdbc/security/advisories/GHSA-j92g-9f8w-j867) | Requiere `channelBinding=require`, interceptación y certificado sin hash compatible; ese modo no está configurado en DEV | Corregido desde 42.7.12; 42.7.13 incorpora la corrección |
| Boot 3.4.0 / [fin de soporte abierto 3.4.x](https://spring.io/blog/2025/12/18/spring-boot-3-4-13-available-now/) | 3.4.13 fue la última entrega OSS. No suscripción comercial acreditada | Una actualización a 3.4.13 no recupera soporte abierto; decidir soporte comercial coherente o migración aprobada |
| Security 6.4.1 / [CVE-2026-22732](https://spring.io/security/cve-2026-22732/) | Servlet con cabeceras de Security: sí, `SecurityConfig.headers` y Tomcat. Condición pertinente; pérdida de cabeceras/explotación no reproducidas | 6.4.15 es comercial; no sustituir Security fuera de la gestión compatible de Boot. Decisión de soporte/migración pendiente |
| Security 6.4.1 / [CVE-2025-22223](https://spring.io/security/cve-2025-22223/), [CVE-2025-41248](https://spring.io/security/cve-2025-41248/); Framework 6.2.0 / [CVE-2025-41249](https://spring.io/security/cve-2025-41249/) | `@EnableMethodSecurity` existe, pero no anotaciones de autorización en métodos genéricos/heredados en `src/main`; la condición adicional no está presente | Sin mecanismo vulnerable demostrado en el código actual; conservar revisión al añadir anotaciones |
| Data JPA 3.4.0 / [CVE-2026-47834](https://spring.io/security/cve-2026-47834/) | Pageable existe, pero sort tiene allowlist y no hay `@NativeQuery`/`@Query(nativeQuery=true)` en `src/main` | No reúne las condiciones del aviso |

[42.7.13](https://jdbc.postgresql.org/changelogs/2026-07-06-42.7.13-release/) es mantenimiento para Java 8+, compatible con Java 21; incorpora invalidación de planes ante cambios `search_path` de PostgreSQL 18 y cambia limpieza de caché DDL. Flyway y las seis suites PostgreSQL deben pasar en el nuevo CI. La resolución Maven y presencia del Connector prueban compatibilidad de empaquetado, no un socket cloud real.

También se revisaron BCrypt (CVE-2025-22228), AspectJ (CVE-2025-41232), WebAuthn (CVE-2026-47841), AesBytesEncryptor (CVE-2026-47842), DPoP (CVE-2026-41707), DataBinder (CVE-2025-22233) y recursos versionados/caché compartida (CVE-2026-41841/41842/41843): no se evidenciaron sus mecanismos condicionantes en `src/main` y configuración vigente. MVC/Tomcat por sí solos no demuestran esos ataques. No habilitar mecanismos nuevos sin reevaluarlos.

Decisión propuesta, no aplicada: soporte comercial para el conjunto Boot/Security/Framework con versiones corregidas compatibles, o migración a una línea Boot 4.x OSS vigente. Esta última cambia Framework/Security, Jakarta, dependencias de persistencia/JSON y compatibilidad Springdoc/Keycloak; requiere revisión de guía oficial, contrato OpenAPI, JWT/JWKS, todas las pruebas PostgreSQL y empaquetado antes de aprobación de despliegue. No se ejecuta una migración mayor aquí.

## Contrato propuesto de identidad técnica

Issuer/cliente/claims reales no confirmados. No se crean clientes, mappers ni actores. Fuentes: KEYCLOAK_ISSUER_URI, DEV_HEALTHCHECK_CLIENT_ID, **DEV_HEALTHCHECK_SUBJECT**, **DEV_HEALTHCHECK_AUDIENCE**. Los dos campos nuevos identifican sujeto estable y audience dedicada sin cambiar globalmente la audience humana. Scope propuesto firmado: visana.health.

Cualquier marcador firmado (sub, azp/client_id, audience reservada o scope) clasifica técnico. El contrato exige sub exacto, azp exacto, client_id consistente si aparece, audience exclusivamente técnica y scope. Omisiones/contradicciones fallan cerradas; roles adicionales no cambian clasificación. El IdP debe mantener sujeto inmutable y reservar los marcadores. La restricción opera después del decoder de firma/issuer/vigencia.

| Caso | Resultado sintético probado |
|---|---|
| Sin token; malformado; firma/issuer/vigencia inválidos | 401 |
| Técnico completo GET exacto /actuator/health | 200 con dependencias disponibles; 503 ante fallo DB controlado |
| Técnico con roles adicionales | Conserva límite exclusivo de salud |
| Técnico: productos, categorías, me, pedidos/pago, genealogía, docs, otros Actuator | 403; no ejecución de negocio |
| Técnico incompleto/inconsistente | 401, sin fallback genérico |
| Técnico otros métodos/rutas de salud | 403 |
| Usuario humano válido | Política previa y controles de dominio conservados |
| Docs anónimos | Política pública previa conservada; token técnico denegado |

Pruebas RSA/JWKS local con decoder real; pruebas de dominio existentes conservan vinculación/propiedad. No valida IdP real. El checker usa Authorization OIDC; no genera token Google para el servicio público observado. Privatizarlo requiere decisión sobre frontend/checker/IAM. Health permanece autenticado; probe TCP es compatible con 8080; HTTP sin bearer a salud sería incompatible.

## Incidencia y Secret Manager

Una salida de herramienta mostró accidentalmente una credencial PostgreSQL literal. Registro mínimo **privado en respaldo existente**, sin literal/hash/copia de salida. Responsable pendiente; rotación/revocación pendiente de confirmación autorizada. Bloquea su uso real, no pruebas sintéticas.

Secuencia preparada, no ejecutada: identificar usuario/consumidores; cambiar o revocar en PostgreSQL; actualizar consumidores por canal seguro; conservar evidencia administrativa/servidor de invalidación y éxito de nuevos consumidores sin valores ni hashes; revisar copias/accesos sin borrar históricos unilateralmente. No recuperar ni probar la contraseña antigua. Cambiar solo GitHub Secrets no rota PostgreSQL; crear otro usuario tampoco invalida la credencial del anterior. No restaurar la contraseña expuesta como rollback. La contención es independiente de la provisión del IdP.

Propuesta externa: almacenar **credencial ya rotada** en secreto/versión numérica aprobados; acreditar secretmanager.versions.access de runtime sobre ese secreto; configurar DEV_DB_PASSWORD_SECRET_REF=nombre:version y DEV_DB_CREDENTIAL_ROTATION_CONFIRMED=true; futura revisión consume DB_PASSWORD por referencia. Si el servicio aún contiene el literal, el despliegue aprobado retirará únicamente esa entrada antes de agregar su referencia en la misma operación; no elimina variables ajenas. [Flags gcloud](https://docs.cloud.google.com/sdk/gcloud/reference/run/deploy). No se inventa nombre/versión ni se crea recurso aquí. CD rechaza referencias flotantes y no transporta literales. La atestación administrativa no demuestra validez.

Diagnósticos proyectan campos permitidos antes de escribir; excluyen env sensibles, argumentos, cuerpos HTTP y excepciones con URL/valores. Pruebas con secretos ficticios cubren discrepancias y errores HTTP/JSON/red. Artefactos publicados: JAR/procedencia y evidencia sanitizada de versiones, sin volcados cloud ni credenciales.

## Pendientes externos de la captura del 2026-09-26

| Principal/responsable | Recurso/acción | Evidencia y pendiente |
|---|---|---|
| PostgreSQL | Rotación, base, usuario, esquema, privilegios aplicación/Flyway | Solo postgres observado. DBA debe acreditar conexión/uso/DDL y migraciones; no SQL real en esta tarea |
| Runtime actual | Instancia SQL: cloudsql.instances.get/connect, Cloud SQL Client | Rol no observado en IAM del proyecto; alcance heredado/condicional no acreditado. Conceder solo si se demuestra ausente |
| Runtime actual | Versión del secreto: secretmanager.versions.access | Recurso/ref pendientes; almacenamiento, permiso y revisión consumidora son distintos de rotación |
| Desplegador histórico | Run/AR/actAs | Se observaron Run Admin, AR Writer, SA User y SQL Client. Confirmar identidad de secreto actual; no pedir permisos genéricos adicionales |
| IdP | Issuer HTTPS/discovery, sujeto/cliente/audience/scope y credencial | Contrato real pendiente; carga segura por responsable |
| GitHub | Fuentes enumeradas en README | Sin variables en captura anterior; ninguna carga aplicada ni Environment creado |
| Exposición | Mantener público o aprobar política privada | AllUsers/ingress All reales; decidir efectos frontend/checker |

No se cambia runtime. Una SA dedicada sería una decisión separada, no un recurso creado. API/cuota SQL y permisos efectivos deben acreditarse antes de activar Connector; no se habilitan APIs.

## Gates y reversión

CI precede CD; PR omite CD; JAR/digest son del mismo SHA validado. El nuevo CI debe aprobar: las 153 pruebas anteriores no validan esta corrección. Capturas sanitizadas verifican identidad/configuración/ref de secreto/revisión/digest; salud autenticada exige UP.

Futura integración/despliegue requiere autorización separada. El arranque puede ejecutar Flyway. No hay revisión previa sana acreditada ni rollback funcional probado. Revertir imagen no revierte migraciones; no repair/baseline/down migrations ni credencial expuesta.

**Estados separados:** código validado en entorno aislado; configuración externa pendiente de aprobación/aplicación; despliegue no realizado; conexión Cloud SQL real no verificada.

## Reconciliación manual 2026-09-28

Solicitud de Cristian, responsable de la configuración manual. Punto de partida verificado: rama `fix/cloud-run-dev-ci-cd`, HEAD local/remoto `fbc570d4a7b91f519326bb44dae363efe640f5fe`, base remota `dev` en `040ec3f929c15e546571f48fa425736dd83c3b36`, PR #20 OPEN/Draft. [CI 36291639215](https://github.com/Visanaz/visana/actions/runs/36291639215) SUCCESS, CD SKIPPED. Esta actualización documental no cambia código, workflows, migraciones ni configuración externa. Las 105 rutas originales pasan la comprobación de contenido/ausencia, estado Git e índice.

El PDF «infra visana gemini.pdf» no se encontró en workspace, adjuntos ni Downloads; no fue leído. Las capturas mencionadas no llegaron como archivos independientes en esta solicitud. Se contrastaron los datos transcritos con la consola autenticada y el contrato del HEAD.

### Estado previo a L-01–L-06 — snapshot histórico

Las filas siguientes registran la captura anterior al lote. No describen el estado posterior: L-01–L-06 ya aplicado, Allow permite las operaciones y deny sigue desconocido. La lectura SQL nueva y su interpretación aparecen en la sección de validación inferior.

| Elemento | Resultado / límite de evidencia |
|---|---|
| Runtime del servicio | `984938781030-compute@developer.gserviceaccount.com`, opción actual seleccionada en Seguridad de `visana-api-dev`; no se cambió ni guardó una revisión |
| Desplegador | Principal histórico `github-actions-dev@visana-erp-dev.iam.gserviceaccount.com`; IAM conserva Run Admin, AR Writer, SA User y SQL Client. La existencia del secreto GitHub `GCP_CREDENTIALS` no permite demostrar su identidad interna sin leerlo; correspondencia pendiente del responsable |
| Agente de plataforma | `service-984938781030@serverless-robot-prod.iam.gserviceaccount.com`: `roles/run.serviceAgent` y bindings adicionales `roles/cloudsql.client` y `roles/secretmanager.secretAccessor`, ambos sobre el proyecto |
| Runtime / SQL | Policy Troubleshooter: `cloudsql.instances.get` y `cloudsql.instances.connect` en `visana-db-dev`: «No puede acceder»; ninguna política de permiso otorga acceso |
| Runtime / secreto | `secretmanager.versions.access` sobre `visana-dev-db-password`: mismo resultado. No se leyó el valor del secreto |
| Alcance del diagnóstico | No existen políticas de límite de acceso en la evaluación. El operador no puede consultar las políticas de denegación: estado desconocido; no afirmar ausencia de deny ni éxito futuro tras conceder un rol |
| Cloud SQL | Instancia `visana-db-dev`, PostgreSQL 18; base `visana_dev` comprobada, UTF8 / en_US.UTF8; usuario `visana_app_dev` comprobado, autenticación integrada |
| Atributos y privilegios SQL | `[NO EVIDENCIADO]`: la tabla Usuarios no acredita membresías/CREATEDB/CREATEROLE ni privilegios de esquema/objetos. SQL Studio presenta autenticación, sin sesión SQL autorizada disponible |
| Secret Manager | `projects/984938781030/secrets/visana-dev-db-password`; versión numérica `1`, habilitada, creada 2026-09-28. Sin binding de acceso para runtime; acceso del agente de plataforma heredado del proyecto. El contenido y su correspondencia con la credencial nueva no fueron consultados |
| GitHub | Cero variables y cero Environments. Secretos existentes: `GCP_PROJECT_ID`, `GCP_CREDENTIALS`, `DB_PASSWORD`; falta `DEV_HEALTHCHECK_CLIENT_SECRET` |

Fuentes actuales: [runtime](https://console.cloud.google.com/run/detail/us-central1/visana-api-dev/security?project=visana-erp-dev), [IAM](https://console.cloud.google.com/iam-admin/iam?project=visana-erp-dev), [bases](https://console.cloud.google.com/sql/instances/visana-db-dev/databases?project=visana-erp-dev), [usuarios](https://console.cloud.google.com/sql/instances/visana-db-dev/users?project=visana-erp-dev), [versiones](https://console.cloud.google.com/security/secret-manager/secret/visana-dev-db-password/versions?project=visana-erp-dev), [permisos del secreto](https://console.cloud.google.com/security/secret-manager/secret/visana-dev-db-password/permissions?project=visana-erp-dev), [diagnóstico IAM](https://console.cloud.google.com/iam-admin/troubleshooter?project=visana-erp-dev). No se ejecutó lectura de payload ni prueba de contraseñas.

Los dos bindings adicionales del agente de plataforma están identificados; no se incluye su retirada en el lote. Falta acreditar que se concedieron por error y revisar impacto sobre otros consumidores. El listado global de servicios Cloud Run devolvió «El servidor no pudo completar tu solicitud»; no equivale a ausencia de otros servicios. Conservar el principal y `roles/run.serviceAgent`; cualquier retirada posterior será de bindings concretos, sin reemplazar políticas completas.

### Lote L-01–L-06 — propuesta histórica, posteriormente aplicada

Principal **R** = `serviceAccount:984938781030-compute@developer.gserviceaccount.com`. Las cuatro variables GitHub pertenecen a `Visanaz/visana`, nivel repositorio. La tabla conserva el estado previo y el cambio aprobado: los seis cambios ya están aplicados y no se repiten. No se cargó una contraseña en GitHub.

| ID / destino | Estado previo | Cambio exacto autorizado | Fuente y comprobación | Impacto / reversión |
|---|---|---|---|---|
| L-01 / proyecto `visana-erp-dev`, principal R | Sin permiso SQL get/connect en diagnóstico | Agregar solo binding `roles/cloudsql.client`, sin condición nueva | Runtime real + diagnóstico; tras aprobación comprobar ambas capacidades, con límite de visibilidad deny | Permite transporte Connector a instancias del proyecto; no concede privilegios PostgreSQL. Revertir solo este binding añadido |
| L-02 / `projects/visana-erp-dev/secrets/visana-dev-db-password`, principal R | Sin permiso de acceso al secreto | Agregar solo binding `roles/secretmanager.secretAccessor` en este secreto, sin condición nueva | Secreto real + diagnóstico; verificar política del recurso y permiso sin leer payload | Permite acceder a versiones de este secreto, no a todos los secretos del proyecto. Revertir solo este binding añadido |
| L-03 / variable `DB_URL` | Ausente | URL literal del bloque siguiente | Base y connection name confirmados; guard Python/Java exige los cinco parámetros exactos | Selecciona `visana_dev` en futuro CD; no conecta ni migra al cargarla. Reversión: eliminar únicamente esta variable nueva |
| L-04 / variable `DB_USER` | Ausente | `visana_app_dev` | Usuario integrado comprobado; consumidor `application-dev.properties` | Selecciona usuario para futuro arranque; no cambia su contraseña/roles. Reversión: eliminar esta variable nueva |
| L-05 / variable `DEV_RUNTIME_SERVICE_ACCOUNT` | Ausente | `984938781030-compute@developer.gserviceaccount.com` | Identidad actual seleccionada; guard rechaza diferencias | Declara la identidad existente para futuro CD. Reversión: eliminar esta variable nueva |
| L-06 / variable `DEV_DB_PASSWORD_SECRET_REF` | Ausente | `visana-dev-db-password:1` | Versión numérica 1 habilitada comprobada; guard rechaza `latest` | Fija referencia para futuro CD; no lee/copiará payload al configurarla. Reversión: eliminar esta variable nueva |

Valor exacto de L-03:

```text
jdbc:postgresql:///visana_dev?socketFactory=com.google.cloud.sql.postgres.SocketFactory&cloudSqlInstance=visana-erp-dev:us-central1:visana-db-dev&ipTypes=PUBLIC&cloudSqlRefreshStrategy=lazy&enableIamAuth=false
```

La aprobación L-01–L-06 no autoriza merge, Ready, despliegue, revisión manual, rotación, escrituras SQL ni nuevas versiones del secreto. Antes de aplicar, releer el estado y omitir concesiones ya efectivas; un cambio de identidad/recurso/valor requiere revisar el lote. Después, comprobar metadatos y diagnosticar acceso, conservando cualquier incertidumbre de deny.

### Entradas bloqueadas o ya existentes

| Entrada / decisión | Estado | Dato o acción concreta pendiente |
|---|---|---|
| `DEV_DB_CREDENTIAL_ROTATION_CONFIRMED` | Falta evidencia administrativa | No cargar `true`: identificar responsable y usuario de la credencial expuesta, acreditar revocación/contención y consumidores actualizados. Crear `visana_app_dev` no cierra el incidente. No recuperar ni probar la contraseña anterior |
| Credencial nueva en secreto | Falta dato concreto del responsable | Acreditar que versión 1 contiene la credencial nueva de `visana_app_dev`, sin entregar su valor. Existencia/habilitación no prueban esa correspondencia ni validez |
| `KEYCLOAK_ISSUER_URI` | Falta issuer cloud verificable | HTTPS, discovery y JWKS accesibles desde cloud; no usar localhost ni inventar URL |
| `DEV_HEALTHCHECK_CLIENT_ID`, `DEV_HEALTHCHECK_SUBJECT`, `DEV_HEALTHCHECK_AUDIENCE` | Falta contrato IdP real | Cliente confidencial con service account, sujeto estable, `azp` exacto, audience exclusivamente dedicada y scope `visana.health`; las identidades locales encontradas no satisfacen este contrato |
| `DEV_HEALTHCHECK_CLIENT_SECRET` | Secreto GitHub ausente | Provisión segura por responsable del cliente real; no enviar por conversación ni reutilizar secretos de ejemplos |
| `GCP_PROJECT_ID`, `GCP_CREDENTIALS` | Secretos GitHub ya existentes | Conservar. Responsable debe acreditar proyecto e identidad del desplegador sin revelar clave; nombres no prueban valores |
| GitHub `DB_PASSWORD` | Consumidor histórico demostrado | Conservar: `dev` aún referencia `secrets.DB_PASSWORD` en `.github/workflows/deploy-dev.yml`, blob `acd7ed6099c7264ec3ed03034ab153cb8d0ddc9e`. Ninguno en candidato; `qa`/`main` no ofrecen workflows consultables. No retirar sin inventario completo y aprobación |
| SQL / privilegios y propiedad | Comprobados en sesión autorizada posterior | Excesos administrativos confirmados; ajuste preparado y probado solo en aislamiento. Véase validación inferior |
| Retirada de roles al agente | Falta atribución de error e impacto | No incluida en lote; no tocar `roles/run.serviceAgent` |

### Keycloak local recuperado

En el backend, `visana-realm-export.json` define realm `visana-erp` y cliente público OIDC `visana-backend`; la configuración general usa como fallback `http://localhost:8080/realms/visana-erp`. El SDK Admin 26.0.0 no demuestra la versión del servidor local.

El worktree autorizado frontend contiene `dev/local-demo/docker-compose.yml` y `e2e/auth/docker-compose.auth-e2e.yml`: imagen `quay.io/keycloak/keycloak:26.7.3`, `start-dev`, binding loopback. Sus exports definen `visana-local` / `visana-local-spa` y `visana-e2e` / `visana-e2e-spa`, clientes públicos con service accounts desactivadas. Son configuraciones de demostración/pruebas; no evidencia de proceso ejecutándose ni de publicación cloud. No se mostraron secretos de exports/compose. El compose de Downloads usa realm `sitfai-erp`; no pertenece al issuer VISANA.

Bloqueo mínimo: el responsable debe proporcionar un Keycloak VISANA publicado y verificable por HTTPS, o autorizar en una tarea separada su publicación con persistencia/configuración apropiadas y provisión del cliente técnico exacto. Verificar discovery/JWKS y claims firmados antes de cargar las cinco entradas OIDC. No desplegarlo desde esta tarea ni reutilizar el cliente público local para `client_credentials`.

### Consultas de lectura para Cristian en Cloud SQL Studio

Seleccionar **visana_dev** y una sesión autorizada. Preferir sesión de `visana_app_dev` para observar su esquema efectivo; si se ejecuta como administrador, `current_schema` y `search_path` describen esa sesión, no el usuario aplicación. Compartir solo resultados de metadata, sin contraseñas ni hashes. No ejecutar migraciones.

```sql
BEGIN READ ONLY;
SELECT current_database(), current_user, session_user,
       current_schema(), current_schemas(false);
SHOW search_path;

SELECT rolname, rolcanlogin, rolsuper, rolinherit, rolcreatedb,
       rolcreaterole, rolreplication, rolbypassrls
FROM pg_roles WHERE rolname = 'visana_app_dev';

SELECT parent.rolname AS granted_role, member.rolname AS member,
       grantor.rolname AS grantor, m.admin_option, m.inherit_option,
       m.set_option
FROM pg_auth_members m
JOIN pg_roles parent ON parent.oid = m.roleid
JOIN pg_roles member ON member.oid = m.member
JOIN pg_roles grantor ON grantor.oid = m.grantor
WHERE member.rolname = 'visana_app_dev';

SELECT rolname,
       pg_has_role('visana_app_dev', oid, 'MEMBER') AS member_direct_or_indirect,
       pg_has_role('visana_app_dev', oid, 'USAGE') AS privileges_available
FROM pg_roles
WHERE rolname <> 'visana_app_dev'
  AND pg_has_role('visana_app_dev', oid, 'MEMBER');

SELECT d.datname, pg_get_userbyid(d.datdba) AS owner,
       has_database_privilege('visana_app_dev', d.oid, 'CONNECT') AS can_connect
FROM pg_database d WHERE d.datname = 'visana_dev';

SELECT COALESCE(d.datname, '*') AS database_name,
       COALESCE(r.rolname, '*') AS role_name, cfg AS search_path_setting
FROM pg_db_role_setting s
LEFT JOIN pg_database d ON d.oid = s.setdatabase
LEFT JOIN pg_roles r ON r.oid = s.setrole
CROSS JOIN LATERAL unnest(s.setconfig) cfg
WHERE (s.setdatabase = 0 OR d.datname = 'visana_dev')
  AND (s.setrole = 0 OR r.rolname = 'visana_app_dev')
  AND cfg LIKE 'search_path=%';

SELECT n.nspname, pg_get_userbyid(n.nspowner) AS owner,
       has_schema_privilege('visana_app_dev', n.oid, 'USAGE') AS can_use,
       has_schema_privilege('visana_app_dev', n.oid, 'CREATE') AS can_create
FROM pg_namespace n
WHERE n.nspname NOT LIKE 'pg_%' AND n.nspname <> 'information_schema';

SELECT n.nspname, c.relname, c.relkind, pg_get_userbyid(c.relowner) AS owner,
       pg_has_role('visana_app_dev', c.relowner, 'USAGE') AS owner_role_available,
       has_table_privilege('visana_app_dev', c.oid, 'SELECT') AS can_select,
       has_table_privilege('visana_app_dev', c.oid, 'INSERT') AS can_insert,
       has_table_privilege('visana_app_dev', c.oid, 'UPDATE') AS can_update,
       has_table_privilege('visana_app_dev', c.oid, 'DELETE') AS can_delete,
       has_table_privilege('visana_app_dev', c.oid, 'REFERENCES') AS can_reference
FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
WHERE c.relkind IN ('r', 'p', 'v', 'm')
  AND n.nspname NOT LIKE 'pg_%' AND n.nspname <> 'information_schema'
ORDER BY n.nspname, c.relname;

SELECT n.nspname, c.relname, pg_get_userbyid(c.relowner) AS owner,
       has_sequence_privilege('visana_app_dev', c.oid, 'USAGE') AS can_use,
       has_sequence_privilege('visana_app_dev', c.oid, 'SELECT') AS can_select,
       has_sequence_privilege('visana_app_dev', c.oid, 'UPDATE') AS can_update
FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
WHERE c.relkind = 'S'
  AND n.nspname NOT LIKE 'pg_%' AND n.nspname <> 'information_schema';

SELECT n.nspname, c.relname
FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
WHERE c.relname = 'flyway_schema_history';
ROLLBACK;
```

Solo si el último SELECT confirma `public.flyway_schema_history`, ejecutar aparte:

```sql
BEGIN READ ONLY;
SELECT installed_rank, version, description, type, installed_by,
       installed_on, success
FROM public.flyway_schema_history ORDER BY installed_rank;
ROLLBACK;
```

Si se encuentra en otro esquema, sustituir `public` únicamente por el esquema comprobado; si no existe, registrar ausencia y no crear/baseline/repair. Las migraciones V1–V8 actuales crean tablas/índices/constraints en esquema no cualificado e insertan datos en V8; no crean roles, bases ni extensiones. Flyway y aplicación comparten DataSource: requieren CONNECT, USAGE/CREATE del esquema efectivo y propiedad compatible para DDL, además de DML sobre objetos. Solo conceder DML a un esquema vacío no permite este arranque.

La [documentación Cloud SQL](https://docs.cloud.google.com/sql/docs/postgres/users) explica la concesión predeterminada `cloudsqlsuperuser`/CREATEDB/CREATEROLE a usuarios integrados sin roles asignados; esto exige verificar el usuario real, no afirmar que los tiene. Si los resultados demuestran esos privilegios innecesarios, preparar `REVOKE cloudsqlsuperuser FROM visana_app_dev` y `ALTER ROLE visana_app_dev NOCREATEDB NOCREATEROLE`, junto con los grants mínimos y propiedad que los resultados indiquen; comprobar membresías indirectas antes. No ejecutar ni autorizar esos ajustes dentro de L-01–L-06.

### Preservación del pipeline y decisión

`build.yml` exige CI antes de CD y omite CD para PR; los guards reales aceptan la URL propuesta y mantienen DB_URL/DB_USER, ADC runtime, puerto, referencia fija y cliente técnico. No hace falta sustituir YAML. Se revisó `deploy-cloudrun@v2`, resuelto a `251330ba9a8a34bfbc1622895f42e1d53fd14522`: merge produce `--update-env-vars` y `--update-secrets`; overwrite produciría `--set-*`. La acción añade los flags extra, incluido `--remove-env-vars=DB_PASSWORD` cuando el guard detecta literal. No se incorpora DB_PASSWORD al archivo de variables. [Código oficial de esa revisión](https://github.com/google-github-actions/deploy-cloudrun/blob/251330ba9a8a34bfbc1622895f42e1d53fd14522/src/main.ts), [semántica gcloud](https://docs.cloud.google.com/sdk/gcloud/reference/run/deploy). La transición está preparada para el futuro despliegue aprobado; no se hizo una revisión manual ni se borraron variables ajenas.

**Estado de integración reconciliado:** L-01–L-06 y ajuste de privilegios SQL aplicados. Cierre administrativo de credencial antigua, correspondencia de nueva credencial e identidad del desplegador pendientes; Keycloak preparado/probado en aislamiento, sin issuer/cliente cloud creado. Estos externos impiden acreditar deploy. Soporte Spring se decide separadamente según el objetivo maestro. Integrar este cambio en `dev` invocará CI y el gate CD; sus cambios backend habilitan el candidato, cuyo preflight debe bloquear mientras falten externos. El arranque autorizado podrá aplicar V1–V8. Configurar variables/permisos no demuestra arranque exitoso.

## Validación SQL y preparación Keycloak 2026-09-28

Continuación autorizada por Cristian desde HEAD `75eab56fe7386c392485e33a528e582e8995709d`, [CI 36465924880 SUCCESS](https://github.com/Visanaz/visana/actions/runs/36465924880), CD omitido. No se repitió L-01–L-06 ni se amplió la auditoría IAM/Spring. Se conservan la incertidumbre de deny, la correspondencia del desplegador pendiente y la retirada separada de bindings del agente serverless.

### Resultado SQL ejecutado — HECHO

Fuente: [Cloud SQL Studio](https://console.cloud.google.com/sql/instances/visana-db-dev/studio?project=visana-erp-dev), autenticación completada manualmente por Cristian. Se ejecutaron bloques `BEGIN READ ONLY` / `ROLLBACK`, con `transaction_read_only=on`; primero contexto, después metadata en una misma transacción. El bloque reproducible revisado está en [inspect_visana_dev.sql](../../scripts/ci/inspect_visana_dev.sql). No se consultaron datos comerciales, `pg_authid`, contraseñas ni hashes.

| Metadata observada | Resultado |
|---|---|
| Sesión y objetivo | `current_database=visana_dev`; `current_user=session_user=visana_app_dev`; permisos evaluados de ese usuario |
| Servidor | PostgreSQL **18.6** |
| Esquema efectivo | `public`; `current_schemas={public}`; `search_path="$user", public`; sin overrides pertinentes en `pg_db_role_setting` |
| Atributos | LOGIN e INHERIT true; SUPERUSER, REPLICATION y BYPASSRLS false; **CREATEDB y CREATEROLE true** |
| Membresía directa | `cloudsqlsuperuser`, otorgada por `cloudsqladmin`; admin false, inherit true, set true |
| Membresías indirectas efectivas | Profundidad 2: `pg_signal_backend`, `pg_monitor`, `pg_checkpoint`; profundidad 3: `pg_stat_scan_tables`, `pg_read_all_stats`, `pg_read_all_settings`; USAGE/SET disponibles |
| Opción admin en la cadena | La arista `cloudsqlsuperuser → pg_checkpoint` tiene admin true; no atribuir por ello administración de todas las membresías al usuario aplicación |
| Base | Propietario `cloudsqlsuperuser`; CONNECT, CREATE y TEMPORARY true; PUBLIC recibe CONNECT y TEMPORARY |
| Esquema public | Propietario `pg_database_owner`; USAGE y CREATE true para aplicación; PUBLIC recibe únicamente USAGE |
| Objetos | **0 tablas, 0 secuencias, 0 historiales Flyway**; ningún propietario/permiso de objetos por evaluar |

**A. Privilegios:** los derechos necesarios para V1–V8 están presentes. La BD vacía es el estado anterior a la primera migración, no un error. Sobran CREATEDB/CREATEROLE y la membresía administrativa con derechos indirectos. CREATE en `public` deriva actualmente de la propiedad heredada de la base: revocar la membresía sin conceder antes USAGE/CREATE directos puede impedir las migraciones. Las migraciones reales no requieren crear bases/roles ni esas capacidades administrativas.

**B. Autenticación:** la sesión de Studio como `visana_app_dev` fue observada y pudo leer metadata. No se utilizó el valor enviado por conversación. Esto no verifica qué payload contiene Secret Manager ni la revocación de una credencial anterior.

**C. Cloud Run/Connector:** conexión del backend mediante ADC, referencia Secret Manager y Connector **todavía no probada**. La lectura de Studio usa otro camino.

[visana_dev_privileges_proposal.sql](../../scripts/ci/visana_dev_privileges_proposal.sql) propone, para aprobación posterior: conservar CONNECT, conceder USAGE/CREATE directos en `public`, revocar `cloudsqlsuperuser` y fijar NOCREATEDB/NOCREATEROLE. Tiene guards de base, propietario, atributos y ausencia de objetos. No altera propietarios ni ACL de PUBLIC; el dueño futuro de tablas será la identidad que ejecute Flyway. Antes de aplicarlo, un administrador autorizado debe repetir metadata y revisar consumidores; después, verificar desde una sesión nueva del usuario, sin membresías administrativas indirectas. **No ejecutado contra Cloud SQL.**

Prueba de la propuesta: `python -B scripts/ci/check_sql_privileges_proposal.py`, PostgreSQL 18.3 desechable, `--network none`, sin puertos ni credenciales reales. Reproduce la propiedad heredada, rechaza base incorrecta, aplica únicamente allí el ajuste y ejecuta los SQL V1–V8 como usuario restringido: **PASS, 23 tablas, todas propiedad de visana_app_dev**; CONNECT/USAGE/CREATE conservados y los tres privilegios administrativos comprobados quedan false. No sustituye una ejecución real de Flyway/Cloud SQL.

### Declaración de Cristian sobre credenciales — PENDIENTE

Fuente: respuesta agrupada del responsable en esta conversación, 2026-09-28. Declara creación de base/usuario y versión 1 habilitada; no confirma el usuario de la credencial expuesta, rotación/revocación, actor/operación/fecha, correspondencia de versión 1 con la contraseña de `visana_app_dev`, ni actualización de todos los consumidores. Declara que no hay nueva revisión Cloud Run consumiendo esa referencia y que `DB_PASSWORD` histórico sigue utilizado en `dev`.

Se registra como **declaración del responsable**, no verificación técnica. Incidencia abierta; `DEV_DB_CREDENTIAL_ROTATION_CONFIRMED` no se establece en true. No se leyó payload ni se probó la credencial anterior. La confirmación de acceso al editor no completa los datos administrativos pendientes.

Operación separada para futura aprobación: identificar primero el usuario y consumidores de la credencial expuesta; el responsable autorizado rota/revoca directamente en PostgreSQL, provisiona la credencial correcta por interfaz segura/versionado y actualiza cada consumidor inventariado. Registrar usuario, actor, operación, fecha y consumidores pendientes, sin valores. No reemplazar/eliminar `DB_PASSWORD` histórico hasta atender su consumidor. Crear un usuario nuevo no acredita esa rotación.

### Keycloak recuperado y diferencias de la preparación

Fuentes acotadas: export raíz del backend y Compose/realms referenciados en `worktrees/visana-front-visual/dev/local-demo` y `e2e/auth`. Inspección de solo lectura del contenedor existente `visana-local-demo-keycloak-1`: **Keycloak 26.7.3**, JVM 21.0.12.1. Esa observación local es histórica; el Dockerfile cloud preparado fija ahora la base oficial 26.7.5 por digest. El SDK Admin 26.0.0 del backend no es la versión del servidor.

El demo existente usa `start-dev`, puertos loopback 8080/9000, almacenamiento Keycloak dev-file/H2 en tmpfs y realm `visana-local`; su PostgreSQL 16 separado sirve al backend, no acredita persistencia PostgreSQL de Keycloak. E2E usa realm `visana-e2e`. Sus clientes SPA son públicos, sin service account y sin scope de salud. No se encontró cliente Swagger específico ni técnico de salud. No se modificaron esos procesos, puertos, volúmenes ni exports.

Se reutiliza el realm raíz **visana-erp**, roles SUPER_ADMIN/DISTRIBUTOR/AFFILIATE/EXTERNAL_CUSTOMER y mapper `tenant_id`, sin copiar usuarios, credenciales, claves ni SMTP. En el archivo preparado `visana-backend` se mantiene **deshabilitado** hasta aprobar redirects y flujos humanos. Se añade el cliente técnico confidencial **visana-dev-health**, service account, sin login humano/direct grants/implicit, scope `visana.health`, mapper de sujeto y audience exclusivamente `visana-dev-health`, TTL 60 s. El secreto es un placeholder resuelto al arrancar. El `sub` se obtiene de la cuenta real persistida; ningún UUID local se carga en GitHub.

### Archivos y pruebas ejecutadas

| Archivo | Función |
|---|---|
| `infra/keycloak/Dockerfile`, `.dockerignore`, `keycloak.conf` | Imagen oficial 26.7.5 por digest, build PostgreSQL/health/metrics; contexto limitado; `start --optimized` sin `start-dev` |
| `infra/keycloak/visana-erp-realm.json` | Importación sanitizada no destructiva; contrato técnico completo |
| `infra/keycloak/cloud-run-service.template.json` | Propuesta de servicio con hostname/digest pendientes, proxy, secretos numéricos, probes y recursos concretos |
| `infra/keycloak/postgres-proposal.sql` | Nueva base/usuario exclusivos de Keycloak, privilegios limitados, sin contraseña literal; **no aplicado** |
| `infra/keycloak/test-postgres-init.sql`, `compose.keycloak-test.yml` | Fixture aislada con usuario limitado, secretos sintéticos, puertos aleatorios loopback y volumen propio |
| `scripts/ci/keycloak_dev.py`, `test_keycloak_dev.py` | Validar, probar y renderizar sin llamadas cloud; guards de hostname, digest y alcance |
| `KeycloakHealthContractTest.java` | Token firmado de Keycloak real contra SecurityConfig/conversor existentes; prueba enfocada habilitada solo por fixture aislada |
| `scripts/ci/inspect_visana_dev.sql`, `visana_dev_privileges_proposal.sql`, `check_sql_privileges_proposal.py` | Lectura reproducible, ajuste propuesto y verificación sintética |
| `.github/workflows/openapi-contract.yml` | Dos pasos de prueba en el CI existente; ningún CD nuevo ni secretos cloud |

Ejecución histórica 2026-09-28: **8 comprobaciones PASS** con Keycloak 26.7.3 y PostgreSQL 18.3; 14 guards Python PASS y propuesta SQL PASS. La prueba Java enfocada ejecutó **1 test, 0 fallos, 0 errores, 0 omitidos** con Maven/JDK 21 en contenedor, fuente montada en lectura y salida fuera de Git. No se arranca la aplicación completa ni su DataSource real.

**Preparación actual 26.7.5:** KC-00F verificó digest oficial estable `sha256:37dbaf6f0722c9ec246335f36e1ef8b2e6cb960f7c27e0d8c615121a3d475a85`, build linux/amd64, 8 comprobaciones aisladas PASS con PostgreSQL 18.3 y contrato backend enfocado (1 test, 0 fallos/errores/omitidos). Trivy 0.74.0: 0 CRITICAL, 5 HIGH, 0 secretos; cinco CVE eliminadas frente a 26.7.4 y ninguna nueva. Permanecen CVE-2026-86145 y CVE-2026-89161 en pcre2/pcre2-syntax (cuatro ocurrencias) y CVE-2025-59250 en mssql-jdbc (una). Gitleaks 8.30.1: `KNOWN_INHERITED_BASE_FINDING` `generic-api-key` en `java.security`, 1 en base/built, 0 source y 0 nuevos; no se clasifica como falso positivo. Los resultados no prueban explotabilidad ni riesgo cero. [Evidencia KC-00F](https://github.com/Visanaz/visana/actions/runs/36761973670). La imagen Keycloak aún no fue publicada en Artifact Registry ni desplegada en cloud.

- Arranque `start --optimized` con PostgreSQL externo limitado; health started/ready/live UP.
- Discovery/JWKS; token client credentials con issuer/azp/sub/audience/scope exigidos. Nimbus del backend valida firma, issuer y vigencia con JWKS real.
- Reemplazo del contenedor conservando su BD: mismo sujeto, claves de firma y modificación sintética del realm; import no sobrescribe el realm existente.
- GET exacto `/actuator/health` → **200/UP**; negocio, docs, subrutas Actuator y POST health → **403**; health anónimo → **401**. Política del backend sin cambios.
- Binario Auth Proxy 2.25.4 y flags propuestos disponibles: prueba de versión/ayuda, **sin ADC ni conexión cloud**.
- Valores sintéticos generados/tokens ausentes de capas guardadas de imagen, logs y artefactos retenidos. Fixtures con secretos/tokens se eliminan; recursos Docker exclusivos se limpian. La búsqueda cubre los valores generados por la prueba, no certifica ausencia universal de secretos de terceros.

Registros locales sanitizados: `C:\Users\Kmilo\.codex\backups\visana-ci-cd-20260926\keycloak-proof-20260928\result.json` y reporte Surefire dentro de `backend-build`; metadata SQL capturada en `sql-readonly-20260928-metadata.json` del mismo respaldo. No se publican fixtures ni respaldos. CI vuelve a ejecutar las comprobaciones sobre el nuevo SHA; su resultado debe leerse en el run correspondiente, sin atribuirle anticipadamente este PASS local.

### Lote Keycloak DEV propuesto, no aplicado

**Una propuesta para aprobación posterior**, independiente de que el backend ya esté desplegado. Los nombres nuevos siguientes no se presentan como recursos existentes. Preparar los archivos y publicar el PR no autoriza este lote.

| Destino | Cambio propuesto |
|---|---|
| Cloud Run | Crear `visana-keycloak-dev`, `visana-erp-dev`, `us-central1`, gen2; publicar imagen propia en `visana-repo/visana-keycloak-dev` por digest inmutable, sin crear otro repositorio AR |
| PostgreSQL | En `visana-db-dev` crear **keycloak_dev** y **keycloak_app_dev**, usuario integrado sin SUPERUSER/CREATEDB/CREATEROLE/REPLICATION/BYPASSRLS, dueño solo de su nueva base; verificar ausencia de membresías administrativas. Mantener `visana_dev` separada. Keycloak inicializará/migrará su propia BD únicamente tras aprobación |
| Runtime | Crear SA dedicada `visana-keycloak-dev@visana-erp-dev.iam.gserviceaccount.com`; SQL Client en el proyecto; Secret Accessor únicamente sobre los tres secretos inferiores. No usar ni alterar la SA del backend o agente serverless |
| Secret Manager | Responsable provisiona de forma segura `visana-dev-keycloak-db-password:1`, `visana-dev-keycloak-bootstrap-password:1`, `visana-dev-keycloak-health-client-secret:1`; no reutilizar credenciales locales, exports ni conversación |
| Desplegador | Actor histórico candidato `github-actions-dev@visana-erp-dev.iam.gserviceaccount.com`, cuya correspondencia actual aún debe acreditarse. Conceder `actAs` solo sobre la SA nueva al actor verificado y permisos mínimos de publicación/despliegue necesarios; no leer `GCP_CREDENTIALS`, cambiar claves ni activar un CD automático |
| CPU/memoria | Keycloak 2 vCPU/2 GiB, heap 65%, pool DB 1–10; Auth Proxy 1 vCPU/512 MiB: total 3 vCPU/2.5 GiB por instancia, CPU siempre asignada |
| Escalado | Mínimo/máximo **1 a nivel servicio**, concurrencia 20, timeout 300 s, cache local DEV; sin tags ni tráfico dividido. No es HA y el máximo no garantiza ausencia de solapamiento transitorio |
| Hostname y exposición | Origen HTTPS real aprobado, verificado desde `status.url`; el render exige origen y digest. Inicialmente privado por IAM; publicar discovery/token requiere aprobar `allUsers roles/run.invoker` **solo para el nuevo IdP**. El manifiesto no concede ese binding |
| Red | Proxy 2.25.4 fijado por digest, ADC de SA dedicada, loopback 127.0.0.1:5432 hacia instancia existente por PUBLIC. Sin nuevas redes autorizadas, VPC, NAT ni claves ADC. Requiere salida 443/3307 |
| Probes | Proxy startup `/startup` y liveness `/liveness` en 9090; Keycloak espera startup del proxy, startup/readiness `/health/ready` y liveness `/health/live` en 9000. El puerto de entrada es 8080; management no se publica como puerto del servicio |
| Persistencia y coste | Cloud SQL persiste realm, sujetos y claves; nueva BD comparte capacidad/fallo de instancia. Verificar backup/restauración antes de inicializar. Aprobar coste de 3 vCPU/2.5 GiB siempre asignados/min 1, posible solapamiento, DB/conexiones/disco/backups, imagen, logs, secretos y tráfico; no presupuestar como gratuito |

La imagen Keycloak incluye driver PostgreSQL, **no el Connector Java del backend**. El sidecar aplica el transporte Cloud SQL. Tramo JDBC→proxy dentro del mismo sandbox/loopback sin cifrado; tramo proxy→Cloud SQL con TLS/certificados efímeros. No se configuran `sslmode=disable`, certificados inseguros ni desactivación de validación del transporte cloud. Cloud Run termina HTTPS, reenvía a HTTP 8080 con hostname estricto y `proxy-headers=xforwarded`; esos encabezados se confían únicamente al proxy de entrada controlado. [Requisitos oficiales del Auth Proxy](https://docs.cloud.google.com/sql/docs/postgres/sql-proxy), [release 2.25.4](https://github.com/GoogleCloudPlatform/cloud-sql-proxy/releases/tag/v2.25.4), [proxy de Keycloak](https://www.keycloak.org/server/reverseproxy).

**Acceso administrativo propuesto:** inicializar en privado con bootstrap temporal; el responsable autorizado crea administrador permanente con MFA y comprueba acceso antes de publicar el IdP. Acceso privado mediante identidad Google autorizada en `X-Serverless-Authorization` y token Admin Keycloak en `Authorization`, sin capturar valores. Retirar el administrador temporal y las referencias de bootstrap en una revisión posterior controlada; revisar versión/acceso de su secreto mediante aprobación correspondiente. Cuando el servicio sea público, las rutas administrativas comparten el mismo origen y son alcanzables por Internet: IAM de Cloud Run no las separa de token/discovery. No afirmar aislamiento de consola ni MFA ya configurado. Esta exposición y la operación administrativa deben aprobarse expresamente antes de publicar. [Bootstrap temporal](https://www.keycloak.org/server/bootstrap-admin-recovery), [cabeceras de autenticación Cloud Run](https://docs.cloud.google.com/run/docs/authenticating/service-to-service).

**Límites operativos:** cache local y réplica objetivo 1 son una elección DEV sin clustering. Actualizaciones requieren ventana de mantenimiento, backup y revisión de compatibilidad; evitar escrituras administrativas durante solapamiento y no cambiar versión/esquema con dos versiones concurrentes. No usar `import --override` ni migraciones destructivas. El máximo Cloud Run puede excederse brevemente; una configuración min/max no prueba actualización sin solapamiento. [Máximo de instancias](https://docs.cloud.google.com/run/docs/configuring/max-instances), [importación que omite realms existentes](https://www.keycloak.org/server/importExport).

Antecedente documental: la [guía versionada de 26.7.3](https://www.keycloak.org/docs/26.7.3/upgrading/) admite PostgreSQL 18 para aquella versión. La compatibilidad actual preparada de **26.7.5 con PostgreSQL 18.3** fue comprobada en aislamiento por KC-00F; falta validación cloud. Construcción optimizada y health: [contenedores](https://www.keycloak.org/server/containers), [health](https://www.keycloak.org/observability/health). Orden de sidecars/probes: [contenedores Cloud Run](https://docs.cloud.google.com/run/docs/configuring/services/containers), [healthchecks](https://docs.cloud.google.com/run/docs/configuring/healthchecks). El servicio nuevo no fue instalado ni estas probes verificadas en cloud.

**Datos a obtener después del primer despliegue aprobado:** origen HTTPS e issuer reales mediante discovery, JWKS y token firmado del cliente cloud; `azp=visana-dev-health`, audience exclusivamente igual, scope `visana.health` y **sub real persistente**. Verificar contrato antes de cargar las cuatro variables OIDC GitHub y `DEV_HEALTHCHECK_CLIENT_SECRET` por canal seguro. No copiar sujeto, URL HTTP ni credenciales del laboratorio. El secreto cloud del cliente y el que consume GitHub deben corresponder sin mostrarlos. Esa carga y el posterior despliegue del backend requieren autorizaciones correspondientes; no se realizaron aquí.
