package com.visana.erp.core.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PercentageTest {

    @Test
    void shouldCreatePercentage() {
        Percentage percentage = Percentage.of(new BigDecimal("0.15"));
        assertEquals(new BigDecimal("0.15"), percentage.value());
    }

    @Test
    void shouldCreatePercentageFromDouble() {
        Percentage percentage = Percentage.of(0.20);
        assertEquals(0, new BigDecimal("0.2").compareTo(percentage.value()));
    }
    
    @Test
    void shouldCreatePercentageFromString() {
        Percentage percentage = Percentage.of("0.50");
        assertEquals(new BigDecimal("0.50"), percentage.value());
    }

    @Test
    void shouldThrowExceptionWhenNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Percentage.of(new BigDecimal("-0.10"));
        });
        assertTrue(exception.getMessage().contains("Percentage cannot be negative"));
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        assertThrows(NullPointerException.class, () -> new Percentage(null));
    }
}
