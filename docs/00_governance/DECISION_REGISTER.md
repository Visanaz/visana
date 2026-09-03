# Registro de decisiones pendientes - Plan 3

Este registro consolida decisiones de negocio y de control que bloquean la evaluacion tecnica. No contiene decisiones tomadas ni arquitectura TO-BE.

| ID | Decision pendiente | Estado | Evidencia de apertura | Impacto de no decidir |
|---|---|---|---|---|
| DG-01 | Vigencia de activacion y recompra | SIGUE_ABIERTO | PDF mes/30 dias; SQL 30; PHP 29/current-month; Java parametrico | Activacion, calificación y comisiones no determinables. |
| DG-02 | Invitacion de distribuidor: 10% o 3%/5% | SIGUE_ABIERTO | PDF vs `distributor_recruitment_tiers`; sin Java | Regla financiera contractual. |
| DG-03 | Definicion y fuentes de team sales | SIGUE_ABIERTO | Umbrales PDF/SQL; `counts_for_team_sales=0`; sin ruta Java | Calificacion y volumen bloqueados. |
| DG-04 | Aprobacion de comisiones | SIGUE_ABIERTO | PHP `pending/approved/paid`; Java sin puente a ledger | Ledger y liquidacion no cerrados. |
| DG-05 | Ordenes de pago / calendario | SIGUE_ABIERTO | Tabla/PHP parcial; `Payout` Java aislado | Pago y conciliacion bloqueados. |
| DG-06 | Pools y consistencia | SIGUE_ABIERTO | PDF/seeds sin calculador Java | Incentivos de distribuidor bloqueados. |
| DG-07 | Premio adicional 1% L1-L7 | SIGUE_ABIERTO | Solo PDF | Riesgo de omision o duplicacion. |
| DG-08 | Restriccion por rango/rama | SIGUE_ABIERTO | Solo PDF | Elegibilidad y retroactividad no definidas. |
| DG-09 | Base y precedencia de incentivos | SIGUE_ABIERTO | Plan base/overrides SQL; Java mapa sin rol | Fuente de verdad financiera no determinable. |
| DG-10 | Devoluciones/anulaciones | PARCIALMENTE_RESUELTO | `Commission.reverse()` aislado | Alcance financiero/datos no definido. |
| DG-11 | Mes calendario vs 30 dias rolling | SIGUE_ABIERTO | Variante especifica de DG-01 | Casos limite de vigencia. |
| DG-12 | Impuestos / retenciones | SIGUE_ABIERTO | PDF sin implementacion fuente | Payout neto y cumplimiento no definidos. |
| DG-13 | Regla de envio >3 productos | SIGUE_ABIERTO | PDF sin implementacion fuente | Comercio/operacion no determinables. |
| DG-14 | Proveedor de identidad: Keycloak o Identity Platform/Firebase Auth | SIGUE_ABIERTO | Java usa Keycloak/JWT; objetivo comercial contempla Google; no hay plan de migracion de usuarios | Identidad, costos, operacion y claims. |
| DG-15 | Payment provider y contrato de confirmacion | SIGUE_ABIERTO | Java solo simula pago; no hay gateway/webhook | Confirmacion financiera, idempotencia y conciliacion. |
| DG-16 | Fuente frontend o alcance de Angular nuevo | SIGUE_ABIERTO | No hay frontend source en el clon | Contratos API, UX y plan de entrega. |
| DG-17 | Cardinalidad Platform Actor a perfiles de negocio | SIGUE_ABIERTO | Sprint 2 introduce actor técnico separado; no existe fuente que relacione actor con Customer/Affiliate/Distributor | Ownership y provisioning de perfiles no pueden inferirse. |

**Condicion para cerrar un gate:** regla de negocio aprobada por VISANA, fuente de verdad identificada, efectos de datos/finanzas definidos y criterios de prueba acordados. El cierre no se infiere de una semilla SQL, documento o binario aislado.
