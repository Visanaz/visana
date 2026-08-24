package com.visana.erp.ledger.domain.model;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.Period;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;

import java.util.Objects;

public class Payout {
    private final PayoutId payoutId;
    private final TenantId tenantId;
    private final AffiliateId affiliateId;
    private final Money totalAmount;
    private final Period period;
    private PayoutStatus status;

    public Payout(PayoutId payoutId, TenantId tenantId, AffiliateId affiliateId, Money totalAmount, Period period) {
        this.payoutId = Objects.requireNonNull(payoutId, "PayoutId cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "TenantId cannot be null");
        this.affiliateId = Objects.requireNonNull(affiliateId, "AffiliateId cannot be null");
        this.totalAmount = Objects.requireNonNull(totalAmount, "Total amount cannot be null");
        this.period = Objects.requireNonNull(period, "Period cannot be null");
        this.status = PayoutStatus.PENDING;
    }

    public void markAsProcessing() {
        if (this.status != PayoutStatus.PENDING) {
            throw new IllegalStateException("Only PENDING payouts can transition to PROCESSING");
        }
        this.status = PayoutStatus.PROCESSING;
    }

    public void markAsPaid() {
        if (this.status != PayoutStatus.PROCESSING) {
            throw new IllegalStateException("Only PROCESSING payouts can transition to PAID");
        }
        this.status = PayoutStatus.PAID;
    }

    public PayoutId getPayoutId() { return payoutId; }
    public TenantId getTenantId() { return tenantId; }
    public AffiliateId getAffiliateId() { return affiliateId; }
    public Money getTotalAmount() { return totalAmount; }
    public Period getPeriod() { return period; }
    public PayoutStatus getStatus() { return status; }
}
