# Configuración cloud DEV — VISANA

Corrección del mismo [PR #20](https://github.com/Visanaz/visana/pull/20), Draft. Código y pruebas aisladas autorizados; configuración externa, IAM, recursos, merge y despliegue no aplicados. Guía de uso: [README](../../README.md).

## Evidencia histórica y captura autenticada

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

Versiones resueltas: Spring Boot 3.4.0, Java 21, Spring Security 6.4.1, Hikari 5.1.0, PostgreSQL JDBC 42.7.4. Flyway Core/PostgreSQL/MySQL cambian juntos de 10.20.1 a **11.14.0**. El módulo antiguo declara PostgreSQL probado hasta 17, el nuevo hasta 18: [10.20.1](https://github.com/flyway/flyway/blob/flyway-10.20.1/flyway-database/flyway-database-postgresql/src/main/java/org/flywaydb/database/postgresql/PostgreSQLDatabase.java), [11.14.0](https://github.com/flyway/flyway/blob/flyway-11.14.0/flyway-database/flyway-database-postgresql/src/main/java/org/flywaydb/database/postgresql/PostgreSQLDatabase.java). No se actualizan frameworks ni se modifican migraciones históricas.

Las seis suites usan postgres:18.3-alpine y consultan la versión real JDBC; emiten únicamente POSTGRESQL18_EVIDENCE. CI rechaza omisiones y falta de versión. La suite de ciclo de vida cubre BD vacía, V1–V8, historial, segundo arranque sin reaplicación y salud UP/DOWN/UP ante fallo controlado. El JAR debe contener Connector, driver y Flyway coherentes. Esto acredita PostgreSQL 18 aislado, no Cloud SQL.

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

Secuencia preparada, no ejecutada: identificar usuario/consumidores; cambiar o revocar en PostgreSQL; actualizar consumidores por canal seguro; demostrar rechazo de credencial anterior; revisar copias/accesos sin borrar históricos unilateralmente. Cambiar solo GitHub Secrets no rota PostgreSQL. No restaurar la contraseña expuesta como rollback.

Propuesta externa: almacenar **credencial ya rotada** en secreto/versión numérica aprobados; acreditar secretmanager.versions.access de runtime sobre ese secreto; configurar DEV_DB_PASSWORD_SECRET_REF=nombre:version y DEV_DB_CREDENTIAL_ROTATION_CONFIRMED=true; futura revisión consume DB_PASSWORD por referencia. Si el servicio aún contiene el literal, el despliegue aprobado retirará únicamente esa entrada antes de agregar su referencia en la misma operación; no elimina variables ajenas. [Flags gcloud](https://docs.cloud.google.com/sdk/gcloud/reference/run/deploy). No se inventa nombre/versión ni se crea recurso aquí. CD rechaza referencias flotantes y no transporta literales. La atestación administrativa no demuestra validez.

Diagnósticos proyectan campos permitidos antes de escribir; excluyen env sensibles, argumentos, cuerpos HTTP y excepciones con URL/valores. Pruebas con secretos ficticios cubren discrepancias y errores HTTP/JSON/red. Artefactos publicados: JAR/procedencia y evidencia sanitizada de versiones, sin volcados cloud ni credenciales.

## Pendientes externos concretos

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
