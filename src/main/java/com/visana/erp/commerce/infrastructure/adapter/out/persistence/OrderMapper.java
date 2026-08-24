package com.visana.erp.commerce.infrastructure.adapter.out.persistence;

import com.visana.erp.commerce.domain.model.Order;
import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.commerce.domain.model.OrderItem;
import com.visana.erp.commerce.domain.model.OrderStatus;
import com.visana.erp.commerce.domain.model.OrderType;
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
        entity.setId(order.getOrderId().value().toString());
        entity.setEmpresaId(order.getTenantId().value().toString());
        entity.setAffiliateId(order.getAffiliateId().value().toString());
        entity.setOrderType(order.getType().name());
        entity.setStatus(order.getStatus().name());

        entity.setItems(order.getItems().stream().map(item -> {
            OrderItemJpaEntity itemEntity = new OrderItemJpaEntity();
            itemEntity.setId(UUID.randomUUID().toString()); // Items act as Value Objects in Domain, but need IDs in DB
            itemEntity.setOrder(entity);
            itemEntity.setProductId(item.getProductId().value().toString());
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setUnitPrice(item.getUnitPrice().amount());
            return itemEntity;
        }).collect(Collectors.toList()));

        return entity;
    }

    public Order toDomainEntity(OrderJpaEntity entity) {
        Order order = new Order(
                TenantId.of(entity.getEmpresaId()),
                OrderId.of(entity.getId()),
                AffiliateId.of(entity.getAffiliateId()),
                OrderType.valueOf(entity.getOrderType())
        );

        if (OrderStatus.valueOf(entity.getStatus()) == OrderStatus.PAID) {
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
        } else if (OrderStatus.valueOf(entity.getStatus()) == OrderStatus.CANCELLED) {
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
