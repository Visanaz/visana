package com.visana.erp.ledger.domain.model;

import java.util.Objects;
import java.util.UUID;

public record LedgerAccountId(UUID value) {
    public LedgerAccountId {
        Objects.requireNonNull(value, "LedgerAccountId value cannot be null");
    }

    public static LedgerAccountId of(UUID value) {
        return new LedgerAccountId(value);
    }

    public static LedgerAccountId of(String value) {
        Objects.requireNonNull(value, "LedgerAccountId string cannot be null");
        try {
            return new LedgerAccountId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for LedgerAccountId", e);
        }
    }

    public static LedgerAccountId generate() {
        return new LedgerAccountId(UUID.randomUUID());
    }
}
