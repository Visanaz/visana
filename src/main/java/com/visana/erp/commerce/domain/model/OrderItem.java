package com.visana.erp.commerce.domain.model;

import com.visana.erp.core.domain.model.Money;
import java.util.Objects;

public class OrderItem {
    private final ProductId productId;
    private final int quantity;
    private final Money unitPrice;
    private final Money subTotal;

    public OrderItem(ProductId productId, int quantity, Money unitPrice) {
        this.productId = Objects.requireNonNull(productId, "ProductId cannot be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = quantity;
        this.unitPrice = Objects.requireNonNull(unitPrice, "Unit price cannot be null");
        this.subTotal = unitPrice.multiply(quantity);
    }

    public ProductId getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Money getSubTotal() {
        return subTotal;
    }
}
