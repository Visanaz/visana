package com.visana.erp.platform.infrastructure.adapter.out.persistence.identity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResourceOwnershipSpringDataRepository extends JpaRepository<ResourceOwnershipJpaEntity, UUID> {
    Optional<ResourceOwnershipJpaEntity> findByResourceTypeAndResourceId(String resourceType, UUID resourceId);
}
