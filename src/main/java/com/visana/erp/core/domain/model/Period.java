package com.visana.erp.core.domain.model;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public record Period(YearMonth value) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public Period {
        Objects.requireNonNull(value, "Period value cannot be null");
    }

    public static Period of(YearMonth yearMonth) {
        return new Period(yearMonth);
    }

    public static Period of(String periodStr) {
        Objects.requireNonNull(periodStr, "Period string cannot be null");
        try {
            return new Period(YearMonth.parse(periodStr, FORMATTER));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid period format. Expected format is yyyy-MM (e.g., 2026-06)", e);
        }
    }

    public String format() {
        return value.format(FORMATTER);
    }
    
    public Period next() {
        return new Period(value.plusMonths(1));
    }
    
    public Period previous() {
        return new Period(value.minusMonths(1));
    }
}
