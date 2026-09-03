package com.visana.erp.platform.application.port.out;

import com.visana.erp.platform.domain.identity.PlatformActorId;
import com.visana.erp.platform.domain.ownership.ResourceOwnership;

import java.util.Optional;
import java.util.UUID;

public interface ResourceOwnershipPersistencePort {
    Optional<PlatformActorId> findOwner(String resourceType, UUID resourceId);

    void persist(ResourceOwnership ownership);
}
