package com.visana.erp.network.domain.model;

import java.util.Objects;
import java.util.UUID;

public record AffiliateId(UUID value) {
    public AffiliateId {
        Objects.requireNonNull(value, "AffiliateId value cannot be null");
    }

    public static AffiliateId of(UUID value) {
        return new AffiliateId(value);
    }

    public static AffiliateId of(String value) {
        Objects.requireNonNull(value, "AffiliateId string cannot be null");
        try {
            return new AffiliateId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for AffiliateId", e);
        }
    }
    
    public static AffiliateId generate() {
        return new AffiliateId(UUID.randomUUID());
    }
}
