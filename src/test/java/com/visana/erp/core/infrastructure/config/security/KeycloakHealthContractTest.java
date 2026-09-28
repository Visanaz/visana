package com.visana.erp.core.infrastructure.config.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Real tokens/JWKS from the isolated Keycloak runner; never a cloud credential. */
@EnabledIfSystemProperty(named = "visana.keycloak.proof.directory", matches = ".+")
@SpringBootTest(classes = KeycloakHealthContractTest.TestApplication.class)
@AutoConfigureMockMvc(print = org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.NONE)
class KeycloakHealthContractTest {
    private static final Map<String, String> PROOF = readProof();
    @Autowired MockMvc mvc;

    private static Map<String, String> readProof() {
        String directory = System.getProperty("visana.keycloak.proof.directory", "");
        if (directory.isBlank()) return Map.of();
        try {
            return new ObjectMapper().readValue(Files.readString(Path.of(directory, "token-fixture.json")),
                    new TypeReference<Map<String, String>>() {});
        } catch (Exception failure) {
            throw new IllegalStateException("Isolated Keycloak fixture unavailable");
        }
    }

    @DynamicPropertySource static void contract(DynamicPropertyRegistry properties) {
        properties.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> PROOF.get("issuer"));
        properties.add("visana.security.health-client-id", () -> PROOF.get("client"));
        properties.add("visana.security.health-subject", () -> PROOF.get("subject"));
        properties.add("visana.security.health-audience", () -> PROOF.get("audience"));
    }

    @Test void realKeycloakTokenUsesExistingSignatureIssuerAndHealthRestriction() throws Exception {
        String form = "grant_type=client_credentials&scope=visana.health&client_id="
                + URLEncoder.encode(PROOF.get("client"), StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(PROOF.get("clientSecret"), StandardCharsets.UTF_8);
        var response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create(PROOF.get("tokenUri")))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form)).build(), HttpResponse.BodyHandlers.ofString());
        org.junit.jupiter.api.Assertions.assertEquals(200, response.statusCode(), "Synthetic token issuance failed");
        String token = new ObjectMapper().readTree(response.body()).path("access_token").asText();
        // The runner scans for this exact token and removes this private file before keeping artifacts.
        Files.writeString(Path.of(System.getProperty("visana.keycloak.proof.directory"), "proof-runtime-token.txt"), token);
        String bearer = "Bearer " + token;
        mvc.perform(get("/actuator/health").header("Authorization", bearer))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
        for (String path : new String[]{"/api/v1/products", "/api/v1/me", "/api/v1/orders",
                "/api/v1/network/me", "/v3/api-docs", "/actuator/health/db"}) {
            mvc.perform(get(path).header("Authorization", bearer)).andExpect(status().isForbidden());
        }
        mvc.perform(post("/actuator/health").header("Authorization", bearer)).andExpect(status().isForbidden());
        mvc.perform(get("/actuator/health")).andExpect(status().isUnauthorized());
    }

    @Configuration
    @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class})
    @Import({SecurityConfig.class, KeycloakJwtAuthenticationConverter.class})
    static class TestApplication {
        @Bean JwtDecoder realKeycloakDecoder() {
            var decoder = NimbusJwtDecoder.withJwkSetUri(PROOF.get("jwksUri")).build();
            decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(PROOF.get("issuer")));
            return decoder;
        }
    }
}
