package com.visana.erp.commerce.catalog.domain;

import com.visana.erp.core.domain.model.Money;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Plan 3 catalog product. It intentionally does not carry compensation attributes from the legacy model. */
public record CatalogProduct(UUID id, String sku, String code, String name, String description, Money basePrice,
                             CatalogProductStatus status, UUID categoryId, Instant createdAt, Instant updatedAt) {
    public CatalogProduct {
        Objects.requireNonNull(id, "product id cannot be null");
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("product sku cannot be blank");
        if (code == null || code.isBlank()) throw new IllegalArgumentException("product code cannot be blank");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("product name cannot be blank");
        Objects.requireNonNull(basePrice, "product base price cannot be null");
        Objects.requireNonNull(status, "product status cannot be null");
        Objects.requireNonNull(createdAt, "product createdAt cannot be null");
        Objects.requireNonNull(updatedAt, "product updatedAt cannot be null");
    }

    public boolean isActive() { return status == CatalogProductStatus.ACTIVE; }
}
