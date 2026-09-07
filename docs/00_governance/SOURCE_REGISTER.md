# Registro de fuentes

| ID | Fuente | Tipo | Estado de uso | Evidencia y limites |
|---|---|---|---|---|
| SRC-001 | `C:\visana_auditoria\visana` | Repositorio Git nuevo | Inspeccionado | HEAD y arbol descritos en `REPO_BASELINE.md`. Fuente principal de codigo actual. |
| SRC-002 | Auditoria anterior `AUDIT_MASTER v2.2` | Expediente historico | Referencia contextual | Declarada por el master prompt. No se presume equivalencia ni se revalida su ejecucion en este clon. |
| SRC-003 | `VISANA SAS - Plan de Compensacion-3.pdf` | Documento funcional, 4 paginas | Inspeccionado | Archivo no trackeado. Contiene reglas documentadas de activacion, recompra, red y pagos; no prueba implementacion en SRC-001. |
| SRC-004 | `visanaco_produc.sql` | Export SQL legado | Inspeccionado | 34 tablas, 31 `FOREIGN KEY` y datos de ejemplo/volcado. La existencia de la exportacion no demuestra que sea la BD activa. |
| SRC-005 | `src/main/resources/db/migration/V1__init_commerce_and_compensation.sql` | Migracion Flyway | Inspeccionado | Declara tres tablas nuevas. No se ejecuto ni se verifico contra una instancia. |
| SRC-006 | `pom.xml`, `docker-compose.yml`, `src/main/resources/application.properties`, `visana-realm-export.json` | Build, infraestructura y seguridad | Inspeccionado | Secretos identificados sin reproducir. No se levantaron servicios. |
| SRC-007 | `src/test/java` y `target/surefire-reports` | Pruebas y reportes generados | Inspeccionado estaticamente | Hay 19 clases de prueba y 19 XML Surefire versionados que declaran 102 pruebas, 0 fallos/errores. No fueron reejecutados en esta auditoria. |
| SRC-008 | `.agents/rules/*.md` y `.agents/modules/*.md` | Reglas y documentacion tecnica interna | Inspeccionado | Describe arquitectura Clean/DDD, flujo objetivo y modulos. Es documentacion de intencion; no acredita que el codigo actual ejecute lo descrito. |
| SRC-009 | `C:\visana_auditoria\visana_frontend` | Entrega frontend legado parcial | Inspeccionado estaticamente | 242 archivos sin Git; contiene front controller CodeIgniter, configuracion Apache, CSS/JS y activos, pero no vistas, rutas, controladores ni manifiesto de build. Ver `docs/07_frontend_audit/FRONTEND_SOURCE_REGISTER.md`. |
| SRC-010 | Respuesta oficial de Google Forms de Daniel Reina | Decisión funcional digital oficial | Fuente funcional vigente | Respondente: Daniel Reina; rol: Gerente Financiero; área: Gerencia y Contabilidad; respuesta 2026-09-05; timestamp 2026-09-07 09:34:03; aprobación declarada para todo el formulario. `DIGITAL_FUNCTIONAL_DECISION`; `PHYSICAL_RATIFICATION=PENDING`; `EFFECTIVE_FROM_PENDING`. El original externo se referencia de forma inmutable y no se modifica. |
| SRC-011 | Documento manual diligenciado por Gerencia | Evidencia funcional histórica | Conservado como soporte | Permanece válido como evidencia histórica, pero no reemplaza SRC-010 cuando existe conflicto. No se modifica ni sobrescribe. |

## Reglas de contraste

1. SRC-001 no sustituye a SRC-003 ni a SRC-004: codigo, documento y SQL describen capas distintas.
2. SRC-002 sirve para detectar diferencias estructurales, no para confirmar comportamiento de este clon.
3. Un dato o regla solo se considera validado cuando exista evidencia de ejecucion y criterio de aceptacion; esta fase no produjo esa evidencia.
4. Para la reconciliación del 2026-09-07, SRC-010 tiene precedencia funcional sobre SRC-011, fuentes verbales, código legado e inferencia técnica.
5. La respuesta de prueba atribuida a Cristian se excluye por instrucción expresa y no se registra como fuente de negocio.

## Referencias concretas

- SRC-003: PDF, paginas 1-4; metadatos: 4 paginas, sin cifrado ni formulario.
- SRC-004: `commission_plans` linea 117, `commission_plan_levels` linea 142, `ledger_transactions` linea 587, `users` linea 1577 y `user_genealogy` linea 1661.
- SRC-005: tablas `orders`, `order_items` y `commissions`, lineas 1, 10 y 20.
- SRC-006: JPA en modo `validate` y emisor JWT en `application.properties:11,16`; reglas HTTP en `SecurityConfig.java:24-32`.
- SRC-008: `.agents/rules/architecture-rules.mcp.md`, `.agents/rules/business-logic.mcp.md` y modulos documentales; sus discrepancias con el fuente se registran en `LEGACY_FINDINGS.md`.
- SRC-009: `index.php`, `.htaccess`, 18 archivos JavaScript, 2 CSS, activos y `package-lock.json`; las llamadas JavaScript son referencias estáticas y no fueron invocadas.
