# Frontend State Strategy

## Recomendación

Usar **Angular Signals y feature-scoped facades/stores** como base. No se adopta NgRx automáticamente: la evidencia actual no demuestra eventos cross-feature complejos ni necesidad de un store global. ADR-015 queda como propuesta para confirmar al arrancar implementación.

| Tipo de estado | Ubicación futura | Ejemplos | Regla |
|---|---|---|---|
| Local de UI | componente/página | modal, foco, tab, filtro temporal | No se promueve globalmente. |
| Server state | facade/store de feature | catálogo paginado, órdenes propias, miembros de red | Carga, error, refresh y cancelación explícitos. |
| Cross-feature mínimo | core/contexto de sesión | estado técnico `/me`, sesión OIDC, locale preferido | No contiene datos financieros ni dominio mutable. |
| Draft local | feature Cart/Orders | productId/cantidad antes de POST | Nunca precio, total, ownerActorId ni estado privilegiado. |

## Flujo de datos

`component/page → facade or signal store → typed API client → /api/v1`.

Los componentes presentan/interactúan. La facade coordina estados de UX y mapea DTO a view model cuando aporte claridad. El cliente API serializa contratos y centraliza transporte/errores. Ni store ni componente calculan comisión, elegibilidad, impuestos, precio autoritativo o autorización.

## Estados de experiencia comunes

Cada feature declara `idle/loading/ready/empty/error/forbidden/unlinked` y, donde corresponda, retry/offline. Los errores ProblemDetail se normalizan en core y conservan `correlationId` sólo para soporte, sin revelar detalles internos ni PII.
