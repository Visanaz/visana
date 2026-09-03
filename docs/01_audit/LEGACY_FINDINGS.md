# Hallazgos de legado, brechas y riesgos observados

## Hallazgos

### FND-001 - Dos modelos de datos sin puente demostrable

- **Tipo / severidad:** integridad y trazabilidad / ALTO potencial.
- **Hecho:** SRC-004 contiene 34 tablas, con identificadores enteros y dominios como `users`, `user_genealogy`, `ledger_transactions` y `ordenes_pago_comisiones`. SRC-005 declara tres tablas nuevas con IDs `VARCHAR(36)` para `orders` y `commissions`.
- **Evidencia:** `visanaco_produc.sql:117,142,587,1577,1661`; `V1__init_commerce_and_compensation.sql:1-31`.
- **Estado:** [TRAZABILIDAD_INCOMPLETA].
- **Impacto potencial:** sin correspondencia demostrada, no es posible afirmar preservacion de usuarios, genealogia, ventas, volumen o movimientos financieros entre ambos modelos.
- **Pregunta para VISANA:** Q-DB-001: ¿SRC-004 es la fuente de datos que Plan 3 debe migrar y existe una especificacion aprobada de mapeo y conciliacion?

### FND-002 - Migracion de legado incompleta en el codigo localizado

- **Tipo / severidad:** migracion / ALTO potencial.
- **Hecho:** `LegacyDataMigrationService` construye nodos pero mantiene la llamada de guardado de genealogia comentada; en ordenes indica que ignora items y solo intenta confirmar estado antes de guardar.
- **Evidencia:** `LegacyDataMigrationService.java:31-35,54-64`.
- **Estado:** [PARCIALMENTE_IMPLEMENTADA].
- **Impacto potencial:** la traza de una migracion completa y conciliada no esta demostrada.
- **Pregunta para VISANA:** Q-MIG-001: ¿este servicio es prototipo, herramienta aprobada o componente activo de migracion?

### FND-003 - Dependencias de red y compensacion sin adaptador localizado

- **Tipo / severidad:** arquitectura / ALTO potencial.
- **Hecho:** el flujo requiere proveedores de genealogia, calificacion y plan; no se localizaron adaptadores de infraestructura para ellos. `NetworkNodeRepository` es importado, pero no se localizo su archivo fuente en el paquete referenciado.
- **Evidencia:** `CalculateCommissionsService.java:5-8,49`; `CreateOrderService.java:15`; `CreateNetworkNodeService.java:6`; `UseCaseBeanConfig.java:61,68`.
- **Estado:** [TRAZABILIDAD_INCOMPLETA].
- **Impacto potencial:** la inyeccion y persistencia completas de red/calificacion/plan no pueden demostrarse; el arranque permanece PENDIENTE de validacion.
- **Pregunta para VISANA:** Q-ARCH-001: ¿estos puertos/adaptadores existen en otra rama, modulo o artefacto que deba incorporarse como fuente?

### FND-004 - Confirmacion de pago sin integracion de recaudo localizada

- **Tipo / severidad:** financiera / ALTO potencial.
- **Hecho:** el endpoint `/pay` construye `ConfirmOrderCommand`; su descripcion OpenAPI dice que simula la confirmacion y el servicio pasa la orden a `PAID` y publica el evento local. No se localizo gateway, webhook, firma, conciliacion ni adaptador de pago en SRC-001.
- **Evidencia:** `OrderController.java:52-68`; `ConfirmOrderPaymentService.java:29-34`.
- **Estado:** [PARCIALMENTE_IMPLEMENTADA].
- **Impacto potencial:** no se puede demostrar origen verificable de una confirmacion que genera comisiones.
- **Pregunta para VISANA:** Q-PAY-001: ¿que proveedor, webhook y controles de conciliacion deben autorizar la transicion a `PAID`?

### FND-005 - Regla de compensacion documentada, configuracion legada y motor nuevo deben contrastarse

- **Tipo / severidad:** regla de negocio / ALTO potencial.
- **Hecho:** el PDF documenta activacion COP 200.000, recompra COP 100.000, red hasta nivel 8, porcentajes por nivel, periodos y pagos. El SQL legado contiene `commission_plans`, niveles y calificaciones por rol. El motor nuevo usa un mapa de porcentajes externo y un maximo de ocho niveles, pero el proveedor del mapa no esta localizado.
- **Evidencia:** SRC-003 paginas 1-4; `visanaco_produc.sql:117-188,1151-1190`; `UnilevelCompensationCalculatorService.java:31,59-93`.
- **Estado:** [DOCUMENTADA_NO_VERIFICADA] para las reglas del PDF en el flujo nuevo; [TRAZABILIDAD_INCOMPLETA] para el origen del plan nuevo.
- **Impacto potencial:** no es posible afirmar que los porcentajes, requisitos, pools, retenciones, cortes o pagos del documento se apliquen en el clon.
- **Pregunta para VISANA:** Q-COMP-001: ¿cual fuente y version del plan esta aprobada para Plan 3, y que matriz de reglas debe prevalecer tras validacion de negocio?

### FND-006 - Superficie de seguridad y secretos de desarrollo

- **Tipo / severidad:** seguridad / MEDIO potencial.
- **Hecho:** la aplicacion declara JWT/Keycloak y autentica `/api/v1/**`; Swagger queda publico. Compose contiene una credencial de base de datos y los archivos de configuracion/export contienen marcadores de secreto.
- **Evidencia:** `SecurityConfig.java:24-32`; `docker-compose.yml`; `application.properties`; `visana-realm-export.json`.
- **Estado:** [COMPORTAMIENTO_OBSERVADO_FUNCIONAMIENTO_NO_VALIDADO] para configuracion; `[SECRETO_PRESENTE_NO_REPRODUCIDO]`.
- **Impacto potencial:** la seguridad efectiva depende del despliegue y de secretos no auditados activamente. La credencial versionada debe ser tratada como informacion sensible hasta que VISANA determine su vigencia.
- **Pregunta para VISANA:** Q-SEC-001: ¿estos secretos son exclusivamente de desarrollo y existe rotacion/control de distribucion verificado?

### FND-007 - Pruebas presentes no sustituyen validacion de integracion

- **Tipo / severidad:** calidad / MEDIO potencial.
- **Hecho:** 19 reportes Surefire versionados declaran 102 pruebas exitosas. `pom.xml` excluye `**/*IT.java` del Surefire y la auditoria no ejecuto Maven, Docker ni Testcontainers.
- **Evidencia:** `target/surefire-reports/TEST-*.xml`; `pom.xml` configuracion Surefire/Failsafe.
- **Estado:** [COMPORTAMIENTO_OBSERVADO_FUNCIONAMIENTO_NO_VALIDADO].
- **Impacto potencial:** no valida arranque con dependencias faltantes, Keycloak, MySQL, Flyway, ni flujo extremo a extremo.
- **Pregunta para VISANA:** Q-QA-001: ¿que ambiente y datos controlados deben usarse para una validacion posterior de integracion?

### FND-008 - Artefactos compilados y fuente actual no son trazables uno a uno

- **Tipo / severidad:** integridad de repositorio / ALTO potencial.
- **Hecho:** `target/classes` versionado contiene `NetworkNodeRepository.class` y `NetworkNodePersistenceAdapter.class`; el arbol `src/main/java` y `git ls-tree HEAD` no contienen sus fuentes. El fuente actual importa `NetworkNodeRepository` en comercio, red y configuracion.
- **Evidencia:** `CreateOrderService.java:15`, `CreateNetworkNodeService.java:6`, `UseCaseBeanConfig.java:61,68`; inventario de `target/classes`; `git ls-tree -r HEAD`.
- **Estado:** [CONTRADICCION].
- **Impacto potencial:** no puede establecerse que el JAR, los reportes Surefire y el fuente vigente procedan de la misma compilacion reproducible.
- **Pregunta para VISANA:** Q-BLD-001: ¿cual commit y pipeline produjeron los artefactos versionados, y deben estos estar bajo control de versiones?

### FND-009 - Aislamiento tenant/recurso no demostrado en las rutas financieras y de red

- **Tipo / severidad:** autorizacion / ALTO potencial.
- **Hecho:** la creacion de orden usa el tenant del JWT pero recupera el afiliado solamente por ID y no compara tenant. La confirmacion de pago no usa el tenant del JWT. La creacion de nodo valida estado del patrocinador, sin comparacion de tenant. No se localizaron anotaciones de autorizacion por rol en el fuente inspeccionado.
- **Evidencia:** `OrderController.java:40-65`; `CreateOrderService.java:33-42`; `ConfirmOrderPaymentService.java:24-34`; `CreateNetworkNodeService.java:27-50`; `SecurityConfig.java:27-28`.
- **Estado:** [TRAZABILIDAD_INCOMPLETA].
- **Impacto potencial:** si los repositorios admiten IDs de distintos tenants, el control de recursos podria depender de logica no localizada. Esta auditoria no intento demostrar acceso indebido.
- **Pregunta para VISANA:** Q-SEC-002: ¿cual es la regla aprobada de pertenencia tenant/rol para crear ordenes, confirmar pagos y asociar patrocinadores?

### FND-010 - Fechas y periodo sentinela presentes en la exportacion legada

- **Tipo / severidad:** integridad de datos / MEDIO potencial.
- **Hecho:** el dump contiene siete ocurrencias de `0000-00-00 00:00:00`, una fila de volumen de distribuidor con periodo `1969-12` y una venta con fecha de creacion sentinela.
- **Evidencia:** `visanaco_produc.sql:287,811-812,1308`; conteo estatico sobre SRC-004.
- **Estado:** [COMPORTAMIENTO_OBSERVADO_FUNCIONAMIENTO_NO_VALIDADO].
- **Impacto potencial:** estas claves temporales podrian afectar ordenamiento, corte, agregacion por periodo o conciliacion si se consumen como datos validos.
- **Pregunta para VISANA:** Q-DATA-001: ¿como deben clasificarse y tratarse los registros con fechas/períodos sentinela durante una conciliacion de datos?

### FND-011 - Configuracion por rol altera la escala de plan base

- **Tipo / severidad:** regla de compensacion / ALTO potencial.
- **Hecho:** `commission_plan_levels` define la escala base 15/10/5/4/3/2/1/2. `role_level_qualifications` declara para roles internos 1 y 2 valores personalizados 10/5/4/3/2/1/1/1 y sin requisito de calificacion; para rol 3 registra requisitos por nivel.
- **Evidencia:** `visanaco_produc.sql:142-168,1151-1190`.
- **Estado:** [CONTRADICCION] entre escala base/documental y configuracion por rol, pendiente de identificar el significado funcional de los IDs de rol y su precedencia.
- **Impacto potencial:** no es posible determinar con la evidencia actual que porcentaje/regla recibe cada categoria de participante.
- **Pregunta para VISANA:** Q-COMP-002: ¿que rol funcional representa cada ID y cuando una configuracion por rol debe sustituir el plan Unilevel base?

### FND-012 - Flujo documentado y orquestacion implementada no coinciden completamente

- **Tipo / severidad:** trazabilidad de arquitectura / ALTO potencial.
- **Hecho:** `.agents/rules/visana.md` documenta `PAYMENT CONFIRMED -> VOLUME ENGINE -> QUALIFICATION -> GENEALOGY -> COMPENSATION -> LEDGER -> SETTLEMENT -> PAYOUT`. El fuente enlaza `OrderPaidEvent` directamente al listener de compensacion; no se localizaron los componentes de volumen, ledger o settlement en esa cadena.
- **Evidencia:** `.agents/rules/visana.md`; `ConfirmOrderPaymentService.java:29-34`; `OrderPaidEventListener.java:17-19`; `CalculateCommissionsService.java:49-77`.
- **Estado:** [CONTRADICCION].
- **Impacto potencial:** el momento en que se calcula volumen, se evalua calificacion y se registra el ledger no es trazable extremo a extremo en el clon.
- **Pregunta para VISANA:** Q-ARCH-002: ¿la secuencia documentada es la requerida para Plan 3 y que componentes/ eventos faltan por incorporar como fuentes verificables?

### FND-013 - Documentacion del calculador afirma una validacion no localizable en su firma

- **Tipo / severidad:** consistencia documental / MEDIO potencial.
- **Hecho:** `.agents/modules/compensation-domain.md` indica que el calculador valida que una `Order` este `PAID`. La firma actual recibe `OrderId`, total, upline, calificaciones y plan, pero no una orden ni su estado. El test que menciona orden `PENDING` se encuentra comentado.
- **Evidencia:** `.agents/modules/compensation-domain.md`; `UnilevelCompensationCalculatorService.java:43-93`; `UnilevelCompensationCalculatorServiceTest.java:216-226`.
- **Estado:** [CONTRADICCION].
- **Impacto potencial:** la garantia de que solo una orden pagada origine comisiones depende de la orquestacion previa y no queda demostrada dentro del calculador documentado.
- **Pregunta para VISANA:** Q-COMP-003: ¿en que capa debe verificarse formalmente el estado pagado y cual es la prueba aprobada que lo demuestra?

## Funcionalidades no evidenciadas en el codigo nuevo

No se localizaron modulos fuente para catalogo completo, carrito, POS, reporting, PQR, email/notificaciones, pools, bonos de consistencia, volumen de equipo, integracion de pagos, ordenes de pago persistentes o frontend. Esta lista no afirma inexistencia fuera de SRC-001.

## Detencion

No se corrigieron los hallazgos ni se propuso arquitectura TO-BE. Los puntos anteriores requieren validacion de VISANA antes de una fase de reingenieria.
