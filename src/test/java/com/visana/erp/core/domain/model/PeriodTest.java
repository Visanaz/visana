package com.visana.erp.core.domain.model;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class PeriodTest {

    @Test
    void shouldCreatePeriodFromYearMonth() {
        YearMonth ym = YearMonth.of(2026, 6);
        Period period = Period.of(ym);
        assertEquals(ym, period.value());
    }

    @Test
    void shouldCreatePeriodFromString() {
        Period period = Period.of("2026-06");
        assertEquals(YearMonth.of(2026, 6), period.value());
    }

    @Test
    void shouldFormatPeriodToString() {
        Period period = Period.of(YearMonth.of(2026, 6));
        assertEquals("2026-06", period.format());
    }

    @Test
    void shouldGetNextPeriod() {
        Period period = Period.of("2026-06");
        Period next = period.next();
        assertEquals("2026-07", next.format());
    }
    
    @Test
    void shouldGetNextPeriodOverYear() {
        Period period = Period.of("2026-12");
        Period next = period.next();
        assertEquals("2027-01", next.format());
    }

    @Test
    void shouldGetPreviousPeriod() {
        Period period = Period.of("2026-06");
        Period previous = period.previous();
        assertEquals("2026-05", previous.format());
    }

    @Test
    void shouldThrowExceptionOnInvalidFormat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Period.of("2026/06");
        });
        assertTrue(exception.getMessage().contains("Invalid period format"));
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        assertThrows(NullPointerException.class, () -> new Period(null));
    }
}
