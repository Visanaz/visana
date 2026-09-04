# Frontend contribution rules

Read `../docs/00_governance/CONTINUOUS_ENGINEERING_LOOP.md` and `PROJECT_STATE.md` before implementation. Keep FSD-Lite boundaries: pages compose features; shared/core never import features; features expose `index.ts`. Use strict TypeScript, Signals for client state, typed/generated DTOs for wire contracts, no business or financial rules in UI, semantic Tailwind tokens, accessible HTML, tests and updated docs for every completed cycle.
