# VISANA Plan 3 frontend

Angular 22 standalone, zoneless SPA foundation. Requirements: Node 24.15+ (validated with 24.19), npm 11.6.2. Use `npm ci`, `npm run start`, `npm run lint`, `npm run test -- --run`, `npm run build`, and `npm run e2e`.

`src/app` is FSD-Lite: `core`, `shared`, `pages`, and `features`. Runtime values live in `public/assets/runtime-config.json`; do not put secrets, authority, client ID or production URL in source. OIDC uses Code + PKCE through an abstraction. `npm run api:generate` requires an approved local OpenAPI file through `OPENAPI_SPEC_PATH`; generated files belong in `core/api/generated` and are not manually edited.

Catalog, orders and network are reserved feature boundaries only. Qualification, rewards, commissions, taxes, payouts and finance are blocked by business decisions.
