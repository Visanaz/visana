package com.visana.erp.platform.infrastructure.adapter.out.persistence.identity;

import com.visana.erp.platform.domain.identity.PlatformActorStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "platform_actors")
public class PlatformActorJpaEntity {
    @Id
    @Column(name = "actor_id", nullable = false, length = 36)
    @JdbcTypeCode(Types.VARCHAR)
    private UUID actorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlatformActorStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected PlatformActorJpaEntity() { }

    PlatformActorJpaEntity(UUID actorId, PlatformActorStatus status, Instant createdAt) {
        this.actorId = actorId;
        this.status = status;
        this.createdAt = createdAt;
    }

    UUID actorId() { return actorId; }
    PlatformActorStatus status() { return status; }
}
