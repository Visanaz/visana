# Frontend Feature Parity Matrix

| Legacy feature/evidencia | TO-BE feature | Disposición | Backend status | Business status | UX status |
|---|---|---|---|---|---|
| Búsqueda ciudad/capacidad `HeroSearch` | Public search | DEPRECATE_PENDING_SCOPE | No equivalente | PRODUCT_DECISION_REQUIRED | No clonar; decidir si pertenece al producto. |
| Preferencias de idioma/menú | shell preferences | PRESERVE_BEHAVIOR | No requiere API | Disponible | Reimplementar sólo preferencias no sensibles. |
| Organización: afiliados/distribuidores, PII, nivel 1 fijo | Network | REDESIGN | PARTIAL_BACKEND | PII/roles pendientes | Lista/tarjetas + drill-down, no paridad visual obligatoria. |
| Órdenes de pago/bonificaciones | Rewards/Finance | BLOCKED | BACKEND_NOT_AVAILABLE | BUSINESS_RULE_BLOCKED | Sólo estados informativos futuros calculados por backend. |
| IVA, retención, 10 %, 35 %, bandas JS | Ninguno cliente | DEPRECATE | No equivalente | BUSINESS_RULE_BLOCKED | Nunca portar como TypeScript. |
| Upload `upload.php` | Upload futuro | DEPRECATE_UNTIL_APPROVED | No endpoint | PRODUCT_DECISION_REQUIRED | Sin UI hasta caso de uso y controles. |
| Activos producto/catálogo | Catalog | REDESIGN | READY_BACKEND | Alcance de contenido pendiente | UI nueva sobre contrato autenticado. |
| PDFs legales y marca | Public/support content | PRESERVE_BEHAVIOR_PENDING_REVIEW | Estático | Ownership/licencia vigente pendiente | Mantener sólo contenido aprobado. |
| Carrito/iconos de compra | Cart/order draft | NEW_TO_BE | PARTIAL_BACKEND | Producto pendiente | Draft local sin precio autoridad. |
| Login/registro legado | Auth | NEW_TO_BE | PARTIAL_BACKEND | DG-14/DG-17 | OIDC; registro de negocio no evidenciado. |
| Admin/reportes | Admin | BLOCKED | No API/matriz | PRODUCT_DECISION_REQUIRED | No recrear DataTables por defecto. |

Resumen: **PRESERVE_BEHAVIOR**: preferencias no sensibles y contenido/marca condicionado; **REDESIGN**: catálogo y red; **DEPRECATE**: fórmulas financieras, upload sin contrato y búsqueda sin alcance; **BLOCKED**: rewards, qualification, finance y admin. La matriz no afirma paridad funcional del legado incompleto.
