package com.visana.erp.commerce.domain.model;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.TenantId;
import java.util.Objects;

public class Product {
    private final TenantId tenantId;
    private final ProductId productId;
    private final String name;
    private final Money price;
    private final boolean isCommissionable;

    public Product(TenantId tenantId, ProductId productId, String name, Money price, boolean isCommissionable) {
        this.tenantId = Objects.requireNonNull(tenantId, "TenantId cannot be null");
        this.productId = Objects.requireNonNull(productId, "ProductId cannot be null");
        
        Objects.requireNonNull(name, "Product name cannot be null");
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        this.name = name;
        
        this.price = Objects.requireNonNull(price, "Product price cannot be null");
        this.isCommissionable = isCommissionable;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public ProductId getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public boolean isCommissionable() {
        return isCommissionable;
    }
}
