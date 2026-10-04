package com.visana.erp.commerce.infrastructure.adapter.out.persistence;

import com.visana.erp.commerce.catalog.application.CatalogProductPort;
import com.visana.erp.commerce.catalog.domain.*;
import com.visana.erp.commerce.catalog.infrastructure.persistence.CatalogPersistenceAdapter;
import com.visana.erp.commerce.order.application.OwnedOrderService;
import com.visana.erp.commerce.order.application.CreateOwnedOrderCommand;
import com.visana.erp.commerce.order.domain.OwnedOrder;
import com.visana.erp.commerce.order.infrastructure.persistence.OwnedOrderPersistenceAdapter;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.platform.application.authorization.PersistentOwnershipAuthorizationPolicy;
import com.visana.erp.platform.application.identity.IdentityProvisioningService;
import com.visana.erp.platform.application.identity.IdentityResolutionMetrics;
import com.visana.erp.platform.application.ownership.OwnershipDeniedException;
import com.visana.erp.platform.application.ownership.ResourceOwnershipService;
import com.visana.erp.platform.domain.identity.ExternalIdentity;
import com.visana.erp.platform.domain.identity.PlatformActor;
import com.visana.erp.platform.infrastructure.adapter.out.persistence.PersistentAuditEventWriter;
import com.visana.erp.platform.infrastructure.adapter.out.persistence.identity.IdentityPersistenceAdapter;
import com.visana.erp.platform.infrastructure.adapter.out.persistence.identity.ResourceOwnershipPersistenceAdapter;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PostgreSqlOwnedCommerceIntegrationTest.CommerceTestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class PostgreSqlOwnedCommerceIntegrationTest {
 @Container static final PostgreSQLContainer<?> POSTGRES=new PostgreSQLContainer<>("postgres:18.3-alpine");
    @org.junit.jupiter.api.BeforeAll
    static void verifyRealServerVersion() throws Exception {
        try (var connection = java.sql.DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {
            org.junit.jupiter.api.Assertions.assertEquals(18, connection.getMetaData().getDatabaseMajorVersion());
            System.out.println("POSTGRESQL18_EVIDENCE " + connection.getMetaData().getDatabaseProductVersion());
        }
    }

 @DynamicPropertySource static void datasource(DynamicPropertyRegistry r){r.add("spring.datasource.url",POSTGRES::getJdbcUrl);r.add("spring.datasource.username",POSTGRES::getUsername);r.add("spring.datasource.password",POSTGRES::getPassword);r.add("spring.jpa.hibernate.ddl-auto",()->"validate");r.add("spring.sql.init.mode",()->"never");}
 @Autowired IdentityProvisioningService provisioning; @Autowired CatalogProductPort catalog; @Autowired OwnedOrderService orders; @Autowired JdbcTemplate jdbc;
 @Test void linkedActorCreatesReadsOwnOrderAndForeignActorIsDeniedOnPostgreSql(){PlatformActor owner=provisioning.provisionAndLink(new ExternalIdentity("OIDC","https://issuer.test","owner"),"test");PlatformActor foreign=provisioning.provisionAndLink(new ExternalIdentity("OIDC","https://issuer.test","foreign"),"test");UUID productId=UUID.randomUUID();Instant now=Instant.now();catalog.save(new CatalogProduct(productId,"SKU-1","CODE-1","Synthetic",null,Money.of("15000"),CatalogProductStatus.ACTIVE,null,now,now));OwnedOrder order=orders.create(owner.id().value(),new CreateOwnedOrderCommand(List.of(new CreateOwnedOrderCommand.Line(productId,2))));assertEquals(0,order.total().amount().compareTo(Money.of("30000").amount()));assertEquals(order.id(),orders.read(owner.id().value(),order.id()).id());assertThrows(OwnershipDeniedException.class,()->orders.read(foreign.id().value(),order.id()));assertEquals(1,jdbc.queryForObject("select count(*) from flyway_schema_history where version = '5'",Integer.class));assertEquals(1,jdbc.queryForObject("select count(*) from flyway_schema_history where version = '6'",Integer.class));assertEquals(1,jdbc.queryForObject("select count(*) from commerce_order_lines",Integer.class));assertTrue(jdbc.queryForObject("select count(*) from platform_audit_events where action = 'ORDER_CREATED'",Integer.class)>=1);}
 @Configuration @EnableAutoConfiguration @EntityScan(basePackages="com.visana.erp") @EnableJpaRepositories(basePackages="com.visana.erp")
 @Import({PersistentAuditEventWriter.class,IdentityPersistenceAdapter.class,ResourceOwnershipPersistenceAdapter.class,IdentityResolutionMetrics.class,com.visana.erp.platform.application.identity.ActorResolverService.class,IdentityProvisioningService.class,ResourceOwnershipService.class,PersistentOwnershipAuthorizationPolicy.class,CatalogPersistenceAdapter.class,OwnedOrderPersistenceAdapter.class,OwnedOrderService.class}) static class CommerceTestApplication{}
}
