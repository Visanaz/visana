package com.visana.erp.core.domain.model;

import java.util.Objects;
import java.util.UUID;

public record TenantId(UUID value) {
    public TenantId {
        Objects.requireNonNull(value, "TenantId value cannot be null");
    }

    public static TenantId of(UUID value) {
        return new TenantId(value);
    }

    public static TenantId of(String value) {
        Objects.requireNonNull(value, "TenantId string cannot be null");
        try {
            return new TenantId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for TenantId", e);
        }
    }
    
    public static TenantId generate() {
        return new TenantId(UUID.randomUUID());
    }
}
