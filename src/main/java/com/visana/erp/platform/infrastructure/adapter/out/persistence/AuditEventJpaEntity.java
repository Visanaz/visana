package com.visana.erp.platform.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "platform_audit_events")
public class AuditEventJpaEntity {

    @Id
    @Column(name = "audit_id", nullable = false, length = 36)
    private UUID auditId;
    @Column(name = "actor_id", length = 128)
    private String actorId;
    @Column(nullable = false, length = 100)
    private String action;
    @Column(name = "resource_type", nullable = false, length = 100)
    private String resourceType;
    @Column(name = "resource_id", nullable = false, length = 128)
    private String resourceId;
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;
    @Column(name = "correlation_id", nullable = false, length = 128)
    private String correlationId;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String metadata;

    protected AuditEventJpaEntity() { }

    AuditEventJpaEntity(UUID auditId, String actorId, String action, String resourceType, String resourceId,
                        Instant occurredAt, String correlationId, String metadata) {
        this.auditId = auditId;
        this.actorId = actorId;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.occurredAt = occurredAt;
        this.correlationId = correlationId;
        this.metadata = metadata;
    }

    String metadata() {
        return metadata;
    }
}
