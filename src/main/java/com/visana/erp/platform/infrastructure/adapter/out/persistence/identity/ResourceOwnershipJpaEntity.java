package com.visana.erp.platform.infrastructure.adapter.out.persistence.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resource_ownerships")
public class ResourceOwnershipJpaEntity {
    @Id
    @Column(name = "ownership_id", nullable = false, length = 36)
    @JdbcTypeCode(Types.VARCHAR)
    private UUID ownershipId;

    @Column(name = "resource_type", nullable = false, length = 100)
    private String resourceType;

    @Column(name = "resource_id", nullable = false, length = 36)
    @JdbcTypeCode(Types.VARCHAR)
    private UUID resourceId;

    @Column(name = "owner_actor_id", nullable = false, length = 36)
    @JdbcTypeCode(Types.VARCHAR)
    private UUID ownerActorId;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    protected ResourceOwnershipJpaEntity() { }

    ResourceOwnershipJpaEntity(UUID ownershipId, String resourceType, UUID resourceId, UUID ownerActorId, Instant assignedAt) {
        this.ownershipId = ownershipId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.ownerActorId = ownerActorId;
        this.assignedAt = assignedAt;
    }

    UUID ownerActorId() { return ownerActorId; }
}
