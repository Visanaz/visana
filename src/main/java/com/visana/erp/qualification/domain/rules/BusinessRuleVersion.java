package com.visana.erp.qualification.domain.rules;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record BusinessRuleVersion(
        UUID id,
        String ruleSetId,
        int version,
        BusinessRuleStatus status,
        Instant effectiveFrom,
        Instant effectiveTo,
        String source,
        Instant approvedAt,
        String approvedBy,
        ProvisionalConfidence provisionalConfidence,
        String notes) {

    public BusinessRuleVersion {
        Objects.requireNonNull(id, "Rule version id cannot be null");
        if (ruleSetId == null || ruleSetId.isBlank()) {
            throw new IllegalArgumentException("Rule set id cannot be blank");
        }
        if (version < 1) {
            throw new IllegalArgumentException("Rule version must be positive");
        }
        Objects.requireNonNull(status, "Rule status cannot be null");
        Objects.requireNonNull(effectiveFrom, "Effective-from timestamp cannot be null");
        if (effectiveTo != null && !effectiveTo.isAfter(effectiveFrom)) {
            throw new IllegalArgumentException("Effective-to timestamp must be after effective-from");
        }
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Rule source cannot be blank");
        }
        if (status == BusinessRuleStatus.PROVISIONAL && provisionalConfidence == null) {
            throw new IllegalArgumentException("Provisional rules require an explicit confidence");
        }
        if (status == BusinessRuleStatus.APPROVED
                && (approvedAt == null || approvedBy == null || approvedBy.isBlank())) {
            throw new IllegalArgumentException("Approved rules require approval timestamp and authority");
        }
        notes = notes == null ? "" : notes;
    }

    public boolean isEffectiveAt(Instant instant) {
        Objects.requireNonNull(instant, "Evaluation timestamp cannot be null");
        return !instant.isBefore(effectiveFrom) && (effectiveTo == null || instant.isBefore(effectiveTo));
    }
}
