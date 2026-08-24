package com.visana.erp.ledger.domain.model;

import java.util.Objects;
import java.util.UUID;

public record PayoutId(UUID value) {
    public PayoutId {
        Objects.requireNonNull(value, "PayoutId value cannot be null");
    }

    public static PayoutId of(UUID value) {
        return new PayoutId(value);
    }

    public static PayoutId of(String value) {
        Objects.requireNonNull(value, "PayoutId string cannot be null");
        try {
            return new PayoutId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for PayoutId", e);
        }
    }

    public static PayoutId generate() {
        return new PayoutId(UUID.randomUUID());
    }
}
