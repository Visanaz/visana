package com.visana.erp.ledger.domain.model;

import java.util.Objects;
import java.util.UUID;

public record TransactionId(UUID value) {
    public TransactionId {
        Objects.requireNonNull(value, "TransactionId value cannot be null");
    }

    public static TransactionId of(UUID value) {
        return new TransactionId(value);
    }

    public static TransactionId of(String value) {
        Objects.requireNonNull(value, "TransactionId string cannot be null");
        try {
            return new TransactionId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for TransactionId", e);
        }
    }

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }
}
