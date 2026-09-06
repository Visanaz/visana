# Registro de decisiones pendientes - Plan 3

Este registro consolida decisiones de negocio y de control que bloquean la evaluacion tecnica. No contiene decisiones tomadas ni arquitectura TO-BE.

| ID | Decision pendiente | Estado | Evidencia de apertura | Impacto de no decidir |
|---|---|---|---|---|
| DG-01 | Vigencia de activacion y recompra | PROVISIONAL_VERBAL_CLARIFICATION | Aclaración verbal VISANA 2026-09-05: trigger `PAYMENT_CONFIRMED`, inicio `paymentConfirmedAt`, duración un mes calendario; sin documento oficial. Vencimiento horario y recompra solapada siguen pendientes. | Foundation evaluable en dev/test; no autoriza consecuencias financieras ni cierre APPROVED. |
| DG-02 | Invitacion de distribuidor: 10% o 3%/5% | SIGUE_ABIERTO | PDF vs `distributor_recruitment_tiers`; sin Java | Regla financiera contractual. |
| DG-03 | Definicion y fuentes de team sales | SIGUE_ABIERTO | Umbrales PDF/SQL; `counts_for_team_sales=0`; sin ruta Java | Calificacion y volumen bloqueados. |
| DG-04 | Aprobacion de comisiones | SIGUE_ABIERTO | PHP `pending/approved/paid`; Java sin puente a ledger | Ledger y liquidacion no cerrados. |
| DG-05 | Ordenes de pago / calendario | SIGUE_ABIERTO | Tabla/PHP parcial; `Payout` Java aislado | Pago y conciliacion bloqueados. |
| DG-06 | Pools y consistencia | SIGUE_ABIERTO | PDF/seeds sin calculador Java | Incentivos de distribuidor bloqueados. |
| DG-07 | Premio adicional 1% L1-L7 | SIGUE_ABIERTO | Solo PDF | Riesgo de omision o duplicacion. |
| DG-08 | Restriccion por rango/rama | SIGUE_ABIERTO | Solo PDF | Elegibilidad y retroactividad no definidas. |
| DG-09 | Base y precedencia de incentivos | SIGUE_ABIERTO | Plan base/overrides SQL; Java mapa sin rol | Fuente de verdad financiera no determinable. |
| DG-10 | Devoluciones/anulaciones | PARCIALMENTE_RESUELTO | `Commission.reverse()` aislado | Alcance financiero/datos no definido. |
| DG-11 | Periodo de activacion y calificacion | PARCIALMENTE_ACLARADO / SIGUE_ABIERTO | Activación: aclaración verbal 2026-09-05 indica mes calendario. Período de calificación/corte sigue sin definición oficial. | La activación usa política provisional; snapshots de calificación requieren política de período explícita. |
| DG-12 | Impuestos / retenciones | SIGUE_ABIERTO | PDF sin implementacion fuente | Payout neto y cumplimiento no definidos. |
| DG-13 | Regla de envio >3 productos | SIGUE_ABIERTO | PDF sin implementacion fuente | Comercio/operacion no determinables. |
| DG-14 | Proveedor de identidad: Keycloak o Identity Platform/Firebase Auth | SIGUE_ABIERTO | Java usa Keycloak/JWT; objetivo comercial contempla Google; no hay plan de migracion de usuarios | Identidad, costos, operacion y claims. |
| DG-15 | Payment provider y contrato de confirmacion | SIGUE_ABIERTO | Java solo simula pago; no hay gateway/webhook | Confirmacion financiera, idempotencia y conciliacion. |
| DG-16 | Fuente frontend o alcance de Angular nuevo | PARTIAL_SOURCE_AVAILABLE / SIGUE_ABIERTO | SRC-009 aporta fuente parcial/artefactos: front controller CodeIgniter, CSS/JS y activos; faltan vistas, rutas, controladores y build. No se cierra por artefacto aislado. | Validar journeys, roles, contratos API, UX y alcance de un frontend nuevo. |
| DG-17 | Cardinalidad Platform Actor a perfiles de negocio | SIGUE_ABIERTO | Sprint 2 introduce actor técnico separado; no existe fuente que relacione actor con Customer/Affiliate/Distributor | Ownership y provisioning de perfiles no pueden inferirse. |
| FR-DG-01 | Aprobación del alcance UX TO-BE | SIGUE_ABIERTO | El legacy es parcial y los journeys completos no son recuperables | Define qué capacidades pasan a frontend nuevo y su prioridad. |
| FR-DG-02 | Límite sitio público versus aplicación autenticada | SIGUE_ABIERTO | Hay activos/contenido público parcial y API actual autenticada | Arquitectura, SEO, rutas, hosting y coexistencia. |
| FR-DG-03 | Elección de design system / librería UI | SIGUE_ABIERTO | CSS Tailwind compilado sin fuente; Bootstrap/jQuery legado heterogéneo | Accesibilidad, mantenimiento, tokens y componentes. |
| FR-DG-04 | Hosting objetivo de frontend | SIGUE_ABIERTO | No existe decisión de Firebase/CDN/Cloud Run ni requisitos operativos | Dominios, headers, rollback, CORS, costo y despliegue. |
| FR-DG-05 | Nivel de paridad visual legado | SIGUE_ABIERTO | Sólo activos/JS parciales; no hay vistas fuente completas | Alcance de migración, UAT y contenido a preservar/rediseñar. |

**Condicion para cerrar un gate:** regla de negocio aprobada por VISANA, fuente de verdad identificada, efectos de datos/finanzas definidos y criterios de prueba acordados. El cierre no se infiere de una semilla SQL, documento o binario aislado.
