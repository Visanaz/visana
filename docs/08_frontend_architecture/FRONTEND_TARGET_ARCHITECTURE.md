# VISANA Plan 3 — Frontend Target Architecture

## Aplicación a construir

Se propone una **aplicación web Angular nueva**, TypeScript, standalone y con release Angular estable soportada que se verificará al implementar. No es una reconstrucción PHP/CodeIgniter ni un clon pixel-perfect. Consume contratos aprobados bajo `/api/v1`; Plan 3 es el sistema de registro y calcula precio, ownership y futuras reglas financieras.

No se crea workspace, dependencia, código Angular ni configuración en esta fase.

## Límites conceptuales

```text
public site (contenido aprobado, opcional y separado)
                 |
application shell / rutas protegidas
                 |
feature page/component -> feature facade or signal store -> typed API client -> /api/v1
                 |
OIDC adapter, HTTP/error infrastructure, observability boundary
```

La estructura inicial a validar al implementar es `core/`, `shared/` y `features/`. `core` concentra autenticación, transporte HTTP, errores, configuración y observabilidad; `shared` sólo piezas verdaderamente reutilizables; cada feature conserva sus modelos, cliente, estado, rutas y componentes. Ningún componente llama `HttpClient` directo, ni contiene cálculo financiero, autorización de servidor o fórmulas de negocio.

## Módulos y estado de preparación

| Feature | Estado | Base disponible | Límite |
|---|---|---|---|
| Auth / session | `PARTIAL_BACKEND` | Keycloak/JWT y `GET /api/v1/me` | DG-14; no hay flujo SPA implementado ni matriz completa de roles. |
| Home/dashboard | `PRODUCT_DECISION_REQUIRED` | Shell e identidad técnica posibles | Widgets, métricas y contenido no están definidos. |
| Profile | `PRODUCT_DECISION_REQUIRED` | `PlatformActor` técnico | DG-17 impide UI que fije cardinalidad de perfiles. |
| Catalog | `READY_BACKEND` | productos/categorías autenticados | Sin administración, búsqueda textual ni UI recuperada. |
| Orders | `PARTIAL_BACKEND` | crear/listar/ver propias, precios servidor | Pago, cancelación, envío e idempotencia no están resueltos. |
| Network | `PARTIAL_BACKEND` | me/direct/ancestors/descendants | Sólo IDs/profundidad; PII, conteos y exploración de rama requieren contrato. |
| Qualification | `BLOCKED_BY_BUSINESS_RULES` | Ninguna API de estado | DG-01/DG-03/DG-11. |
| Rewards | `BLOCKED_BY_BUSINESS_RULES` | Referencia visual legado | DG-02/DG-04/DG-06–DG-09/DG-12. |
| Finance | `BACKEND_NOT_AVAILABLE` | Confirmación de pago simulada aislada | No ledger, settlement, payout ni conciliación aptos para UI. |
| Admin | `PRODUCT_DECISION_REQUIRED` | Ningún rol/capability administrativo probado | Role Matrix pendiente. |
| Support | `LEGACY_ONLY` | PDFs/activos legales | Ticketing y soporte transaccional no evidenciados. |
| Public/marketing | `PRODUCT_DECISION_REQUIRED` | activos y búsqueda legado parcial | Contenido, ownership y límite con la SPA por aprobar. |

## Principios no negociables

1. `BACKEND_CALCULATES; FRONTEND_PRESENTS`: los literales legado de IVA, retenciones, 10 %, 35 % e incentivos son `LEGACY_FRONTEND_RULE_REFERENCE`, nunca reglas canónicas TypeScript.
2. La visibilidad de UI mejora la experiencia; el backend sigue siendo la única autoridad de autenticación, capability y ownership.
3. No se deduce identidad de negocio desde email, username ni claims decodificados. Tras OIDC, `/api/v1/me` es la fuente de estado técnico de actor.
4. Los contratos ausentes se registran como gaps; la SPA no inventa payloads, permisos ni respuestas de compensación.
5. Toda feature diseña loading, empty, error, forbidden, unlinked identity y retry/offline cuando aplique.

## Decisiones relacionadas

Las ADR propuestas ADR-014 a ADR-019 describen alternativas sin convertir proveedor, hosting, librería UI o versión Angular en decisiones aprobadas. DG-16 queda `PARTIAL_SOURCE_AVAILABLE / SIGUE_ABIERTO`; los subgates FR-DG-01 a FR-DG-05 requieren validación VISANA.
