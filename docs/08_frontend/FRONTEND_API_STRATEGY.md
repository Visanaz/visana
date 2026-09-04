# Frontend API Strategy

Springdoc and `/v3/api-docs/**` exist in backend source. F0 adds pinned OpenAPI Generator and `api:generate`, but no spec was extracted or DTO invented: `FRONTEND_API_CODEGEN_BLOCKED_BY_SPEC`. Generated code belongs in `core/api/generated`; `/api/v1/me` remains a future generated-client boundary.
