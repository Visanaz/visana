# Project State

- **Project version:** Plan 3 / Frontend F0 in progress
- **Date updated:** 2026-09-03 America/Bogota
- **Current dev SHA:** `0155f428b67099974ca96da56ea81344dfac8c44`
- **Main / QA SHA:** `5f241fe`
- **Merged:** Sprints 0–4 and frontend AS-IS audit PR #9.
- **Open PRs:** #8 business-rules baseline; #10 target-architecture documentation; #11 Frontend F0 foundation.
- **Backend:** Spring `/api/v1`, Springdoc present; business finance/qualification blocked.
- **Frontend:** Angular 22.1 / TypeScript 6.0 / Node 24 target foundation created; OpenAPI codegen blocked pending reproducible spec.
- **Quality:** F0 local lint PASS; Vitest 7 PASS; build PASS; Playwright 1 PASS. CI pending PR #11.
- **Identity/security:** Keycloak/OIDC and PlatformActor backend foundation; SPA uses OIDC/PKCE abstraction, no secret or token storage manual.
- **Open gates:** DG-01..DG-17 and FR-DG-01..05 remain open; PR #8 remains authoritative for business-rule approval.
- **Current authorized work:** Frontend F0 foundation only.
- **Prohibited work:** qualification, volume, commissions, finance, payout, payment provider, production deployment, legacy frontend modification.
- **Next PM gate:** review F0 PR and decide OpenAPI spec extraction / PR #10 sequencing.
