package com.visana.erp.commerce.domain.model;

import java.util.Objects;
import java.util.UUID;

public record ProductId(UUID value) {
    public ProductId {
        Objects.requireNonNull(value, "ProductId value cannot be null");
    }

    public static ProductId of(UUID value) {
        return new ProductId(value);
    }

    public static ProductId of(String value) {
        Objects.requireNonNull(value, "ProductId string cannot be null");
        try {
            return new ProductId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for ProductId", e);
        }
    }

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID());
    }
}
