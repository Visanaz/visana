package com.visana.erp.commerce.catalog.infrastructure.persistence;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CatalogProductSpringDataRepository extends JpaRepository<CatalogProductJpaEntity, UUID> { Page<CatalogProductJpaEntity> findByStatus(String status, Pageable pageable); }
