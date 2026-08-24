package com.visana.erp.ledger.domain.model;

import com.visana.erp.compensation.domain.model.CommissionId;
import com.visana.erp.core.domain.model.Money;

import java.util.Objects;
import java.util.Optional;

public final class LedgerTransaction {
    private final TransactionId transactionId;
    private final LedgerAccountId ledgerAccountId;
    private final Money amount;
    private final TransactionType type;
    private final CommissionId commissionId; // Optional origin reference

    public LedgerTransaction(TransactionId transactionId, LedgerAccountId ledgerAccountId, Money amount, TransactionType type, CommissionId commissionId) {
        this.transactionId = Objects.requireNonNull(transactionId, "TransactionId cannot be null");
        this.ledgerAccountId = Objects.requireNonNull(ledgerAccountId, "LedgerAccountId cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.type = Objects.requireNonNull(type, "TransactionType cannot be null");
        this.commissionId = commissionId; // Can be null if it's not from a commission (e.g. payout)
    }

    public TransactionId getTransactionId() { return transactionId; }
    public LedgerAccountId getLedgerAccountId() { return ledgerAccountId; }
    public Money getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public Optional<CommissionId> getCommissionId() { return Optional.ofNullable(commissionId); }
}
