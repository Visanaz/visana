package com.visana.erp.platform.domain.identity;

import java.util.Objects;

/**
 * Stable external identity key. Email, username and display name are intentionally excluded.
 */
public record ExternalIdentity(String provider, String issuer, String subject) {
    public ExternalIdentity {
        provider = required(provider, "provider");
        issuer = required(issuer, "issuer");
        subject = required(subject, "subject");
    }

    private static String required(String value, String label) {
        Objects.requireNonNull(value, label + " cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be blank");
        }
        return value;
    }
}
