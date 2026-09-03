# Inventario tecnico del clon

## Stack confirmado mediante evidencia

| Componente | Evidencia | Estado |
|---|---|---|
| Java 21 / Maven | `pom.xml` (`java.version=21`) | [IMPLEMENTADA] como configuracion de build |
| Spring Boot 3.4.0 | `pom.xml` parent | [IMPLEMENTADA] como dependencia declarada |
| Spring Web, Data JPA, Validation | `pom.xml` | [IMPLEMENTADA] como dependencias declaradas |
| MySQL 8.0 en Compose | `docker-compose.yml` | [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO] |
| Flyway | `pom.xml`; `src/main/resources/db/migration/V1__init_commerce_and_compensation.sql` | [PARCIALMENTE_IMPLEMENTADA] |
| OAuth2 Resource Server / Keycloak | `pom.xml`, `application.properties:16`, `SecurityConfig.java` | [IMPLEMENTADA] como configuracion estatica |
| OpenAPI / Swagger | `pom.xml`, `SecurityConfig.java:27` | [IMPLEMENTADA] como dependencia y ruta publica declarada |
| Docker Compose | `docker-compose.yml` | [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO] |
| CI/CD | No se localizaron manifiestos de pipeline en el arbol inspeccionado | [NO_EVIDENCIADO] |
| Frontend propio | No localizado | [NO_EVIDENCIADO] en SRC-001 |

## Modulos de codigo propio

| Dominio | Contenido localizado | Clasificacion |
|---|---|---|
| `commerce` | Orden, items, puertos, casos de uso, controller y adaptador JPA | [PARCIALMENTE_IMPLEMENTADA] |
| `compensation` | Comision Unilevel, servicio de calculo, listener de evento y persistencia de comision | [PARCIALMENTE_IMPLEMENTADA] |
| `network` | Nodo genealogico, sponsor, roles/estado, caso de uso y controller | [PARCIALMENTE_IMPLEMENTADA] |
| `qualification` | Metricas, reglas de nivel y evaluador puro | [PARCIALMENTE_IMPLEMENTADA] |
| `ledger` | Objetos de cuenta/transaccion/payout y `SettlementService` | [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO] |
| `migration` | DTOs y servicio de migracion legado | [PARCIALMENTE_IMPLEMENTADA] |
| `core` | Value objects, eventos, configuracion, seguridad y manejo global de excepciones | [IMPLEMENTADA] como estructura de soporte |

## Dominios obligatorios - evidencia en este clon

| Dominio | Estado | Evidencia / limite |
|---|---|---|
| Identidad y roles | [PARCIALMENTE_IMPLEMENTADA] | Realm con roles y JWT converter; no hay dominio de usuario local localizado. |
| Clientes, catalogo, productos, carrito, POS | [NO_EVIDENCIADO] | `Product` es modelo de valor dentro de orden; no hay catalogo/carrito/POS persistente localizado. |
| Ordenes/ventas | [PARCIALMENTE_IMPLEMENTADA] | Dos POST `/api/v1/orders`; no hay gateway de pago. |
| Afiliacion/genealogia | [PARCIALMENTE_IMPLEMENTADA] | Modelo y POST de nodo; falta puerto de red localizado. |
| Activacion/recompra/calificacion | [PARCIALMENTE_IMPLEMENTADA] | Evaluador parametrico; faltan adaptadores, almacenamiento y disparador en flujo. |
| Volumen/team sales | [NO_EVIDENCIADO] en codigo nuevo | Solo estructuras en SQL legado. |
| Comisiones/niveles | [PARCIALMENTE_IMPLEMENTADA] | Calculo Unilevel maximo 8 y persistencia; faltan proveedores del plan, genealogia y calificacion. |
| Ledger/liquidacion/orden de pago/pago | [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO] | Modelos y servicio de settlement sin endpoint/adaptador/persistencia localizada. |
| Pools/bonos de consistencia | [NO_EVIDENCIADO] en codigo nuevo | Tablas en SRC-004 sin flujo nuevo demostrado. |
| Reportes, PQR, notificaciones, email | [NO_EVIDENCIADO] | No se localizaron modulos fuente. |

## Persistencia y datos

La migracion Flyway nueva declara `orders`, `order_items` y `commissions`; solo `order_items.order_id` tiene FK declarada (`V1__init_commerce_and_compensation.sql:1-31`). El dump legado contiene 34 tablas, 31 FKs y 5 restricciones UNIQUE localizadas mediante inspeccion estatica. Entre sus dominios aparecen usuarios, roles, genealogia, volumen, planes, ledger, ventas retail y ordenes de pago.

No se demostro que Flyway se haya ejecutado, que la migracion corresponda al dump legado ni que una instancia de MySQL contenga alguno de estos esquemas.

## Pruebas disponibles

Hay 19 clases `*Test.java` y 19 XML Surefire incluidos en `target/surefire-reports`; los XML declaran en conjunto 102 pruebas, 0 errores, 0 fallos y 0 omitidas. [COMPORTAMIENTO_OBSERVADO_FUNCIONAMIENTO_NO_VALIDADO]: son artefactos ya presentes y versionados; esta auditoria no los ejecuto ni verifico servicios externos.

Los artefactos `target/classes` contienen clases de red y persistencia para las que no hay fuente equivalente en `HEAD`. En consecuencia, los reportes no permiten afirmar que las 102 pruebas correspondan exactamente al arbol fuente que se audita hoy.

## Matriz de reglas - documento, SQL legado y codigo nuevo

| ID | Regla | PDF (SRC-003) | SQL legado (SRC-004) | Java nuevo (SRC-001) | Estado |
|---|---|---|---|---|---|
| BR-001 | Activacion inicial y vigencia | COP 200.000; 30 dias | Seed: `activation_amount=200000`, `activation_duration_days=30` | Evaluador recibe monto y dias como parametros; no se localizo proveedor ni flujo | [PARCIALMENTE_IMPLEMENTADA] |
| BR-002 | Recompra minima | COP 100.000 mensual | Seed: `min_recompra_amount=100000` | No se localizo regla de recompra ni persistencia de su estado | [DOCUMENTADA_NO_VERIFICADA] en codigo nuevo |
| BR-003 | Unilevel ocho niveles | 15/10/5/4/3/2/1/2 | Seed de `commission_plan_levels` con los mismos ocho valores | Profundidad maxima 8 y mapa externo de porcentajes; proveedor no localizado | [TRAZABILIDAD_INCOMPLETA] |
| BR-004 | Calificacion por directos, indirectos y ventas de equipo | Tabla de requisitos por nivel | JSON en `role_level_qualifications` con los umbrales de niveles 1-8 | `QualificationEvaluatorService` y `LevelQualificationRule` son genericos; sin carga/persistencia localizada | [PARCIALMENTE_IMPLEMENTADA] |
| BR-005 | Pools y bonos de consistencia de distribuidores | 15M/25M/45M/100M; 6 y 12 meses | Seeds en `distributor_sales_pools` y `distributor_consistency_bonuses` | No localizado | [ESTRUCTURA_EXISTENTE_USO_NO_VERIFICADO] en SQL; [NO_EVIDENCIADO] en Java |
| BR-006 | Corte, pago e impuestos | Cortes 1-15 y 16-fin; pagos 25 y 10; retenciones | `periods`, `ordenes_pago_comisiones` y ledger existen | `Payout`/`SettlementService` aislados; no hay calendario, orden de pago ni impuestos | [DOCUMENTADA_NO_VERIFICADA] |

La coincidencia de valores entre PDF y semillas SQL no demuestra que esos valores hayan sido aprobados ni que se ejecuten en una instancia actual.

### Variacion por rol localizada en SRC-004

La escala base del plan SQL coincide con el PDF. Sin embargo, `role_level_qualifications` define para los roles internos `1` y `2` `custom_payout_value` 10/5/4/3/2/1/1/1 y `requires_qualification=0`, mientras que para el rol `3` registra los umbrales de red/ventas. La propia tabla documenta que un `custom_payout_value` no nulo sustituye al valor por defecto. No se localizo el catalogo funcional que asocie esos IDs con las categorias del PDF, ni codigo Java que cargue esta configuracion. [TRAZABILIDAD_INCOMPLETA].

## Calidad de datos observada en el dump legado

La exportacion contiene siete ocurrencias de fecha/hora `0000-00-00 00:00:00` y una fila de `distributor_monthly_volumes` con periodo `1969-12`; tambien hay una venta con fecha de creacion sentinela. Ejemplos: `visanaco_produc.sql:287,811-812,1308`. Esto es HECHO sobre la copia SQL, no evidencia de que los datos esten hoy en produccion ni prueba de causa. Debe considerarse al definir una conciliacion futura.

## Informacion sensible

`docker-compose.yml`, `application.properties` y `visana-realm-export.json` contienen 9 marcadores de secreto/credencial localizados por patron. [SECRETO_PRESENTE_NO_REPRODUCIDO]. La presencia de estos archivos no prueba validez de las credenciales.
