package com.visana.erp.commerce.domain.model;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private final TenantId tenantId = TenantId.generate();
    private final OrderId orderId = OrderId.generate();
    private final AffiliateId affiliateId = AffiliateId.generate();

    @Test
    void shouldCreateOrderAndCalculateTotal() {
        Order order = new Order(tenantId, orderId, affiliateId, OrderType.COMPRA_DIRECTA);
        
        assertEquals(OrderStatus.PENDING, order.getStatus());
        
        OrderItem item1 = new OrderItem(ProductId.generate(), 2, Money.of(new BigDecimal("50.00")));
        OrderItem item2 = new OrderItem(ProductId.generate(), 1, Money.of(new BigDecimal("100.00")));
        
        order.addItem(item1);
        order.addItem(item2);
        
        Money total = order.calculateTotal();
        
        assertEquals(0, new BigDecimal("200.0000").compareTo(total.amount()));
    }

    @Test
    void shouldConfirmPaymentWhenPendingAndHasItems() {
        Order order = new Order(tenantId, orderId, affiliateId, OrderType.AFILIACION);
        order.addItem(new OrderItem(ProductId.generate(), 1, Money.of(new BigDecimal("200.00"))));
        
        order.confirmPayment();
        
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenConfirmingEmptyOrder() {
        Order order = new Order(tenantId, orderId, affiliateId, OrderType.AFILIACION);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::confirmPayment);
        assertTrue(exception.getMessage().contains("Cannot confirm payment for an empty order"));
    }

    @Test
    void shouldThrowExceptionWhenConfirmingCancelledOrder() {
        Order order = new Order(tenantId, orderId, affiliateId, OrderType.RECOMPRA);
        order.cancel();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::confirmPayment);
        assertTrue(exception.getMessage().contains("Cannot confirm payment for a cancelled order"));
    }
    
    @Test
    void shouldThrowExceptionWhenConfirmingAlreadyPaidOrder() {
        Order order = new Order(tenantId, orderId, affiliateId, OrderType.RECOMPRA);
        order.addItem(new OrderItem(ProductId.generate(), 1, Money.of(new BigDecimal("50.00"))));
        order.confirmPayment();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::confirmPayment);
        assertTrue(exception.getMessage().contains("Order is already paid"));
    }

    @Test
    void shouldThrowExceptionWhenAddingItemsToPaidOrder() {
        Order order = new Order(tenantId, orderId, affiliateId, OrderType.RECOMPRA);
        order.addItem(new OrderItem(ProductId.generate(), 1, Money.of(new BigDecimal("50.00"))));
        order.confirmPayment();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            order.addItem(new OrderItem(ProductId.generate(), 1, Money.of(new BigDecimal("10.00"))));
        });
        assertTrue(exception.getMessage().contains("Cannot add items to an order that is not pending"));
    }
    
    @Test
    void shouldThrowExceptionWhenCancelingPaidOrder() {
        Order order = new Order(tenantId, orderId, affiliateId, OrderType.RECOMPRA);
        order.addItem(new OrderItem(ProductId.generate(), 1, Money.of(new BigDecimal("50.00"))));
        order.confirmPayment();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::cancel);
        assertTrue(exception.getMessage().contains("Cannot cancel an order that is already paid"));
    }
}
