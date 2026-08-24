package com.visana.erp.ledger.domain.service;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.Period;
import com.visana.erp.ledger.domain.model.LedgerAccount;
import com.visana.erp.ledger.domain.model.Payout;
import com.visana.erp.ledger.domain.model.PayoutId;
import com.visana.erp.ledger.domain.model.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.Objects;

public class SettlementService {

    public Payout generatePayout(LedgerAccount account, Period period) {
        Objects.requireNonNull(account, "LedgerAccount cannot be null");
        Objects.requireNonNull(period, "Period cannot be null");

        Money currentBalance = account.getCurrentBalance();
        if (currentBalance.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientFundsException("Cannot generate payout for account with zero or negative balance");
        }

        Money amountToDebit = Money.of(currentBalance.amount()); // Copy to avoid reference issues
        
        // Debe vaciar la cuenta (retirar todo el saldo)
        account.registerDebit(amountToDebit);
        
        return new Payout(
                PayoutId.generate(),
                account.getTenantId(),
                account.getAffiliateId(),
                amountToDebit,
                period
        );
    }
}
