package com.visana.erp.commerce.application.service;

import com.visana.erp.commerce.application.dto.ConfirmOrderCommand;
import com.visana.erp.commerce.application.dto.OrderPaidEvent;
import com.visana.erp.commerce.application.port.in.ConfirmOrderPaymentUseCase;
import com.visana.erp.commerce.application.port.out.DomainEventPublisher;
import com.visana.erp.commerce.application.port.out.OrderRepository;
import com.visana.erp.commerce.domain.model.Order;
import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.core.domain.model.Money;

public class ConfirmOrderPaymentService implements ConfirmOrderPaymentUseCase {

    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;

    public ConfirmOrderPaymentService(OrderRepository orderRepository, DomainEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(ConfirmOrderCommand command) {
        OrderId orderId = OrderId.of(command.orderId());
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + command.orderId()));
                
        order.confirmPayment(); // Domain logic validation and state change
        orderRepository.save(order);
        
        Money total = order.calculateTotal();
        OrderPaidEvent event = new OrderPaidEvent(order.getTenantId(), order.getOrderId(), order.getAffiliateId(), total);
        eventPublisher.publish(event);
    }
}
