package com.visana.erp.core.infrastructure.config.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TechnicalHealthSecurityTest.TestApplication.class, properties = {
        "visana.security.health-client-id=fixture-health-client", "visana.security.health-subject=fixture-health-subject",
        "visana.security.health-audience=fixture-health-audience"})
@AutoConfigureMockMvc(print = org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.NONE)
class TechnicalHealthSecurityTest {
    static final SignedJwtFixture TOKENS = new SignedJwtFixture();
    @Autowired MockMvc mvc;
    @AfterAll static void close() { TOKENS.close(); }

    @Test void devCannotDisableTheContractWithEmptyOrPartialConfiguration() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> new HealthClientPolicy("", "", "", true));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> new HealthClientPolicy("fixture", "", "", true));
    }

    @Test void onlyExactGetHealthIsAllowedEvenWithAdministrativeRoles() throws Exception {
        Map<String, Object> claims = new HashMap<>(TOKENS.technicalClaims());
        claims.put("realm_access", Map.of("roles", List.of("admin", "distributor")));
        String token = TOKENS.token(claims);
        mvc.perform(get("/actuator/health").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
        for (String path : List.of("/api/v1/products", "/api/v1/products/fixture", "/api/v1/categories", "/api/v1/me",
                "/api/v1/orders", "/api/v1/orders/fixture/pay", "/api/v1/network/me", "/api/v1/network/direct",
                "/api/v1/network/ancestors", "/api/v1/network/descendants", "/api/v1/network-nodes", "/v3/api-docs", "/actuator/info",
                "/actuator/health/db", "/actuator/health/")) {
            mvc.perform(get(path).header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
        }
        mvc.perform(post("/actuator/health").header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
        for (String path : List.of("/api/v1/orders", "/api/v1/orders/fixture/pay", "/api/v1/network-nodes")) {
            mvc.perform(post(path).header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
        }
    }

    @Test void missingAndConflictingTechnicalClaimsNeverFallBackToBusinessAccess() throws Exception {
        for (String claim : List.of("sub", "azp", "aud", "scope")) {
            Map<String, Object> incomplete = new HashMap<>(TOKENS.technicalClaims());
            incomplete.remove(claim);
            mvc.perform(get("/api/v1/products").header("Authorization", "Bearer " + TOKENS.token(incomplete))).andExpect(status().isUnauthorized());
            mvc.perform(get("/actuator/health").header("Authorization", "Bearer " + TOKENS.token(incomplete))).andExpect(status().isUnauthorized());
        }
        for (Map<String, Object> conflict : List.<Map<String, Object>>of(Map.of("client_id", "other-client"), Map.of("aud", List.of(SignedJwtFixture.AUDIENCE, "business-api")),
                Map.of("sub", "different-subject"), Map.of("scope", List.of("visana.health")))) {
            Map<String, Object> claims = new HashMap<>(TOKENS.technicalClaims());
            claims.putAll(conflict);
            mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + TOKENS.token(claims))).andExpect(status().isUnauthorized());
        }
        Map<String, Object> stripped = Map.of("sub", SignedJwtFixture.SUBJECT);
        mvc.perform(get("/api/v1/products").header("Authorization", "Bearer " + TOKENS.token(stripped))).andExpect(status().isUnauthorized());
    }

    @Test void cryptographicIssuerExpiryAndAudienceChecksUseTheRealDecoder() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isUnauthorized());
        mvc.perform(get("/actuator/health").header("Authorization", "Bearer invalid.fixture.token")).andExpect(status().isUnauthorized());
        Map<String, Object> claims = new HashMap<>(TOKENS.technicalClaims());
        claims.put("iss", "https://untrusted.example.invalid");
        mvc.perform(get("/actuator/health").header("Authorization", "Bearer " + TOKENS.token(claims))).andExpect(status().isUnauthorized());
        claims = new HashMap<>(TOKENS.technicalClaims());
        claims.put("exp", java.util.Date.from(java.time.Instant.now().minusSeconds(300)));
        mvc.perform(get("/actuator/health").header("Authorization", "Bearer " + TOKENS.token(claims))).andExpect(status().isUnauthorized());
        try (SignedJwtFixture untrusted = new SignedJwtFixture()) {
            claims = new HashMap<>(TOKENS.technicalClaims()); claims.put("iss", TOKENS.issuer);
            mvc.perform(get("/actuator/health").header("Authorization", "Bearer " + untrusted.token(claims))).andExpect(status().isUnauthorized());
        }
    }

    @Test void legitimateUsersAndAnonymousDocumentationKeepTheirPreviousPolicy() throws Exception {
        String human = TOKENS.token(Map.of("sub", "fixture-human", "azp", "fixture-web", "aud", List.of("business-api")));
        for (String path : List.of("/api/v1/products", "/api/v1/categories", "/api/v1/me")) {
            mvc.perform(get(path).header("Authorization", "Bearer " + human)).andExpect(status().isOk());
        }
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/products")).andExpect(status().isUnauthorized());
    }

    @Configuration
    @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class})
    @Import({SecurityConfig.class, KeycloakJwtAuthenticationConverter.class, FixtureController.class})
    static class TestApplication {
        @Bean JwtDecoder signedDecoder(HealthClientPolicy policy) { return TOKENS.decoder(policy); }
    }
    @RestController
    static class FixtureController {
        @RequestMapping({"/api/v1/products", "/api/v1/categories", "/api/v1/me", "/v3/api-docs"})
        Map<String, String> fixture() { return Map.of("status", "fixture"); }
    }
}
