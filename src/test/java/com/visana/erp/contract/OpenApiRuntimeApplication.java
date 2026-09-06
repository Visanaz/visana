package com.visana.erp.contract;

import com.visana.erp.commerce.catalog.application.CatalogQueryService;
import com.visana.erp.commerce.infrastructure.adapter.in.web.CatalogController;
import com.visana.erp.commerce.infrastructure.adapter.in.web.OrderController;
import com.visana.erp.commerce.infrastructure.adapter.in.web.OwnedOrderController;
import com.visana.erp.commerce.order.application.OwnedOrderService;
import com.visana.erp.commerce.application.service.ConfirmOrderPaymentService;
import com.visana.erp.core.infrastructure.adapter.in.web.exception.GlobalExceptionHandler;
import com.visana.erp.core.infrastructure.adapter.in.web.filter.CorrelationIdFilter;
import com.visana.erp.core.infrastructure.config.OpenApiConfig;
import com.visana.erp.core.infrastructure.config.WebFoundationConfig;
import com.visana.erp.core.infrastructure.config.security.KeycloakAuthenticatedPrincipalAdapter;
import com.visana.erp.core.infrastructure.config.security.KeycloakJwtAuthenticationConverter;
import com.visana.erp.core.infrastructure.config.security.SecurityConfig;
import com.visana.erp.network.application.port.in.CreateNetworkNodeUseCase;
import com.visana.erp.network.foundation.application.NetworkFoundationService;
import com.visana.erp.network.foundation.infrastructure.web.NetworkQueryController;
import com.visana.erp.network.infrastructure.adapter.in.web.NetworkNodeController;
import com.visana.erp.platform.application.identity.ActorResolverPort;
import com.visana.erp.platform.application.authorization.AuthorizationPolicy;
import com.visana.erp.platform.infrastructure.adapter.in.web.MeController;
import org.mockito.Mockito;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@SpringBootConfiguration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class})
@Import({
        OpenApiConfig.class, WebFoundationConfig.class, SecurityConfig.class, GlobalExceptionHandler.class, CorrelationIdFilter.class,
        MeController.class, CatalogController.class, OwnedOrderController.class, OrderController.class,
        NetworkQueryController.class, NetworkNodeController.class
})
class OpenApiRuntimeApplication {
    @Bean KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter() { return Mockito.mock(KeycloakJwtAuthenticationConverter.class); }
    @Bean KeycloakAuthenticatedPrincipalAdapter keycloakAuthenticatedPrincipalAdapter() { return Mockito.mock(KeycloakAuthenticatedPrincipalAdapter.class); }
    @Bean ActorResolverPort actorResolverPort() { return Mockito.mock(ActorResolverPort.class); }
    @Bean AuthorizationPolicy authorizationPolicy() { return Mockito.mock(AuthorizationPolicy.class); }
    @Bean CatalogQueryService catalogQueryService() { return Mockito.mock(CatalogQueryService.class); }
    @Bean OwnedOrderService ownedOrderService() { return Mockito.mock(OwnedOrderService.class); }
    @Bean ConfirmOrderPaymentService confirmOrderPaymentService() { return Mockito.mock(ConfirmOrderPaymentService.class); }
    @Bean NetworkFoundationService networkFoundationService() { return Mockito.mock(NetworkFoundationService.class); }
    @Bean CreateNetworkNodeUseCase createNetworkNodeUseCase() { return Mockito.mock(CreateNetworkNodeUseCase.class); }
    @Bean JwtDecoder jwtDecoder() { return Mockito.mock(JwtDecoder.class); }
}
