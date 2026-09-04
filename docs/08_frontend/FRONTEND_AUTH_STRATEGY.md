# Frontend Auth Strategy

OIDC Authorization Code + PKCE is wrapped behind an Angular OIDC port, not a Keycloak SDK dependency. Runtime config provides authority/client ID/API URL/scope without secrets. JWT is not manually stored or logged; `/api/v1/me` resolves platform context only after an approved generated contract. Guards are UX only; backend retains 401/403 and ownership authority.
