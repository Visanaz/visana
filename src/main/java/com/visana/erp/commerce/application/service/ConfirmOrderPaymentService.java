package com.visana.erp.commerce.application.service;

import com.visana.erp.commerce.application.dto.ConfirmOrderCommand;
import com.visana.erp.commerce.application.dto.OrderPaidEvent;
import com.visana.erp.commerce.application.port.in.ConfirmOrderPaymentUseCase;
import com.visana.erp.commerce.application.port.out.DomainEventPublisher;
import com.visana.erp.commerce.application.port.out.OrderRepository;
import com.visana.erp.commerce.domain.model.Order;
import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.core.domain.model.Money;
import java.time.Clock;
import java.util.Objects;

public class ConfirmOrderPaymentService implements ConfirmOrderPaymentUseCase {

    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;
    private final Clock clock;

    public ConfirmOrderPaymentService(
            OrderRepository orderRepository,
            DomainEventPublisher eventPublisher,
            Clock clock) {
        this.orderRepository = Objects.requireNonNull(orderRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public void execute(ConfirmOrderCommand command) {
        OrderId orderId = OrderId.of(command.orderId());
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + command.orderId()));
                
        order.confirmPayment(); // Domain logic validation and state change
        orderRepository.save(order);
        
        Money total = order.calculateTotal();
        OrderPaidEvent event = new OrderPaidEvent(
                order.getTenantId(), order.getOrderId(), order.getAffiliateId(), total, clock.instant());
        eventPublisher.publish(event);
    }
}
