# Frontend Feature Map

| ID | Feature | Responsabilidad y journeys | Estado | Dependencias |
|---|---|---|---|---|
| FF-01 | Auth/session | login, callback, logout, recuperación de sesión, unauthorized/forbidden/unlinked | PARTIAL_BACKEND | OIDC/PKCE, DG-14, `/api/v1/me` |
| FF-02 | Application shell/dashboard | navegación, contexto de sesión, landing autenticado sin métricas inventadas | PRODUCT_DECISION_REQUIRED | FF-01, alcance UX |
| FF-03 | Technical account/profile | estado técnico y entrada futura a perfiles de negocio | PRODUCT_DECISION_REQUIRED | DG-17, API de perfiles ausente |
| FF-04 | Catalog | lista, filtro UI si el contrato lo permite, detalle, paginación, estados UX | READY_BACKEND | GET products/categories |
| FF-05 | Cart/order draft | selección local de líneas antes de crear una orden | PARTIAL_BACKEND | FF-04, POST orders; precio no es fuente cliente |
| FF-06 | Orders | crear, historial y detalle propio | PARTIAL_BACKEND | ownership y endpoints Orders |
| FF-07 | Checkout placeholder | explicación de orden pendiente, no gateway | BLOCKED_BY_BUSINESS_RULES | DG-15; no se promete checkout productivo |
| FF-08 | Network | mi miembro, directos, ancestros, descendientes y drill-down progresivo | PARTIAL_BACKEND | endpoints Network, contrato de datos/PII |
| FF-09 | Qualification | estado/progreso/requisitos futuros | BLOCKED_BY_BUSINESS_RULES | DG-01/DG-03/DG-11 y API inexistente |
| FF-10 | Rewards | consulta futura de resultados calculados por backend | BLOCKED_BY_BUSINESS_RULES | decisiones de compensación y API inexistente |
| FF-11 | Finance | ledger, settlements, payouts, estado de pago futuros | BACKEND_NOT_AVAILABLE | DG-04/DG-05/DG-10/DG-12/DG-15 |
| FF-12 | Admin | catálogo, perfiles, correcciones de red, aprobación financiera, reportes, cada uno separado | PRODUCT_DECISION_REQUIRED | role matrix, APIs administrativas |
| FF-13 | Public/support content | documentos, marketing y soporte estático aprobado | LEGACY_ONLY | FR-DG-02, contenido/licencias |

## Aislamiento

Cada feature mantiene API client, modelos de transporte/view model, facade/store, páginas/componentes y rutas. El shell conoce enlaces/capabilities de presentación, no reglas de compensación. Un feature no importa el estado interno de otro: la coordinación cross-feature ocurre mediante contratos mínimos (por ejemplo, contexto de sesión) y no mediante un store global indiscriminado.

## Red y visualización

La vista inicial debe priorizar tabla o tarjetas de directos/ancestros/descendientes y drill-down explícito, con límite/paginación acordado. Un árbol completo infinito no está justificado por los endpoints actuales, puede degradar móvil/accesibilidad y no preserva una necesidad probada. No se selecciona librería de diagramas.
