package com.visana.erp.core.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Percentage(BigDecimal value) {
    public Percentage {
        Objects.requireNonNull(value, "Percentage value cannot be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Percentage cannot be negative");
        }
    }

    public static Percentage of(BigDecimal value) {
        return new Percentage(value);
    }

    public static Percentage of(String value) {
        return new Percentage(new BigDecimal(value));
    }
    
    public static Percentage of(double value) {
        return new Percentage(BigDecimal.valueOf(value));
    }
}
