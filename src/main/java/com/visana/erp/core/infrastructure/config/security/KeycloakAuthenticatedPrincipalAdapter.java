package com.visana.erp.core.infrastructure.config.security;

import com.visana.erp.platform.application.identity.AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

/** Converts the current Keycloak/OIDC token at the infrastructure boundary only. */
@Component
public class KeycloakAuthenticatedPrincipalAdapter {
    private static final String OIDC_PROVIDER = "OIDC";

    public AuthenticatedPrincipal from(Jwt jwt, Set<String> roles) {
        Objects.requireNonNull(jwt, "jwt cannot be null");
        if (jwt.getIssuer() == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new IllegalArgumentException("Authenticated OIDC principal requires issuer and subject.");
        }
        return new AuthenticatedPrincipal(OIDC_PROVIDER, jwt.getIssuer().toString(), jwt.getSubject(), roles);
    }
}
