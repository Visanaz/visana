# Network Model — Sprint 4

`PlatformActor` permanece técnico y no equivale a perfil de negocio. Una afiliación es una operación explícita que crea `business_profiles`, opcionalmente enlaza un actor y crea `network_members`. OIDC no crea afiliación.

El modelo nuevo separa `business_profiles`, `business_profile_actor_links`, `network_members` y `sponsor_relationships`. El vínculo actor-perfil es uno a uno técnico en esta foundation para habilitar `/api/v1/network/me`; no decide la cardinalidad de negocio DG-17 ni convierte IDs legacy.

Sólo existen consultas autenticadas propias: `GET /api/v1/network/me`, `/direct`, `/ancestors`, `/descendants`. Retornan IDs y profundidad, nunca PII. Creación administrativa pública y consulta global están bloqueadas por falta de matriz de roles.
