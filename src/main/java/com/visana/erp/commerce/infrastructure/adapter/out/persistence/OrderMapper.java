package com.visana.erp.commerce.infrastructure.adapter.out.persistence;

import com.visana.erp.commerce.domain.model.Order;
import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.commerce.domain.model.OrderItem;
import com.visana.erp.commerce.domain.model.OrderStatus;
import com.visana.erp.commerce.domain.model.ProductId;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderJpaEntity toJpaEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(order.getOrderId().value());
        entity.setTenantId(order.getTenantId().value());
        entity.setAffiliateId(order.getAffiliateId().value());
        entity.setOrderType(order.getType());
        entity.setStatus(order.getStatus());

        entity.setItems(order.getItems().stream().map(item -> {
            OrderItemJpaEntity itemEntity = new OrderItemJpaEntity();
            itemEntity.setId(UUID.randomUUID()); // Items act as Value Objects in Domain, but need IDs in DB
            itemEntity.setOrder(entity);
            itemEntity.setProductId(item.getProductId().value());
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setUnitPrice(item.getUnitPrice().amount());
            return itemEntity;
        }).collect(Collectors.toList()));

        return entity;
    }

    public Order toDomainEntity(OrderJpaEntity entity) {
        Order order = new Order(
                TenantId.of(entity.getTenantId()),
                OrderId.of(entity.getId()),
                AffiliateId.of(entity.getAffiliateId()),
                entity.getOrderType()
        );

        if (entity.getStatus() == OrderStatus.PAID) {
            // Need to bypass normal logic or add all items then confirm
            // We'll add items first
            entity.getItems().forEach(item -> 
                order.addItem(new OrderItem(
                    ProductId.of(item.getProductId()), 
                    item.getQuantity(), 
                    Money.of(item.getUnitPrice())
                ))
            );
            order.confirmPayment();
        } else if (entity.getStatus() == OrderStatus.CANCELLED) {
            entity.getItems().forEach(item -> 
                order.addItem(new OrderItem(
                    ProductId.of(item.getProductId()), 
                    item.getQuantity(), 
                    Money.of(item.getUnitPrice())
                ))
            );
            order.cancel();
        } else {
            entity.getItems().forEach(item -> 
                order.addItem(new OrderItem(
                    ProductId.of(item.getProductId()), 
                    item.getQuantity(), 
                    Money.of(item.getUnitPrice())
                ))
            );
        }

        return order;
    }
}
