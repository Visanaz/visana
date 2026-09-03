package com.visana.erp.platform.application.ownership;

import com.visana.erp.platform.application.audit.AuditEventWriter;
import com.visana.erp.platform.application.port.out.ResourceOwnershipPersistencePort;
import com.visana.erp.platform.domain.audit.AuditEvent;
import com.visana.erp.platform.domain.identity.PlatformActorId;
import com.visana.erp.platform.domain.ownership.ResourceOwnership;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Explicit ownership assignment for new Plan 3 resources; no legacy profile mapping is inferred. */
@Service
public class ResourceOwnershipService {
    private final ResourceOwnershipPersistencePort ownershipPersistencePort;
    private final AuditEventWriter auditEventWriter;

    public ResourceOwnershipService(ResourceOwnershipPersistencePort ownershipPersistencePort, AuditEventWriter auditEventWriter) {
        this.ownershipPersistencePort = ownershipPersistencePort;
        this.auditEventWriter = auditEventWriter;
    }

    @Transactional
    public void assign(String resourceType, UUID resourceId, PlatformActorId ownerActorId, String correlationId) {
        Optional<PlatformActorId> existingOwner = ownershipPersistencePort.findOwner(resourceType, resourceId);
        existingOwner.ifPresent(existing -> {
            if (!existing.equals(ownerActorId)) {
                throw new IllegalStateException("Resource ownership is already assigned.");
            }
        });
        if (existingOwner.isPresent()) {
            return;
        }
        ownershipPersistencePort.persist(new ResourceOwnership(resourceType, resourceId, ownerActorId));
        auditEventWriter.append(new AuditEvent(ownerActorId.value().toString(), "OWNERSHIP_ASSIGNED", resourceType,
                resourceId.toString(), Instant.now(), correlationId, Map.of()));
    }
}
