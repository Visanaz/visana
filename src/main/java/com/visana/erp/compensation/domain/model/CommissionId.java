package com.visana.erp.compensation.domain.model;

import java.util.Objects;
import java.util.UUID;

public record CommissionId(UUID value) {
    public CommissionId {
        Objects.requireNonNull(value, "CommissionId value cannot be null");
    }

    public static CommissionId of(UUID value) {
        return new CommissionId(value);
    }

    public static CommissionId of(String value) {
        Objects.requireNonNull(value, "CommissionId string cannot be null");
        try {
            return new CommissionId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for CommissionId", e);
        }
    }

    public static CommissionId generate() {
        return new CommissionId(UUID.randomUUID());
    }
}
