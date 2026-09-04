# Frontend Authentication and Security Architecture

## Autenticación y actor

La propuesta es OIDC Authorization Code Flow con PKCE mediante un adaptador/librería evaluada al implementar. La SPA no recolecta ni guarda contraseña. Tras callback o recuperación de sesión, invoca `GET /api/v1/me`, que actualmente devuelve `actorId`, `identityStatus` y roles concedidos; no devuelve claims ni credenciales.

`identityStatus` determina experiencia técnica: sesión válida con actor enlazado permite rutas propias; identidad no enlazada va a `/unlinked`; deshabilitada/denegada produce un estado no privilegiado. No se deduce ownership de email, username o token decodificado. El backend decide de nuevo actor, rol y ownership en cada petición.

## Token y sesión

| Alternativa | Evaluación conceptual |
|---|---|
| Sólo memoria + renovación controlada por librería | Preferencia inicial a evaluar: reduce persistencia tras XSS, exige estrategia de recuperación/refresh. |
| `sessionStorage` | Alternativa con persistencia limitada por pestaña; sigue expuesta a XSS. |
| `localStorage` | No recomendado por defecto: amplía exposición a robo de token por XSS. |
| BFF futuro | Alternativa si necesidades de sesión, CSRF y operación justifican complejidad; no se asume ahora. |

No se selecciona mecanismo final mientras DG-14 y los parámetros del proveedor (redirect URIs, scopes, refresh/cookies) sigan abiertos.

## Threat model y controles

| Amenaza | Límite frontend | Control requerido |
|---|---|---|
| XSS / token theft | UI no reemplaza hardening servidor | template seguro, evitar HTML no confiable, CSP futura, dependencias revisadas y almacenamiento no persistente por defecto. |
| CSRF | Depende de bearer/BFF/cookies | decidir con patrón auth; backend actual stateless no prueba configuración SPA. |
| IDOR | Frontend no puede impedirlo | no construir IDs privilegiados; backend ownership fail-closed. |
| Price/mass-assignment manipulation | Cliente es manipulable | Orders envía sólo líneas `productId`/`quantity`; backend calcula precio/totales. |
| Open redirect | callback/logout | allowlist de rutas locales y validación de state/nonce por librería OIDC. |
| Dependencias/terceros | legado no tiene SBOM | manifiesto, lockfile, scanning y revisión de CSP al implementar. |

## HTTP, errores y observabilidad

Un interceptor/infraestructura central adjunta token según el adaptador, correlaciona errores y traduce ProblemDetail a mensajes de UX. No expone stacktrace ni detalles de autorización. La observabilidad futura incluye error cliente, release/version y métricas de rendimiento sin PII; no se selecciona vendor. CSP, CORS por DEV/QA/PROD, headers y allowlists de redirect son requisitos de implementación, no cambios actuales.
