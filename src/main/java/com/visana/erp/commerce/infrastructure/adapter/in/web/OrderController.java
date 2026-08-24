package com.visana.erp.commerce.infrastructure.adapter.in.web;

import com.visana.erp.commerce.application.dto.ConfirmOrderCommand;
import com.visana.erp.commerce.application.port.in.ConfirmOrderPaymentUseCase;
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
public class OrderController {

    private final ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase;

    public OrderController(ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase) {
        this.confirmOrderPaymentUseCase = confirmOrderPaymentUseCase;
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Void> confirmPayment(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal Jwt jwt) {
        
        // Extract tenant ID for multitenancy logic if required in the future by the Use Case
        // String empresaId = jwt.getClaimAsString("empresa_id");
        
        ConfirmOrderCommand command = new ConfirmOrderCommand(orderId);
        confirmOrderPaymentUseCase.execute(command);
        
        return ResponseEntity.ok().build();
    }
}
