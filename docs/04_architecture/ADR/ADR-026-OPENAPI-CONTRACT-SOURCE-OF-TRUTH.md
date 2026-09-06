# ADR-026 OpenAPI contract source of truth

**Status:** ACCEPTED.

Spring controller mappings, DTOs and Springdoc configuration are the technical source of truth. `openapi/visana-api-v1.json` is generated only by the reproducible runtime command and is reviewed as a snapshot; it is never manually authored as an independent contract. Future Angular work consumes generated transport types behind feature adapters/facades, not directly from UI components. The CI drift gate requires any intentional API change to update the generated snapshot in the same PR.
