package com.visana.erp.platform.infrastructure.adapter.out.persistence;

import com.visana.erp.platform.application.audit.AuditEventWriter;
import com.visana.erp.platform.application.authorization.PersistentOwnershipAuthorizationPolicy;
import com.visana.erp.platform.application.identity.ActorResolutionStatus;
import com.visana.erp.platform.application.identity.ActorResolverPort;
import com.visana.erp.platform.application.identity.IdentityConflictException;
import com.visana.erp.platform.application.identity.IdentityProvisioningService;
import com.visana.erp.platform.application.identity.IdentityResolutionMetrics;
import com.visana.erp.platform.application.ownership.ResourceOwnershipService;
import com.visana.erp.platform.domain.identity.ExternalIdentity;
import com.visana.erp.platform.domain.identity.PlatformActor;
import com.visana.erp.platform.infrastructure.adapter.out.persistence.identity.ExternalIdentityLinkJpaEntity;
import com.visana.erp.platform.infrastructure.adapter.out.persistence.identity.ExternalIdentityLinkSpringDataRepository;
import com.visana.erp.platform.infrastructure.adapter.out.persistence.identity.IdentityPersistenceAdapter;
import com.visana.erp.platform.infrastructure.adapter.out.persistence.identity.PlatformActorJpaEntity;
import com.visana.erp.platform.infrastructure.adapter.out.persistence.identity.PlatformActorSpringDataRepository;
import com.visana.erp.platform.infrastructure.adapter.out.persistence.identity.ResourceOwnershipJpaEntity;
import com.visana.erp.platform.infrastructure.adapter.out.persistence.identity.ResourceOwnershipPersistenceAdapter;
import com.visana.erp.platform.infrastructure.adapter.out.persistence.identity.ResourceOwnershipSpringDataRepository;
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

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = PostgreSqlIdentityOwnershipIntegrationTest.IdentityOwnershipTestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class PostgreSqlIdentityOwnershipIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.sql.init.mode", () -> "never");
    }

    @Autowired private IdentityProvisioningService provisioningService;
    @Autowired private ActorResolverPort actorResolver;
    @Autowired private ResourceOwnershipService ownershipService;
    @Autowired private PersistentOwnershipAuthorizationPolicy authorizationPolicy;
    @Autowired private JdbcTemplate jdbcTemplate;

    @Test
    void validatesIdentityLinksOwnershipAndFailClosedAuthorizationOnPostgreSql() {
        ExternalIdentity identity = new ExternalIdentity("OIDC", "https://issuer.test", "subject-1");
        PlatformActor actor = provisioningService.provisionAndLink(identity, "test-correlation");

        assertEquals(ActorResolutionStatus.LINKED, actorResolver.resolve(
                new com.visana.erp.platform.application.identity.AuthenticatedPrincipal("OIDC", "https://issuer.test", "subject-1", Set.of())).status());
        assertEquals(ActorResolutionStatus.UNLINKED_IDENTITY, actorResolver.resolve(
                new com.visana.erp.platform.application.identity.AuthenticatedPrincipal("OIDC", "https://issuer.test", "unknown", Set.of())).status());
        assertThrows(IdentityConflictException.class, () -> provisioningService.provisionAndLink(identity, "test-correlation"));

        UUID orderId = UUID.randomUUID();
        ownershipService.assign("ORDER", orderId, actor.id(), "test-correlation");
        assertTrue(authorizationPolicy.canConfirmPayment(actor.id().value(), orderId));
        assertFalse(authorizationPolicy.canConfirmPayment(UUID.randomUUID(), orderId));

        provisioningService.disableLink(identity, "test-correlation");
        assertEquals(ActorResolutionStatus.DISABLED_IDENTITY, actorResolver.resolve(
                new com.visana.erp.platform.application.identity.AuthenticatedPrincipal("OIDC", "https://issuer.test", "subject-1", Set.of())).status());
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from flyway_schema_history where version = '3'", Integer.class));
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from flyway_schema_history where version = '4'", Integer.class));
        assertTrue(jdbcTemplate.queryForObject("select count(*) from platform_audit_events where action = 'IDENTITY_LINK_CREATED'", Integer.class) >= 1);
        assertTrue(jdbcTemplate.queryForObject("select count(*) from platform_audit_events where action = 'AUTHORIZATION_DENIED'", Integer.class) >= 1);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = {AuditEventJpaEntity.class, PlatformActorJpaEntity.class, ExternalIdentityLinkJpaEntity.class, ResourceOwnershipJpaEntity.class})
    @EnableJpaRepositories(basePackageClasses = {AuditEventSpringDataRepository.class, PlatformActorSpringDataRepository.class,
            ExternalIdentityLinkSpringDataRepository.class, ResourceOwnershipSpringDataRepository.class})
    @Import({PersistentAuditEventWriter.class, IdentityPersistenceAdapter.class, ResourceOwnershipPersistenceAdapter.class,
            IdentityResolutionMetrics.class, com.visana.erp.platform.application.identity.ActorResolverService.class,
            IdentityProvisioningService.class, ResourceOwnershipService.class, PersistentOwnershipAuthorizationPolicy.class})
    static class IdentityOwnershipTestApplication { }
}
