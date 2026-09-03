package com.visana.erp.commerce.order.domain;

import com.visana.erp.core.domain.model.Money;
import java.util.Objects;
import java.util.UUID;

public record OwnedOrderLine(UUID id, UUID productId, String productCodeSnapshot, String productNameSnapshot,
                             Money unitPriceSnapshot, int quantity, Money lineTotal) {
    public OwnedOrderLine {
        Objects.requireNonNull(id, "line id cannot be null"); Objects.requireNonNull(productId, "product id cannot be null");
        if (productCodeSnapshot == null || productCodeSnapshot.isBlank()) throw new IllegalArgumentException("product code snapshot cannot be blank");
        if (productNameSnapshot == null || productNameSnapshot.isBlank()) throw new IllegalArgumentException("product name snapshot cannot be blank");
        Objects.requireNonNull(unitPriceSnapshot, "unit price snapshot cannot be null");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        Objects.requireNonNull(lineTotal, "line total cannot be null");
    }
}
