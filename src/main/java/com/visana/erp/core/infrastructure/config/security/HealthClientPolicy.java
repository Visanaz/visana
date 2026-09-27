package com.visana.erp.core.infrastructure.config.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/** Proposed signed IdP contract; no role can override the technical identity restriction. */
public final class HealthClientPolicy implements OAuth2TokenValidator<Jwt> {
    public static final String SCOPE = "visana.health";
    private final String clientId;
    private final String subject;
    private final String audience;

    public HealthClientPolicy(String clientId, String subject, String audience) {
        this.clientId = clientId;
        this.subject = subject;
        this.audience = audience;
        long configured = List.of(clientId, subject, audience).stream().filter(v -> !v.isBlank()).count();
        if (configured != 0 && (configured != 3 || List.of(clientId, subject, audience).stream()
                .anyMatch(v -> v.contains("${") || v.chars().anyMatch(Character::isWhitespace)))) {
            throw new IllegalArgumentException("Incomplete technical health identity contract");
        }
    }

    public boolean technical(Jwt jwt) {
        if (clientId.isBlank()) return false;
        Map<String, Object> claims = jwt.getClaims();
        Object aud = claims.get("aud");
        return subject.equals(claims.get("sub")) || clientId.equals(claims.get("azp"))
                || clientId.equals(claims.get("client_id")) || audience.equals(aud)
                || (aud instanceof Collection<?> values && values.contains(audience))
                || scopes(claims.get("scope")).contains(SCOPE);
    }

    private List<String> scopes(Object claim) {
        return claim instanceof String value ? List.of(value.split("\\s+")) : List.of();
    }

    @Override public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (!technical(jwt)) return OAuth2TokenValidatorResult.success();
        Map<String, Object> claims = jwt.getClaims();
        Object client = claims.get("client_id");
        Object aud = claims.get("aud");
        boolean exactAudience = audience.equals(aud) || (aud instanceof Collection<?> values
                && values.size() == 1 && values.contains(audience));
        if (subject.equals(claims.get("sub")) && clientId.equals(claims.get("azp"))
                && (client == null || clientId.equals(client)) && exactAudience
                && scopes(claims.get("scope")).contains(SCOPE)) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid technical health contract", null));
    }
}
