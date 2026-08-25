package com.visana.erp.commerce.application.service;

import com.visana.erp.commerce.application.dto.ConfirmOrderCommand;
import com.visana.erp.commerce.application.dto.OrderPaidEvent;
import com.visana.erp.commerce.application.port.out.DomainEventPublisher;
import com.visana.erp.commerce.application.port.out.OrderRepository;
import com.visana.erp.commerce.domain.model.Order;
import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.commerce.domain.model.OrderItem;
import com.visana.erp.commerce.domain.model.OrderType;
import com.visana.erp.commerce.domain.model.ProductId;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmOrderPaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    private ConfirmOrderPaymentService service;
    private OrderId orderId;
    private Order order;

    @BeforeEach
    void setUp() {
        service = new ConfirmOrderPaymentService(orderRepository, eventPublisher);
        orderId = OrderId.generate();
        order = new Order(TenantId.generate(), orderId, AffiliateId.generate(), OrderType.PURCHASE);
        order.addItem(new OrderItem(ProductId.generate(), 1, Money.of(new BigDecimal("100.00"))));
    }

    @Test
    void shouldConfirmPaymentAndPublishEvent() {
        UUID rawId = orderId.value();
        ConfirmOrderCommand command = new ConfirmOrderCommand(rawId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        service.execute(command);

        verify(orderRepository).save(order);
        
        ArgumentCaptor<OrderPaidEvent> eventCaptor = ArgumentCaptor.forClass(OrderPaidEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());

        OrderPaidEvent capturedEvent = eventCaptor.getValue();
        assertEquals(order.getTenantId(), capturedEvent.tenantId());
        assertEquals(orderId, capturedEvent.orderId());
        assertEquals(order.getAffiliateId(), capturedEvent.affiliateId());
        assertEquals(0, new BigDecimal("100.0000").compareTo(capturedEvent.total().amount()));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        UUID rawId = UUID.randomUUID();
        ConfirmOrderCommand command = new ConfirmOrderCommand(rawId);

        when(orderRepository.findById(OrderId.of(rawId))).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.execute(command));
        assertTrue(exception.getMessage().contains("Order not found"));

        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }
}
