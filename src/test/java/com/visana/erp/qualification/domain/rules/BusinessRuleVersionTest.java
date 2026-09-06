package com.visana.erp.qualification.domain.rules;

import static com.visana.erp.qualification.support.QualificationTestFixtures.provisionalRule;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BusinessRuleVersionTest {
    private final RuleExecutionGuard guard = new RuleExecutionGuard();

    @Test
    void provisionalRulesMayBeEvaluatedButCannotDriveFinancialEffects() {
        BusinessRuleVersion provisional = provisionalRule(UUID.randomUUID(), 1);

        assertDoesNotThrow(() -> guard.requireEvaluable(provisional));
        assertThrows(IllegalStateException.class,
                () -> guard.requireApprovedForFinancialEffect(provisional));
    }

    @Test
    void approvedRulesMayPassTheFinancialEffectGuard() {
        BusinessRuleVersion approved = new BusinessRuleVersion(
                UUID.randomUUID(), "SYNTHETIC-APPROVED", 1, BusinessRuleStatus.APPROVED,
                Instant.parse("2026-01-01T00:00:00Z"), null, "synthetic test source",
                Instant.parse("2026-01-02T00:00:00Z"), "test-authority", null, "fixture");

        assertDoesNotThrow(() -> guard.requireApprovedForFinancialEffect(approved));
    }

    @Test
    void effectiveDatingUsesStartInclusiveAndEndExclusive() {
        BusinessRuleVersion version = new BusinessRuleVersion(
                UUID.randomUUID(), "SYNTHETIC", 1, BusinessRuleStatus.DRAFT,
                Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-02-01T00:00:00Z"),
                "synthetic test source", null, null, null, "fixture");

        assertEquals(true, version.isEffectiveAt(Instant.parse("2026-01-01T00:00:00Z")));
        assertEquals(false, version.isEffectiveAt(Instant.parse("2026-02-01T00:00:00Z")));
    }
}
