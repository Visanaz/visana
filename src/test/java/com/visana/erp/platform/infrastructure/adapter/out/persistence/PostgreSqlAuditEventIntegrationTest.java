package com.visana.erp.platform.infrastructure.adapter.out.persistence;

import com.visana.erp.platform.domain.audit.AuditEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class PostgreSqlAuditEventIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired private PersistentAuditEventWriter writer;
    @Autowired private AuditEventSpringDataRepository repository;
    @Autowired private JdbcTemplate jdbcTemplate;

    @Test
    void appliesFlywayMigrationsAndPersistsSanitizedAuditEvent() {
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from flyway_schema_history where version = '1'", Integer.class));
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from flyway_schema_history where version = '2'", Integer.class));

        writer.append(new AuditEvent("actor", "ORDER_ACCESS", "ORDER", "resource", Instant.parse("2026-09-03T00:00:00Z"),
                "correlation", Map.of("result", "allowed", "token", "discard")));

        AuditEventJpaEntity persisted = repository.findAll().getFirst();
        assertEquals("actor", persisted.actorId());
        assertEquals("ORDER_ACCESS", persisted.action());
        assertEquals("ORDER", persisted.resourceType());
        assertEquals("resource", persisted.resourceId());
        assertEquals("correlation", persisted.correlationId());
        assertTrue(persisted.metadata().contains("allowed"));
        assertFalse(persisted.metadata().contains("discard"));
    }
}
