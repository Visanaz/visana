package com.visana.erp.core.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithPositiveAmount() {
        Money money = Money.of(new BigDecimal("100.50"));
        assertEquals(new BigDecimal("100.5000").setScale(4, RoundingMode.HALF_UP), money.amount());
        assertFalse(money.isReversal());
    }

    @Test
    void shouldCreateMoneyFromDouble() {
        Money money = Money.of(50.25);
        assertEquals(new BigDecimal("50.2500").setScale(4, RoundingMode.HALF_UP), money.amount());
        assertFalse(money.isReversal());
    }

    @Test
    void shouldCreateZeroMoney() {
        Money zero = Money.zero();
        assertEquals(new BigDecimal("0.0000").setScale(4, RoundingMode.HALF_UP), zero.amount());
        assertFalse(zero.isReversal());
    }

    @Test
    void shouldThrowExceptionWhenCreatingNormalMoneyWithNegativeAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Money.of(new BigDecimal("-100.00"));
        });
        assertTrue(exception.getMessage().contains("Money amount cannot be negative"));
    }

    @Test
    void shouldCreateReversalMoney() {
        Money reversal = Money.reversal(new BigDecimal("50.00"));
        assertEquals(new BigDecimal("-50.0000").setScale(4, RoundingMode.HALF_UP), reversal.amount());
        assertTrue(reversal.isReversal());
    }
    
    @Test
    void shouldCreateReversalMoneyWhenInputIsAlreadyNegative() {
        Money reversal = Money.reversal(new BigDecimal("-50.00"));
        assertEquals(new BigDecimal("-50.0000").setScale(4, RoundingMode.HALF_UP), reversal.amount());
        assertTrue(reversal.isReversal());
    }

    @Test
    void shouldAddMoneys() {
        Money m1 = Money.of(new BigDecimal("100.00"));
        Money m2 = Money.of(new BigDecimal("50.00"));
        Money result = m1.add(m2);
        assertEquals(new BigDecimal("150.0000").setScale(4, RoundingMode.HALF_UP), result.amount());
        assertFalse(result.isReversal());
    }

    @Test
    void shouldAddReversalMoney() {
        Money m1 = Money.of(new BigDecimal("100.00"));
        Money m2 = Money.reversal(new BigDecimal("50.00"));
        Money result = m1.add(m2);
        assertEquals(new BigDecimal("50.0000").setScale(4, RoundingMode.HALF_UP), result.amount());
        assertFalse(result.isReversal());
    }

    @Test
    void shouldSubtractMoneys() {
        Money m1 = Money.of(new BigDecimal("100.00"));
        Money m2 = Money.of(new BigDecimal("50.00"));
        Money result = m1.subtract(m2);
        assertEquals(new BigDecimal("50.0000").setScale(4, RoundingMode.HALF_UP), result.amount());
        assertFalse(result.isReversal());
    }
    
    @Test
    void shouldResultInReversalWhenSubtractingLargerAmount() {
        Money m1 = Money.of(new BigDecimal("50.00"));
        Money m2 = Money.of(new BigDecimal("100.00"));
        Money result = m1.subtract(m2);
        assertEquals(new BigDecimal("-50.0000").setScale(4, RoundingMode.HALF_UP), result.amount());
        assertTrue(result.isReversal());
    }

    @Test
    void shouldMultiplyByPercentage() {
        Money money = Money.of(new BigDecimal("200.00"));
        Percentage percentage = Percentage.of(new BigDecimal("0.15")); // 15%
        Money result = money.multiply(percentage);
        assertEquals(new BigDecimal("30.0000").setScale(4, RoundingMode.HALF_UP), result.amount());
        assertFalse(result.isReversal());
    }

    @Test
    void shouldMultiplyByInteger() {
        Money money = Money.of(new BigDecimal("50.00"));
        Money result = money.multiply(3);
        assertEquals(new BigDecimal("150.0000").setScale(4, RoundingMode.HALF_UP), result.amount());
        assertFalse(result.isReversal());
    }
    
    @Test
    void shouldThrowExceptionWhenNullAmount() {
        assertThrows(NullPointerException.class, () -> new Money(null));
    }
}
