# Project State

- **Project version:** Plan 3 / backend repository split preparation
- **Date updated:** 2026-09-04 America/Bogota
- **Current dev SHA:** `c05fc27c145a38d3c04c258c91d7b93e91de8ff7`
- **Main / QA SHA:** `5f241fe`
- **Merged:** Sprints 0–4 and frontend AS-IS audit PR #9.
- **Open PRs:** #8 business-rules baseline; #12 backend OpenAPI contract.
- **Backend:** Spring `/api/v1`, Springdoc present; business finance/qualification blocked.
- **Frontend:** Angular F0 has moved to `Visanaz/visana-front`; F1 remains blocked pending approved backend OpenAPI v1.
- **Repository split:** merged PR #10 and PR #11 were transitional monorepo work and are superseded operationally by the controlled frontend migration/cleanup PRs.
- **Quality:** frontend validation belongs to `visana-front`; backend validation remains Maven/Java only.
- **Identity/security:** Keycloak/OIDC and PlatformActor backend foundation; SPA uses OIDC/PKCE abstraction, no secret or token storage manual.
- **Open gates:** DG-01..DG-17 and FR-DG-01..05 remain open; PR #8 remains authoritative for business-rule approval.
- **Current authorized work:** controlled backend/frontend repository split; no F1.
- **Prohibited work:** qualification, volume, commissions, finance, payout, payment provider, production deployment, legacy frontend modification.
- **Next PM gate:** review F0 PR and decide OpenAPI spec extraction / PR #10 sequencing.
