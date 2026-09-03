package com.visana.erp.commerce.catalog.infrastructure.persistence;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CatalogCategorySpringDataRepository extends JpaRepository<CatalogCategoryJpaEntity, UUID> { Page<CatalogCategoryJpaEntity> findByStatus(String status, Pageable pageable); }
