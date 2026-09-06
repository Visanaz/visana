package com.visana.erp.qualification.domain.period;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class QualificationPeriodTest {
    @Test
    void usesExplicitBoundariesWithoutAssumingACalendarPolicy() {
        Instant evaluation = Instant.parse("2026-09-20T00:00:00Z");
        QualificationPeriodPolicy fixturePolicyA = ignored -> new QualificationPeriod(
                "FIXTURE-A", Instant.parse("2026-09-01T00:00:00Z"), Instant.parse("2026-10-01T00:00:00Z"));
        QualificationPeriodPolicy fixturePolicyB = ignored -> new QualificationPeriod(
                "FIXTURE-B", Instant.parse("2026-09-15T00:00:00Z"), Instant.parse("2026-09-30T00:00:00Z"));

        assertEquals("FIXTURE-A", fixturePolicyA.resolve(evaluation).key());
        assertEquals("FIXTURE-B", fixturePolicyB.resolve(evaluation).key());
        assertTrue(fixturePolicyA.resolve(evaluation).contains(evaluation));
        assertFalse(fixturePolicyA.resolve(evaluation).contains(Instant.parse("2026-10-01T00:00:00Z")));
    }
}
