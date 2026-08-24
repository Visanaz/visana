package com.visana.erp.ledger.domain.service;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.Period;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.ledger.domain.model.InsufficientFundsException;
import com.visana.erp.ledger.domain.model.LedgerAccount;
import com.visana.erp.ledger.domain.model.LedgerAccountId;
import com.visana.erp.ledger.domain.model.Payout;
import com.visana.erp.ledger.domain.model.PayoutStatus;
import com.visana.erp.network.domain.model.AffiliateId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class SettlementServiceTest {

    private SettlementService settlementService;
    private LedgerAccount account;
    private Period period;

    @BeforeEach
    void setUp() {
        settlementService = new SettlementService();
        account = new LedgerAccount(
                TenantId.generate(),
                LedgerAccountId.generate(),
                AffiliateId.generate(),
                Money.of(new BigDecimal("500000.00"))
        );
        period = Period.of(YearMonth.of(2026, 6));
    }

    @Test
    void shouldGeneratePayoutAndEmptyAccountBalance() {
        Payout payout = settlementService.generatePayout(account, period);

        assertNotNull(payout);
        assertEquals(account.getTenantId(), payout.getTenantId());
        assertEquals(account.getAffiliateId(), payout.getAffiliateId());
        assertEquals(period, payout.getPeriod());
        assertEquals(0, new BigDecimal("500000.0000").compareTo(payout.getTotalAmount().amount()));
        assertEquals(PayoutStatus.PENDING, payout.getStatus());

        // Account balance should be zero after debit
        assertEquals(0, BigDecimal.ZERO.compareTo(account.getCurrentBalance().amount()));
    }

    @Test
    void shouldThrowExceptionWhenGeneratingPayoutForZeroBalance() {
        LedgerAccount emptyAccount = new LedgerAccount(
                TenantId.generate(),
                LedgerAccountId.generate(),
                AffiliateId.generate(),
                Money.zero()
        );

        assertThrows(InsufficientFundsException.class, () -> {
            settlementService.generatePayout(emptyAccount, period);
        });
    }
    
    @Test
    void shouldRegisterCreditCorrectly() {
        account.registerCredit(Money.of(new BigDecimal("100000.00")));
        assertEquals(0, new BigDecimal("600000.0000").compareTo(account.getCurrentBalance().amount()));
    }
    
    @Test
    void shouldRegisterDebitCorrectly() {
        account.registerDebit(Money.of(new BigDecimal("100000.00")));
        assertEquals(0, new BigDecimal("400000.0000").compareTo(account.getCurrentBalance().amount()));
    }
    
    @Test
    void shouldThrowExceptionWhenDebitExceedsBalance() {
        assertThrows(InsufficientFundsException.class, () -> {
            account.registerDebit(Money.of(new BigDecimal("600000.00")));
        });
    }
}
