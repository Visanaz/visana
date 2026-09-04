# Frontend Sprint 0 Proposal — no ejecutado

## Objetivo

Preparar la fundación técnica sin liberar un journey financiero ni asumir perfiles de negocio. Esta propuesta no crea Angular workspace, `package.json`, dependencias, CI ni código.

| Propuesta | Backlog | Resultado esperado futuro | Dependencia |
|---|---|---|---|
| Workspace Angular standalone y configuración externa | ST-FE-001 | build/lint/typecheck reproducibles | aprobación de implementación |
| Shell y lazy routing | ST-FE-004 | límite public/app y rutas base | FR-DG-02 acotado |
| Adaptador OIDC PKCE y sesión | ST-FE-002 | login/callback/logout seguros | DG-14 / parámetros IdP |
| Contexto `/api/v1/me` y guards | ST-FE-003/004 | linked/unlinked/forbidden UX | backend actual |
| Cliente API, interceptor y ProblemDetail | ST-FE-005 | transporte central y correlation ID | OpenAPI/contrato |
| Pattern Signals/facades | ADR-015 | estado feature-scoped | aprobación técnica |
| Test foundation y contract mocks | ST-FE-001/005 | unit/component/integration base | contratos aprobados |
| Tokens/diseño y accesibilidad base | ST-FE-017/018 | decisiones visuales trazables | FR-DG-03 |
| Pipeline conceptual CI | ST-FE-001 | lint/typecheck/test/build futuros | hosting/CI decision |

## Fuera de Sprint 0

No catálogo, orden, pago, red de negocio, qualification, rewards, finance, admin, migración de activos ni integración productiva. La propuesta debe revisarse después de cerrar los gates que correspondan; no inicia trabajo automáticamente.
