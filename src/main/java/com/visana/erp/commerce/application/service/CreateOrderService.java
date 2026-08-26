package com.visana.erp.commerce.application.service;

import com.visana.erp.commerce.application.dto.CreateOrderCommand;
import com.visana.erp.commerce.application.dto.OrderItemDto;
import com.visana.erp.commerce.application.port.in.CreateOrderUseCase;
import com.visana.erp.commerce.application.port.out.OrderRepository;
import com.visana.erp.commerce.domain.model.Order;
import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.commerce.domain.model.OrderItem;
import com.visana.erp.commerce.domain.model.OrderType;
import com.visana.erp.commerce.domain.model.ProductId;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import com.visana.erp.network.application.port.out.NetworkNodeRepository;
import com.visana.erp.network.domain.exception.NodeNotFoundException;
import com.visana.erp.network.domain.model.GenealogyNode;

import java.util.UUID;

public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final NetworkNodeRepository networkNodeRepository;

    public CreateOrderService(OrderRepository orderRepository, NetworkNodeRepository networkNodeRepository) {
        this.orderRepository = orderRepository;
        this.networkNodeRepository = networkNodeRepository;
    }

    @Override
    public UUID execute(CreateOrderCommand command) {
        TenantId tenantId = TenantId.of(command.tenantId());
        AffiliateId affiliateId = AffiliateId.of(command.affiliateId());
        OrderType type = OrderType.valueOf(command.orderType());

        // Validate affiliate existence
        GenealogyNode affiliateNode = networkNodeRepository.findById(affiliateId)
                .orElseThrow(() -> new NodeNotFoundException(command.affiliateId()));

        OrderId orderId = OrderId.of(UUID.randomUUID());
        Order order = new Order(tenantId, orderId, affiliateId, type);

        for (OrderItemDto itemDto : command.items()) {
            ProductId productId = ProductId.of(itemDto.productId());
            Money unitPrice = Money.of(itemDto.unitPrice());
            OrderItem orderItem = new OrderItem(productId, itemDto.quantity(), unitPrice);
            order.addItem(orderItem);
        }

        // Logic here to handle OrderStatus.PENDING automatically in the constructor
        orderRepository.save(order);

        return orderId.value();
    }
}
