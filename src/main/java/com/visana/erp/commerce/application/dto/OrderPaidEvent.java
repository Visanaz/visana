package com.visana.erp.commerce.application.dto;

import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.core.domain.event.DomainEvent;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.network.domain.model.AffiliateId;

import com.visana.erp.core.domain.model.TenantId;

import java.util.Objects;

public record OrderPaidEvent(TenantId tenantId, OrderId orderId, AffiliateId affiliateId, Money total) implements DomainEvent {
    public OrderPaidEvent {
        Objects.requireNonNull(tenantId, "TenantId cannot be null");
        Objects.requireNonNull(orderId, "OrderId cannot be null");
        Objects.requireNonNull(affiliateId, "AffiliateId cannot be null");
        Objects.requireNonNull(total, "Total amount cannot be null");
    }
}
