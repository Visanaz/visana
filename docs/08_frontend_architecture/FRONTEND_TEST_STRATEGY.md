# Frontend Test Strategy — propuesta futura

| Nivel | Propósito | Ejemplos |
|---|---|---|
| Unit | modelos, facades, mapeo de ProblemDetail y guards | no enviar precio/owner, unlinked state, límite de paginación UI |
| Component | estados y accesibilidad de pantalla | loading/empty/error/forbidden, foco de modal, labels y keyboard |
| Integration | cliente API con contract mocks o DEV controlado | products, create/list/read own order, `/me`, network mínimo |
| E2E | journeys críticos con backend/identidad aprobados | login, catálogo, crear/ver propia, denegar orden ajena, red; qualification/rewards sólo después de reglas |
| Security/accessibility | regresión de XSS, dependencia, headers y a11y | HTML no confiable, token flow, escaneo, navegación teclado |

La herramienta se elige al implementar; se evaluarán utilidades Angular y Playwright sin instalar ahora. Mocks representan contratos aprobados, no inventan comisiones, impuestos, roles ni decisión financiera.

## Quality gates futuros

Pipeline conceptual: lint, typecheck, unit, build, contract/integration, E2E cuando aplique y dependency/security checks. Ambientes LOCAL/DEV/QA/PROD usan configuración externa; no se codifican localhost, dominios de producción ni client IDs en fuente. CORS para orígenes DEV/QA/PROD es requisito de backend futuro, no cambio de esta rama.
