package com.visana.erp.qualification.domain.activation;

import java.time.ZonedDateTime;

public record CalendarMonthActivationDuration(int months) implements ActivationDurationPolicy {
    public CalendarMonthActivationDuration {
        if (months < 1) {
            throw new IllegalArgumentException("Calendar-month duration must be positive");
        }
    }

    @Override
    public ZonedDateTime expiryFrom(ZonedDateTime activationStart) {
        return activationStart.plusMonths(months);
    }
}
