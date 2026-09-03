package com.visana.erp.platform.application.identity;

import com.visana.erp.platform.domain.identity.ExternalIdentity;

import java.util.Objects;
import java.util.Set;

/** Transport-neutral representation of an already authenticated OIDC principal. */
public record AuthenticatedPrincipal(String provider, String issuer, String subject, Set<String> roles) {
    public AuthenticatedPrincipal {
        Objects.requireNonNull(provider, "provider cannot be null");
        Objects.requireNonNull(issuer, "issuer cannot be null");
        Objects.requireNonNull(subject, "subject cannot be null");
        roles = roles == null ? Set.of() : Set.copyOf(roles);
    }

    public ExternalIdentity externalIdentity() {
        return new ExternalIdentity(provider, issuer, subject);
    }
}
