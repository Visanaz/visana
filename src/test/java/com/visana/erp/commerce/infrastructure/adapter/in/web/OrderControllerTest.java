package com.visana.erp.commerce.infrastructure.adapter.in.web;

import com.visana.erp.commerce.application.dto.ConfirmOrderCommand;
import com.visana.erp.commerce.application.port.in.ConfirmOrderPaymentUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.visana.erp.commerce.application.port.in.CreateOrderUseCase createOrderUseCase;

    @Test
    void shouldReturnOkWhenPaymentIsConfirmed() throws Exception {
        UUID orderId = UUID.randomUUID();

        doNothing().when(confirmOrderPaymentUseCase).execute(any(ConfirmOrderCommand.class));

        mockMvc.perform(post("/api/v1/orders/{orderId}/pay", orderId)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", "11111111-1111-1111-1111-111111111111"))))
                .andExpect(status().isOk());
    }
    
    @Test
    void shouldReturnCreatedWhenOrderIsCreated() throws Exception {
        UUID orderId = UUID.randomUUID();

        org.mockito.Mockito.when(createOrderUseCase.execute(any())).thenReturn(orderId);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"affiliateId\":\"11111111-1111-1111-1111-111111111111\", \"orderType\":\"PURCHASE\", \"items\":[]}")
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", "11111111-1111-1111-1111-111111111111"))))
                .andExpect(status().isCreated());
    }
    
    @Test
    void shouldReturnBadRequestWhenDomainThrowsException() throws Exception {
        UUID orderId = UUID.randomUUID();

        // Simulate domain validation error
        doThrow(new IllegalArgumentException("Order not found")).when(confirmOrderPaymentUseCase).execute(any(ConfirmOrderCommand.class));

        // The GlobalExceptionHandler handles IllegalArgumentException with BAD_REQUEST
        mockMvc.perform(post("/api/v1/orders/{orderId}/pay", orderId)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", "11111111-1111-1111-1111-111111111111"))))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldReturnUnauthorizedWhenNoJwtProvided() throws Exception {
        UUID orderId = UUID.randomUUID();

        // Expect 401 Unauthorized since we enforce JWT auth in Spring Security (assumed setup)
        mockMvc.perform(post("/api/v1/orders/{orderId}/pay", orderId).with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
