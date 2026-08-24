package com.visana.erp.ledger.domain.model;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;

import java.math.BigDecimal;
import java.util.Objects;

public class LedgerAccount {
    private final TenantId tenantId;
    private final LedgerAccountId ledgerAccountId;
    private final AffiliateId affiliateId;
    private Money currentBalance;

    public LedgerAccount(TenantId tenantId, LedgerAccountId ledgerAccountId, AffiliateId affiliateId, Money initialBalance) {
        this.tenantId = Objects.requireNonNull(tenantId, "TenantId cannot be null");
        this.ledgerAccountId = Objects.requireNonNull(ledgerAccountId, "LedgerAccountId cannot be null");
        this.affiliateId = Objects.requireNonNull(affiliateId, "AffiliateId cannot be null");
        this.currentBalance = Objects.requireNonNull(initialBalance, "Initial balance cannot be null");
    }

    public void registerCredit(Money amount) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        if (amount.isReversal()) {
            throw new IllegalArgumentException("Cannot credit a reversal amount");
        }
        this.currentBalance = this.currentBalance.add(amount);
    }

    public void registerDebit(Money amount) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        if (amount.isReversal()) {
            throw new IllegalArgumentException("Cannot debit a reversal amount");
        }
        
        if (this.currentBalance.amount().compareTo(amount.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Current balance: " + currentBalance.amount() + ", requested debit: " + amount.amount());
        }
        
        this.currentBalance = this.currentBalance.subtract(amount);
    }

    public TenantId getTenantId() { return tenantId; }
    public LedgerAccountId getLedgerAccountId() { return ledgerAccountId; }
    public AffiliateId getAffiliateId() { return affiliateId; }
    public Money getCurrentBalance() { return currentBalance; }
}
