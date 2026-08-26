package com.visana.erp.commerce.infrastructure.adapter.in.web;

import com.visana.erp.commerce.application.dto.ConfirmOrderCommand;
import com.visana.erp.commerce.application.dto.CreateOrderCommand;
import com.visana.erp.commerce.application.port.in.ConfirmOrderPaymentUseCase;
import com.visana.erp.commerce.application.port.in.CreateOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Endpoints for managing commerce orders")
public class OrderController {

    private final ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase;
    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase, CreateOrderUseCase createOrderUseCase) {
        this.confirmOrderPaymentUseCase = confirmOrderPaymentUseCase;
        this.createOrderUseCase = createOrderUseCase;
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Creates a new order in PENDING status for the specified affiliate.")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        UUID tenantId = UUID.fromString(jwt.getClaimAsString("tenant_id"));
        CreateOrderCommand command = new CreateOrderCommand(tenantId, request.affiliateId(), request.orderType(), request.items());
        UUID orderId = createOrderUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(new OrderResponse(orderId));
    }
    
    public record CreateOrderRequest(
            UUID affiliateId,
            String orderType,
            java.util.List<com.visana.erp.commerce.application.dto.OrderItemDto> items
    ) {}

    @PostMapping("/{orderId}/pay")
    @Operation(summary = "Confirm order payment", description = "Simulates payment confirmation and marks the order as PAID.")
    public ResponseEntity<Void> confirmPayment(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal Jwt jwt) {
        
        // Extract tenant ID for multitenancy logic if required in the future by the Use Case
        // String empresaId = jwt.getClaimAsString("empresa_id");
        
        ConfirmOrderCommand command = new ConfirmOrderCommand(orderId);
        confirmOrderPaymentUseCase.execute(command);
        
        return ResponseEntity.ok().build();
    }
    
    public record OrderResponse(UUID orderId) {}
}
