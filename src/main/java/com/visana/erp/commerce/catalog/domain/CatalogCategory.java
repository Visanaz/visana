package com.visana.erp.commerce.catalog.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record CatalogCategory(UUID id, String name, CatalogProductStatus status, Instant createdAt, Instant updatedAt) {
    public CatalogCategory {
        Objects.requireNonNull(id, "category id cannot be null");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("category name cannot be blank");
        Objects.requireNonNull(status, "category status cannot be null");
        Objects.requireNonNull(createdAt, "category createdAt cannot be null");
        Objects.requireNonNull(updatedAt, "category updatedAt cannot be null");
    }
}
