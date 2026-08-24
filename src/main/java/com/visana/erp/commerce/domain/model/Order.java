package com.visana.erp.commerce.domain.model;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {
    private final TenantId tenantId;
    private final OrderId orderId;
    private final AffiliateId affiliateId;
    private final OrderType type;
    private OrderStatus status;
    private final List<OrderItem> items;

    public Order(TenantId tenantId, OrderId orderId, AffiliateId affiliateId, OrderType type) {
        this.tenantId = Objects.requireNonNull(tenantId, "TenantId cannot be null");
        this.orderId = Objects.requireNonNull(orderId, "OrderId cannot be null");
        this.affiliateId = Objects.requireNonNull(affiliateId, "AffiliateId cannot be null");
        this.type = Objects.requireNonNull(type, "OrderType cannot be null");
        this.status = OrderStatus.PENDING;
        this.items = new ArrayList<>();
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public AffiliateId getAffiliateId() {
        return affiliateId;
    }

    public OrderType getType() {
        return type;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
    
    public void addItem(OrderItem item) {
        Objects.requireNonNull(item, "OrderItem cannot be null");
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot add items to an order that is not pending");
        }
        this.items.add(item);
    }

    public Money calculateTotal() {
        Money total = Money.zero();
        for (OrderItem item : items) {
            total = total.add(item.getSubTotal());
        }
        return total;
    }

    public void confirmPayment() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot confirm payment for a cancelled order");
        }
        if (this.status == OrderStatus.PAID) {
            throw new IllegalStateException("Order is already paid");
        }
        if (this.items.isEmpty()) {
            throw new IllegalStateException("Cannot confirm payment for an empty order");
        }
        
        this.status = OrderStatus.PAID;
        
        // El pago exitoso generará un dominio evento (ej. OrderPaidEvent).
        // Ese evento es escuchado por el motor de compensación y la genealogía para evaluar calificaciones
        // y calcular comisiones correspondientes.
    }
    
    public void cancel() {
        if (this.status == OrderStatus.PAID) {
            throw new IllegalStateException("Cannot cancel an order that is already paid");
        }
        this.status = OrderStatus.CANCELLED;
    }
}
