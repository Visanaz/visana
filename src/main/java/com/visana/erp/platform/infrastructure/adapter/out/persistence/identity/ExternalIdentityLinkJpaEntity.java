package com.visana.erp.platform.infrastructure.adapter.out.persistence.identity;

import com.visana.erp.platform.domain.identity.ExternalIdentityLinkStatus;
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
@Table(name = "external_identity_links")
public class ExternalIdentityLinkJpaEntity {
    @Id
    @Column(name = "link_id", nullable = false, length = 36)
    @JdbcTypeCode(Types.VARCHAR)
    private UUID linkId;

    @Column(nullable = false, length = 40)
    private String provider;

    @Column(nullable = false, length = 512)
    private String issuer;

    @Column(nullable = false, length = 512)
    private String subject;

    @Column(name = "actor_id", nullable = false, length = 36)
    @JdbcTypeCode(Types.VARCHAR)
    private UUID actorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExternalIdentityLinkStatus status;

    @Column(name = "linked_at", nullable = false)
    private Instant linkedAt;

    @Column(name = "disabled_at")
    private Instant disabledAt;

    protected ExternalIdentityLinkJpaEntity() { }

    ExternalIdentityLinkJpaEntity(UUID linkId, String provider, String issuer, String subject, UUID actorId,
                                  ExternalIdentityLinkStatus status, Instant linkedAt) {
        this.linkId = linkId;
        this.provider = provider;
        this.issuer = issuer;
        this.subject = subject;
        this.actorId = actorId;
        this.status = status;
        this.linkedAt = linkedAt;
    }

    void disable(Instant disabledAt) {
        this.status = ExternalIdentityLinkStatus.DISABLED;
        this.disabledAt = disabledAt;
    }

    UUID actorId() { return actorId; }
    ExternalIdentityLinkStatus status() { return status; }
}
