package com.visana.erp.platform.infrastructure.adapter.out.persistence.identity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlatformActorSpringDataRepository extends JpaRepository<PlatformActorJpaEntity, UUID> { }
