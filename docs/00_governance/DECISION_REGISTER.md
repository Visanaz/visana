# Registro de decisiones pendientes - Plan 3

Este registro consolida el estado evidenciario vigente, reconciliado el 2026-09-06. Un estado provisional permite modelado y pruebas controladas; no equivale a aprobación de VISANA ni autoriza efectos financieros o productivos.

| ID | Decisión | Estado coherente | Fuente y fecha | Aprobación VISANA | Pendiente explícito |
|---|---|---|---|---|---|
| DG-01 | Vigencia de activación y recompra | `PROVISIONAL_VERBAL_CLARIFICATION` | Aclaración verbal atribuida a Catherine, 2026-09-05; Working Baseline provisional y foundation Sprint 5 | `NO_APROBADA` | Documento oficial; hora exacta de vencimiento; recompra solapada. Trigger provisional: `PAYMENT_CONFIRMED`; inicio `paymentConfirmedAt`; duración `plusMonths(1)`. |
| DG-02 | Invitación de distribuidor: 10% o 3%/5% | `SIGUE_ABIERTO` | SRC-003 PDF frente a `distributor_recruitment_tiers`; auditoría previa, vigente a 2026-09-05 | `NO_APROBADA` | Porcentaje oficial, elegibilidad, período y base. |
| DG-03 | Definición y fuentes de Team Sales | `SIGUE_ABIERTO` | SRC-003/SQL y aclaración verbal 2026-09-05 recogida en Working Baseline | `NO_APROBADA` | La hipótesis propia + todos los descendientes es provisional; faltan venta calificable, estados, ajustes y fuente oficial. |
| DG-04 | Aprobación de comisiones | `SIGUE_ABIERTO` | PHP histórico `pending/approved/paid` y Java aislado; evidencia vigente a 2026-09-05 | `NO_APROBADA` | Actor, controles y momento de aprobación. |
| DG-05 | Órdenes de pago y calendario | `SIGUE_ABIERTO` | SRC-003 y estructuras históricas/Java aisladas; evidencia vigente a 2026-09-05 | `NO_APROBADA` | Ciclo completo, responsables, fallos, reintentos y feriados. |
| DG-06 | Pools y consistencia | `SIGUE_ABIERTO` | SRC-003 y semillas sin calculador Java; evidencia vigente a 2026-09-05 | `NO_APROBADA` | Base, participantes, escalonamiento y reglas de racha 6/12 meses. |
| DG-07 | Premio adicional 1% L1-L7 | `SIGUE_ABIERTO` | SRC-003; evidencia vigente a 2026-09-05 | `NO_APROBADA` | Si es adicional, equivalente o condicionado; elegibilidad y base. |
| DG-08 | Restricción por rango/rama | `SIGUE_ABIERTO` | SRC-003; evidencia vigente a 2026-09-05 | `NO_APROBADA` | Beneficios afectados, inicio, reactivación e historia. |
| DG-09 | Base y precedencia de incentivos | `PROVISIONAL_VERBAL_CLARIFICATION` | Aclaración verbal atribuida a Catherine, 2026-09-05; Working Baseline provisional | `NO_APROBADA` | Fórmula oficial, tratamiento de envío/ajustes/devoluciones y precedencia. Para qualification se usa provisionalmente valor después de descuentos y antes de impuestos. |
| DG-10 | Devoluciones, anulaciones y reversos | `PARCIALMENTE_RESUELTO` | `Commission.reverse()` aislado; evidencia vigente a 2026-09-05 | `NO_APROBADA` | Alcance end-to-end antes/después de pago, saldo negativo y autorización. |
| DG-11 | Período de calificación | `SIGUE_ABIERTO` | Aclaración verbal 2026-09-05 sólo cubre duración de activación; Working Baseline y Sprint 5 | `NO_APROBADA` | Elegir y documentar período de calificación: calendario, rolling, quincenal u otro. No se infiere de DG-01. |
| DG-12 | Impuestos y retenciones | `SIGUE_ABIERTO` | SRC-003 sin matriz implementable; evidencia vigente a 2026-09-05 | `NO_APROBADA` | Matriz contable oficial, tasas, bases, sujetos y vigencias. |
| DG-13 | Regla de envío por más de tres productos | `SIGUE_ABIERTO` | SRC-003 sin implementación fuente; evidencia vigente a 2026-09-05 | `NO_APROBADA` | Unidades/SKU/líneas, destinos, topes y excepciones. |
| DG-14 | Proveedor de identidad | `SIGUE_ABIERTO` | Java usa Keycloak/JWT; objetivo comercial contempla Google; evidencia vigente a 2026-09-05 | `NO_APROBADA` | Decisión de proveedor y, si aplica, plan de migración. |
| DG-15 | Proveedor y contrato de confirmación de pago | `SIGUE_ABIERTO` | Java simula pago; no hay gateway/webhook; evidencia vigente a 2026-09-05 | `NO_APROBADA` | Proveedor, checkout, webhook, reembolso, sandbox y conciliación. |
| DG-16 | Fuente frontend o alcance nuevo | `SIGUE_ABIERTO` | SRC-009 parcial, 2026-09-05: front controller CodeIgniter, CSS/JS y activos; faltan vistas, rutas, controladores y build | `NO_APROBADA` | Confirmar fuente recuperable, alcance UX y estrategia del frontend. |
| DG-17 | Cardinalidad Platform Actor a perfiles | `SIGUE_ABIERTO` | Foundation Sprint 2 y ausencia de matriz de negocio; evidencia vigente a 2026-09-05 | `NO_APROBADA` | Combinaciones permitidas y provisioning/ownership. |
| DG-18 | Política de cambio de patrocinador | `PROVISIONAL_VERBAL_CLARIFICATION` | Aclaración verbal de alta confianza suministrada para reconciliación, 2026-09-05; Sprint 4 mantiene rechazo seguro | `NO_APROBADA` | Provisional: sólo administración, con justificación y auditoría. Sigue abierta la semántica de movimiento de subárbol, vigencia e historia financiera. |
| DG-19 | Autoridad de provisioning de afiliación | `SIGUE_ABIERTO` | SRC-003 y foundation Sprint 4; evidencia vigente a 2026-09-05 | `NO_APROBADA` | Evento/actor autorizador, controles y relación con registro, compra o pago. |

## Decisiones frontend relacionadas

| ID | Decisión | Estado | Fuente y fecha | Aprobación VISANA | Pendiente explícito |
|---|---|---|---|---|---|
| FR-DG-01 | Alcance UX TO-BE | `SIGUE_ABIERTO` | Auditoría frontend parcial, 2026-09-05 | `NO_APROBADA` | Capacidades, journeys y prioridad. |
| FR-DG-02 | Sitio público vs aplicación autenticada | `SIGUE_ABIERTO` | Auditoría frontend parcial, 2026-09-05 | `NO_APROBADA` | Límites, rutas, SEO, hosting y coexistencia. |
| FR-DG-03 | Design system / librería UI | `SIGUE_ABIERTO` | CSS/JS legado parcial, 2026-09-05 | `NO_APROBADA` | Tecnología, accesibilidad, tokens y componentes. |
| FR-DG-04 | Hosting objetivo frontend | `SIGUE_ABIERTO` | Sin decisión evidenciada a 2026-09-05 | `NO_APROBADA` | Plataforma, dominios, CORS, rollback, costo y despliegue. |
| FR-DG-05 | Paridad visual legado | `SIGUE_ABIERTO` | Activos parciales sin vistas completas, 2026-09-05 | `NO_APROBADA` | Alcance de preservación/rediseño y UAT. |

**Condición para cerrar un gate:** regla aprobada por VISANA, fuente de verdad identificada, efectos de datos/finanzas definidos y criterios de prueba acordados. Ninguna fila de este registro está `APPROVED`.

## Clarificación documental previa a Frontend F5 — actualizada 2026-09-07

La clasificación y el impacto de DG-01, DG-03, DG-09, DG-11, DG-18 y DG-19 para F5 se documentan sin crear entradas duplicadas en `docs/02_business/F5_BUSINESS_RULES_DECISION_GATE.md`. El instrumento de respuesta está en `docs/02_business/F5_CLIENT_DECISION_PACKAGE.md` y la secuencia de entrega en `docs/05_delivery/F5_READINESS.md`.

Resultado técnico: `READ_ONLY_SKELETON_ONLY`. Decisión de ejecución del PM: `WAIT_FOR_CLIENT_DECISIONS`. Autorización de implementación: `NOT_AUTHORIZED`. Esta secuencia no crea ni aprueba una regla de negocio; el conteo `APPROVED` permanece en **0**.
