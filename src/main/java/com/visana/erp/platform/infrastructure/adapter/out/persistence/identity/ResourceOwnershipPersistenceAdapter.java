package com.visana.erp.platform.infrastructure.adapter.out.persistence.identity;

import com.visana.erp.platform.application.port.out.ResourceOwnershipPersistencePort;
import com.visana.erp.platform.domain.identity.PlatformActorId;
import com.visana.erp.platform.domain.ownership.ResourceOwnership;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class ResourceOwnershipPersistenceAdapter implements ResourceOwnershipPersistencePort {
    private final ResourceOwnershipSpringDataRepository repository;

    public ResourceOwnershipPersistenceAdapter(ResourceOwnershipSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<PlatformActorId> findOwner(String resourceType, UUID resourceId) {
        return repository.findByResourceTypeAndResourceId(resourceType, resourceId)
                .map(entity -> PlatformActorId.of(entity.ownerActorId()));
    }

    @Override
    public void persist(ResourceOwnership ownership) {
        repository.save(new ResourceOwnershipJpaEntity(UUID.randomUUID(), ownership.resourceType(), ownership.resourceId(),
                ownership.ownerActorId().value(), Instant.now()));
    }
}
