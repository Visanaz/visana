# ADR-016 — OIDC SPA authentication

**Estado:** PROPOSED.

**Contexto:** Plan 3 valida JWT Keycloak/OIDC y resuelve `PlatformActor`; el legado no demuestra un contrato de sesión recuperable. DG-14 sigue abierto.

**Propuesta:** Authorization Code Flow con PKCE, mediante adaptador/librería evaluada al implementar. Tras sesión se consulta `/api/v1/me`; no se crea relación de negocio desde email, username o claims. Se prefiere token en memoria/librería gestionada; sessionStorage y BFF futuro se evalúan por threat model; localStorage no es patrón por defecto.

**Consecuencia:** no se implementa login, refresh, redirect URI, scopes, provider ni BFF en esta fase. El backend mantiene autoridad y los estados unlinked/disabled se representan explícitamente.
