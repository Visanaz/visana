# Frontend Testing Strategy

Vitest/TestBed covers bootstrap, runtime config, session states and HTTP ProblemDetail/correlation. Playwright covers a local public shell smoke. Tests mock OIDC boundaries and never use production tokens or Keycloak. CI runs npm ci, lint, tests, build and Playwright Chromium.
