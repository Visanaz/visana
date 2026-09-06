package com.visana.erp.commerce.infrastructure.adapter.in.web;

import com.visana.erp.commerce.application.dto.ConfirmOrderCommand;
import com.visana.erp.commerce.application.port.in.ConfirmOrderPaymentUseCase;
import com.visana.erp.core.infrastructure.config.security.KeycloakAuthenticatedPrincipalAdapter;
import com.visana.erp.platform.application.authorization.AuthorizationPolicy;
import com.visana.erp.platform.application.identity.ActorResolution;
import com.visana.erp.platform.application.identity.ActorResolverPort;
import com.visana.erp.platform.application.identity.UnlinkedIdentityException;
import com.visana.erp.platform.application.ownership.OwnershipDeniedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Endpoints for managing commerce orders")
public class OrderController {

    private final ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase;
    private final KeycloakAuthenticatedPrincipalAdapter principalAdapter;
    private final ActorResolverPort actorResolver;
    private final AuthorizationPolicy authorizationPolicy;

    public OrderController(ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase,
                           KeycloakAuthenticatedPrincipalAdapter principalAdapter, ActorResolverPort actorResolver,
                           AuthorizationPolicy authorizationPolicy) {
        this.confirmOrderPaymentUseCase = confirmOrderPaymentUseCase;
        this.principalAdapter = principalAdapter;
        this.actorResolver = actorResolver;
        this.authorizationPolicy = authorizationPolicy;
    }

    @PostMapping("/{orderId}/pay")
    @Operation(operationId = "simulateOrderPayment", summary = "Confirm order payment", description = "NON_PRODUCTION / SIMULATED: marks the order as PAID. It does not invoke a payment provider, webhook, settlement, or payout flow.")
    public ResponseEntity<Void> confirmPayment(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID actorId = resolveLinkedActor(jwt);
        if (!authorizationPolicy.canConfirmPayment(actorId, orderId)) {
            throw new OwnershipDeniedException();
        }
        ConfirmOrderCommand command = new ConfirmOrderCommand(orderId);
        confirmOrderPaymentUseCase.execute(command);
        
        return ResponseEntity.ok().build();
    }

    private UUID resolveLinkedActor(Jwt jwt) {
        ActorResolution resolution = actorResolver.resolve(principalAdapter.from(jwt, java.util.Set.of()));
        if (!resolution.isLinked()) {
            throw new UnlinkedIdentityException();
        }
        return resolution.actorId().value();
    }
    
}
