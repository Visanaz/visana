package com.visana.erp.platform.infrastructure.adapter.out.persistence.identity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExternalIdentityLinkSpringDataRepository extends JpaRepository<ExternalIdentityLinkJpaEntity, UUID> {
    Optional<ExternalIdentityLinkJpaEntity> findByProviderAndIssuerAndSubject(String provider, String issuer, String subject);
}
