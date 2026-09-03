package com.visana.erp.platform.domain.audit;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Foundation event for future audit adapters. Metadata must be pre-sanitized by callers.
 */
public record AuditEvent(
        String actor,
        String action,
        String resourceType,
        String resourceId,
        Instant occurredAt,
        String correlationId,
        Map<String, String> metadata) {

    public AuditEvent {
        Objects.requireNonNull(action, "action cannot be null");
        Objects.requireNonNull(resourceType, "resourceType cannot be null");
        Objects.requireNonNull(resourceId, "resourceId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        Objects.requireNonNull(correlationId, "correlationId cannot be null");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
