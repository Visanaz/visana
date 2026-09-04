# Frontend Target Architecture — F0

Angular 22 standalone/zoneless SPA in monorepo `frontend/`. FSD-Lite has `core`, `shared`, `pages`, `features`; pages compose feature public APIs, core/shared never import features, and no feature imports another feature implementation. Signals hold client state; backend remains authority for business facts.

Catalog, orders and network have reservation boundaries only. Qualification, volume, rewards, commissions, finance and payments remain blocked.
