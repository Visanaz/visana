package com.visana.erp.commerce.order.domain;

import com.visana.erp.commerce.domain.model.OrderStatus;
import com.visana.erp.core.domain.model.Money;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Isolated Plan 3 order aggregate; legacy orders retain affiliate-centred semantics in V1 tables. */
public record OwnedOrder(UUID id, UUID ownerActorId, OrderStatus status, Money total, List<OwnedOrderLine> lines,
                         Instant createdAt, Instant updatedAt) {
    public OwnedOrder {
        Objects.requireNonNull(id, "order id cannot be null"); Objects.requireNonNull(ownerActorId, "owner actor id cannot be null");
        Objects.requireNonNull(status, "order status cannot be null"); Objects.requireNonNull(total, "order total cannot be null");
        lines = List.copyOf(lines); if (lines.isEmpty()) throw new IllegalArgumentException("order must have at least one line");
        Objects.requireNonNull(createdAt, "createdAt cannot be null"); Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
    }
}
