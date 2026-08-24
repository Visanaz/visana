package com.visana.erp.core.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount, boolean isReversal) {

    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        if (!isReversal && amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative. For negative values, explicitly create a reversal.");
        }
        if (isReversal && amount.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("A reversal must have a negative or zero amount.");
        }
        amount = amount.setScale(4, RoundingMode.HALF_UP); // High precision for intermediate calculations
    }
    
    public Money(BigDecimal amount) {
        this(amount, amount != null && amount.compareTo(BigDecimal.ZERO) < 0);
        if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative. For negative values, explicitly create a reversal.");
        }
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public static Money of(String amount) {
        return new Money(new BigDecimal(amount));
    }
    
    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }
    
    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public static Money reversal(BigDecimal amount) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        BigDecimal reversalAmount = amount;
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            reversalAmount = amount.negate();
        }
        return new Money(reversalAmount, true);
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "Cannot add null Money");
        BigDecimal result = this.amount.add(other.amount);
        boolean resultIsReversal = result.compareTo(BigDecimal.ZERO) < 0;
        return new Money(result, resultIsReversal);
    }

    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Cannot subtract null Money");
        BigDecimal result = this.amount.subtract(other.amount);
        boolean resultIsReversal = result.compareTo(BigDecimal.ZERO) < 0;
        return new Money(result, resultIsReversal);
    }

    public Money multiply(Percentage percentage) {
        Objects.requireNonNull(percentage, "Percentage cannot be null");
        BigDecimal result = this.amount.multiply(percentage.value());
        boolean resultIsReversal = result.compareTo(BigDecimal.ZERO) < 0;
        return new Money(result, resultIsReversal);
    }

    public Money multiply(int multiplier) {
        BigDecimal result = this.amount.multiply(BigDecimal.valueOf(multiplier));
        boolean resultIsReversal = result.compareTo(BigDecimal.ZERO) < 0;
        return new Money(result, resultIsReversal);
    }
    
    public Money multiply(BigDecimal multiplier) {
        Objects.requireNonNull(multiplier, "Multiplier cannot be null");
        BigDecimal result = this.amount.multiply(multiplier);
        boolean resultIsReversal = result.compareTo(BigDecimal.ZERO) < 0;
        return new Money(result, resultIsReversal);
    }
}
