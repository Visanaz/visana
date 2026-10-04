package com.visana.erp.platform.infrastructure.adapter.out.persistence;

import com.visana.erp.network.foundation.application.NetworkFoundationService;
import com.visana.erp.network.foundation.application.NetworkOperationForbiddenException;
import com.visana.erp.network.foundation.domain.NetworkMember;
import com.visana.erp.network.foundation.infrastructure.persistence.BusinessProfileActorLinkJpaEntity;
import com.visana.erp.network.foundation.infrastructure.persistence.BusinessProfileActorLinkRepository;
import com.visana.erp.network.foundation.infrastructure.persistence.BusinessProfileJpaEntity;
import com.visana.erp.network.foundation.infrastructure.persistence.BusinessProfileRepository;
import com.visana.erp.network.foundation.infrastructure.persistence.NetworkMemberJpaEntity;
import com.visana.erp.network.foundation.infrastructure.persistence.NetworkMemberRepository;
import com.visana.erp.network.foundation.infrastructure.persistence.SponsorRelationshipJpaEntity;
import com.visana.erp.network.foundation.infrastructure.persistence.SponsorRelationshipRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = PostgreSqlNetworkFoundationIntegrationTest.NetworkFoundationTestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class PostgreSqlNetworkFoundationIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:18.3-alpine");

    @org.junit.jupiter.api.BeforeAll
    static void verifyRealServerVersion() throws Exception {
        try (var connection = java.sql.DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {
            org.junit.jupiter.api.Assertions.assertEquals(18, connection.getMetaData().getDatabaseMajorVersion());
            System.out.println("POSTGRESQL18_EVIDENCE " + connection.getMetaData().getDatabaseProductVersion());
        }
    }

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.sql.init.mode", () -> "never");
    }

    @Autowired NetworkFoundationService network;
    @Autowired JdbcTemplate jdbc;

    @Test
    void validatesV7ClosureQueriesAndNetworkInvariantsOnPostgreSql() {
        UUID actorA = actor();
        UUID actorB = actor();
        UUID actorC = actor();
        NetworkMember memberA = network.establishMemberForActor(actorA);
        NetworkMember memberB = network.establishMemberForActor(actorB);
        NetworkMember memberC = network.establishMemberForActor(actorC);

        assertThrows(IllegalArgumentException.class,
                () -> network.assignInitialSponsor(actorA, memberA.id(), memberA.id()));
        network.assignInitialSponsor(actorA, memberA.id(), memberB.id());
        assertThrows(NetworkOperationForbiddenException.class,
                () -> network.assignInitialSponsor(actorA, memberA.id(), memberB.id()));
        network.assignInitialSponsor(actorB, memberB.id(), memberC.id());
        assertThrows(IllegalArgumentException.class,
                () -> network.assignInitialSponsor(actorC, memberC.id(), memberA.id()));
        assertThrows(NetworkOperationForbiddenException.class,
                () -> network.assignInitialSponsor(actorA, memberA.id(), memberC.id()));

        assertEquals(List.of(memberB.id()), network.direct(memberA.id(), 50));
        assertEquals(List.of(new NetworkFoundationService.MemberDepth(memberB.id(), 1),
                        new NetworkFoundationService.MemberDepth(memberA.id(), 2)), network.ancestors(memberC.id(), 50));
        assertEquals(List.of(new NetworkFoundationService.MemberDepth(memberB.id(), 1),
                        new NetworkFoundationService.MemberDepth(memberC.id(), 2)), network.descendants(memberA.id(), 50));
        assertEquals(6, jdbc.queryForObject("select count(*) from genealogy_closure", Integer.class));
        assertEquals(2, jdbc.queryForObject("select count(*) from sponsor_relationships", Integer.class));
        assertEquals(3, jdbc.queryForObject("select count(*) from business_profiles", Integer.class));
        assertEquals(3, jdbc.queryForObject("select count(*) from business_profile_actor_links", Integer.class));
        assertEquals(3, jdbc.queryForObject("select count(*) from network_members", Integer.class));
        assertEquals(1, jdbc.queryForObject("select count(*) from flyway_schema_history where version = '7'", Integer.class));
        assertTrue(jdbc.queryForObject("select count(*) from platform_audit_events where action = 'SPONSOR_ASSIGNED'", Integer.class) >= 2);
    }

    private UUID actor() {
        UUID actor = UUID.randomUUID();
        jdbc.update("insert into platform_actors(actor_id, status, created_at) values (?, ?, ?)",
                actor.toString(), "ACTIVE", Timestamp.from(Instant.now()));
        return actor;
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = {AuditEventJpaEntity.class, BusinessProfileJpaEntity.class,
            BusinessProfileActorLinkJpaEntity.class, NetworkMemberJpaEntity.class, SponsorRelationshipJpaEntity.class})
    @EnableJpaRepositories(basePackageClasses = {AuditEventSpringDataRepository.class, BusinessProfileRepository.class,
            BusinessProfileActorLinkRepository.class, NetworkMemberRepository.class, SponsorRelationshipRepository.class})
    @Import({PersistentAuditEventWriter.class, NetworkFoundationService.class})
    static class NetworkFoundationTestApplication { }
}
