package com.visana.erp.commerce.catalog.infrastructure.persistence;

import com.visana.erp.commerce.catalog.application.CatalogCategoryPort;
import com.visana.erp.commerce.catalog.application.CatalogProductPort;
import com.visana.erp.commerce.catalog.domain.*;
import com.visana.erp.core.domain.model.Money;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class CatalogPersistenceAdapter implements CatalogProductPort, CatalogCategoryPort {
    private final CatalogProductSpringDataRepository products;
    private final CatalogCategorySpringDataRepository categories;
    public CatalogPersistenceAdapter(CatalogProductSpringDataRepository products, CatalogCategorySpringDataRepository categories) { this.products=products; this.categories=categories; }
    @Override public Optional<CatalogProduct> findById(UUID id) { return products.findById(id).map(this::product); }
    @Override public Page<CatalogProduct> findActive(Pageable pageable) { return products.findByStatus(CatalogProductStatus.ACTIVE.name(), pageable).map(this::product); }
    @Override public CatalogProduct save(CatalogProduct product) { products.save(new CatalogProductJpaEntity(product.id(), product.sku(), product.code(), product.name(), product.description(), product.basePrice().amount(), product.status().name(), product.categoryId(), product.createdAt(), product.updatedAt())); return product; }
    @Override public Page<CatalogCategory> findActiveCategories(Pageable pageable) { return categories.findByStatus(CatalogProductStatus.ACTIVE.name(), pageable).map(this::category); }
    @Override public CatalogCategory save(CatalogCategory category) { categories.save(new CatalogCategoryJpaEntity(category.id(), category.name(), category.status().name(), category.createdAt(), category.updatedAt())); return category; }
    private CatalogProduct product(CatalogProductJpaEntity e) { return new CatalogProduct(e.getId(),e.getSku(),e.getCode(),e.getName(),e.getDescription(),Money.of(e.getBasePrice()),CatalogProductStatus.valueOf(e.getStatus()),e.getCategoryId(),e.getCreatedAt(),e.getUpdatedAt()); }
    private CatalogCategory category(CatalogCategoryJpaEntity e) { return new CatalogCategory(e.getId(),e.getName(),CatalogProductStatus.valueOf(e.getStatus()),e.getCreatedAt(),e.getUpdatedAt()); }
}
