# ADR-017 — Cliente API contract-first

**Estado:** PROPOSED.

**Contexto:** `/api/v1` dispone de algunos contratos, mientras 12 gaps frontend requieren acuerdo. El legado no es compatible por nombre de ruta.

**Propuesta:** evaluar generación OpenAPI para DTOs/operaciones estables, detrás de clientes tipados por feature; una capa manual delgada cubre OIDC, ProblemDetail, correlation ID, cancelación y view models. Componentes usan facades/stores, nunca transporte directo.

**Consecuencia:** contratos faltantes se acuerdan y prueban antes de UI. No se generan clientes ni se inventan payloads en esta fase.
