package com.visana.erp.platform.application.identity;

import com.visana.erp.platform.application.audit.AuditEventWriter;
import com.visana.erp.platform.application.port.out.IdentityPersistencePort;
import com.visana.erp.platform.domain.audit.AuditEvent;
import com.visana.erp.platform.domain.identity.ExternalIdentity;
import com.visana.erp.platform.domain.identity.ExternalIdentityLink;
import com.visana.erp.platform.domain.identity.ExternalIdentityLinkStatus;
import com.visana.erp.platform.domain.identity.PlatformActor;
import com.visana.erp.platform.domain.identity.PlatformActorId;
import com.visana.erp.platform.domain.identity.PlatformActorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/** Explicit provisioning only. It is intentionally not exposed as a public HTTP API. */
@Service
public class IdentityProvisioningService {
    private final IdentityPersistencePort identityPersistencePort;
    private final AuditEventWriter auditEventWriter;

    public IdentityProvisioningService(IdentityPersistencePort identityPersistencePort, AuditEventWriter auditEventWriter) {
        this.identityPersistencePort = identityPersistencePort;
        this.auditEventWriter = auditEventWriter;
    }

    @Transactional
    public PlatformActor provisionAndLink(ExternalIdentity externalIdentity, String correlationId) {
        if (identityPersistencePort.externalIdentityExists(externalIdentity)) {
            throw new IdentityConflictException();
        }
        PlatformActor actor = new PlatformActor(PlatformActorId.of(UUID.randomUUID()), PlatformActorStatus.ACTIVE);
        ExternalIdentityLink link = new ExternalIdentityLink(UUID.randomUUID(), externalIdentity, actor.id(),
                ExternalIdentityLinkStatus.ACTIVE, Instant.now());
        identityPersistencePort.persist(actor, link);
        auditEventWriter.append(new AuditEvent(actor.id().value().toString(), "IDENTITY_LINK_CREATED",
                "EXTERNAL_IDENTITY_LINK", link.id().toString(), Instant.now(), correlationId, Map.of("provider", externalIdentity.provider())));
        return actor;
    }

    @Transactional
    public void disableLink(ExternalIdentity externalIdentity, String correlationId) {
        if (!identityPersistencePort.disable(externalIdentity)) {
            throw new IllegalArgumentException("External identity link was not found.");
        }
        auditEventWriter.append(new AuditEvent(null, "IDENTITY_LINK_DISABLED", "EXTERNAL_IDENTITY_LINK",
                "current-link", Instant.now(), correlationId, Map.of("provider", externalIdentity.provider())));
    }
}
