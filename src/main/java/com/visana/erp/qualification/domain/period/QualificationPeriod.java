package com.visana.erp.qualification.domain.period;

import java.time.Instant;
import java.util.Objects;

public record QualificationPeriod(String key, Instant startInclusive, Instant endExclusive) {
    public QualificationPeriod {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Qualification period key cannot be blank");
        }
        Objects.requireNonNull(startInclusive, "Period start cannot be null");
        Objects.requireNonNull(endExclusive, "Period end cannot be null");
        if (!endExclusive.isAfter(startInclusive)) {
            throw new IllegalArgumentException("Period end must be after its start");
        }
    }

    public boolean contains(Instant instant) {
        return !instant.isBefore(startInclusive) && instant.isBefore(endExclusive);
    }
}
