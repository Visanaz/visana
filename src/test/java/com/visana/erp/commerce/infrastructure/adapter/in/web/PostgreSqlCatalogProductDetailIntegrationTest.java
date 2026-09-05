package com.visana.erp.commerce.infrastructure.adapter.in.web;

import com.visana.erp.commerce.catalog.application.CatalogProductPort;
import com.visana.erp.commerce.catalog.application.CatalogQueryService;
import com.visana.erp.commerce.catalog.domain.CatalogProduct;
import com.visana.erp.commerce.catalog.domain.CatalogProductStatus;
import com.visana.erp.commerce.catalog.infrastructure.persistence.CatalogPersistenceAdapter;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.infrastructure.adapter.in.web.exception.GlobalExceptionHandler;
import com.visana.erp.core.infrastructure.adapter.in.web.filter.CorrelationIdFilter;
import com.visana.erp.core.infrastructure.config.WebFoundationConfig;
import com.visana.erp.core.infrastructure.config.security.KeycloakJwtAuthenticationConverter;
import com.visana.erp.core.infrastructure.config.security.SecurityConfig;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PostgreSqlCatalogProductDetailIntegrationTest.CatalogDetailTestApplication.class)
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class PostgreSqlCatalogProductDetailIntegrationTest {
    @Container static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.sql.init.mode", () -> "never");
    }

    @Autowired MockMvc mvc;
    @Autowired CatalogProductPort products;
    @MockBean JwtDecoder jwtDecoder;
    @MockBean KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;

    @Test void persistedActiveProductIsReturnedByTheAuthenticatedDetailEndpoint() throws Exception {
        UUID productId = UUID.randomUUID();
        Instant now = Instant.parse("2026-09-04T00:00:00Z");
        products.save(new CatalogProduct(productId, "SKU-IT-1", "CODE-IT-1", "Integration product", "Persisted fixture",
                Money.of("27000"), CatalogProductStatus.ACTIVE, null, now, now));

        mvc.perform(get("/api/v1/products/{id}", productId).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId.toString()))
                .andExpect(jsonPath("$.code").value("CODE-IT-1"))
                .andExpect(jsonPath("$.basePrice").value(27000));
    }

    @Test void missingProductIsReturnedAsProblemDetailNotFound() throws Exception {
        mvc.perform(get("/api/v1/products/{id}", UUID.randomUUID()).with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackages = "com.visana.erp")
    @EnableJpaRepositories(basePackages = "com.visana.erp")
    @Import({CatalogPersistenceAdapter.class, CatalogQueryService.class, CatalogController.class,
            GlobalExceptionHandler.class, CorrelationIdFilter.class, WebFoundationConfig.class, SecurityConfig.class})
    static class CatalogDetailTestApplication { }
}
