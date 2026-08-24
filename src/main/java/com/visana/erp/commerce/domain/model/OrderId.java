package com.visana.erp.commerce.domain.model;

import java.util.Objects;
import java.util.UUID;

public record OrderId(UUID value) {
    public OrderId {
        Objects.requireNonNull(value, "OrderId value cannot be null");
    }

    public static OrderId of(UUID value) {
        return new OrderId(value);
    }

    public static OrderId of(String value) {
        Objects.requireNonNull(value, "OrderId string cannot be null");
        try {
            return new OrderId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for OrderId", e);
        }
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }
}
