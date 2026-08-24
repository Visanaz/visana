package com.visana.erp.network.domain.model;

import java.util.Objects;
import java.util.UUID;

public record SponsorId(UUID value) {
    public SponsorId {
        Objects.requireNonNull(value, "SponsorId value cannot be null");
    }

    public static SponsorId of(UUID value) {
        return new SponsorId(value);
    }

    public static SponsorId of(String value) {
        Objects.requireNonNull(value, "SponsorId string cannot be null");
        try {
            return new SponsorId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for SponsorId", e);
        }
    }
    
    public static SponsorId generate() {
        return new SponsorId(UUID.randomUUID());
    }
}
