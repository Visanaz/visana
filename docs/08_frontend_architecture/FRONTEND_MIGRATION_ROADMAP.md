# Frontend Migration Roadmap

| Fase | Resultado documental/implementable futuro | Precondiciones | Riesgo / salida |
|---|---|---|---|
| F0 — Architecture foundation | ADRs, contratos y diseño aprobados | FR-DG-01..05 acotados; API gaps priorizados | No inicia código hasta aprobación. |
| F1 — Auth + shell | OIDC, `/me`, shell, errores y rutas base | DG-14/redirect URIs y estrategia token decididas | Prueba login/unlinked/forbidden sin perfiles de negocio. |
| F2 — Catalog + Orders | catálogo, draft, crear/ver propias | OpenAPI estable, ownership probado | Sin checkout real, envío ni cancelación. |
| F3 — Network | consulta mínima, drill-down, UX de datos autorizados | contrato de PII/capabilities y performance | Sin árbol infinito ni paridad visual forzada. |
| F4 — Qualification/Rewards | UI de resultados servidor | Business rules aprobadas y APIs calculadas | No portar fórmulas cliente. |
| F5 — Finance/Admin | ledger/payout/admin aprobados | contratos, role matrix, auditoría y payment decision | UAT financiero y segregación de roles. |
| F6 — Parity/UAT/Cutover | coexistencia, contenido, redirecciones y retiro | journeys críticos, seguridad, reconciliación/UAT | rollback y criterios de reversión acordados. |

## Coexistencia recomendada

Se recomienda una migración **strangler/route-by-route condicionada**, no big-bang por defecto: el frontend legado es parcial, no hay rutas recuperables ni prueba de coexistencia. El sitio público puede permanecer estático/separado mientras la SPA maneja capacidades autenticadas, si FR-DG-02 y contenido lo aprueban. Si la plataforma no admite aislamiento de rutas, un cutover controlado puede ser necesario; esa elección requiere evidencia operativa, URLs, SEO y rollback.

## Cutover futuro

No se fija fecha. Requiere journeys críticos aprobados, contratos estables, security review, UAT VISANA, reglas financieras aprobadas donde aplique, migración/reconciliación de datos y plan de rollback verificable. No se migra identidad por email/nombre ni se decide paridad por el artefacto legado.
