package com.visana.erp.qualification.domain.model;

import com.visana.erp.qualification.domain.period.QualificationPeriod;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record QualificationResult(
        UUID id,
        UUID memberId,
        UUID volumeResultId,
        QualificationPeriod period,
        UUID businessRuleVersionId,
        int qualifiedLevel,
        boolean qualified,
        boolean activationEligible,
        List<QualificationLevelAssessment> levelAssessments,
        List<String> evidenceReferences,
        Instant calculatedAt,
        String explanation) {

    public QualificationResult {
        Objects.requireNonNull(id, "Qualification result id cannot be null");
        Objects.requireNonNull(memberId, "Member id cannot be null");
        Objects.requireNonNull(volumeResultId, "Volume result id cannot be null");
        Objects.requireNonNull(period, "Period cannot be null");
        Objects.requireNonNull(businessRuleVersionId, "Rule version id cannot be null");
        if (qualifiedLevel < 0) {
            throw new IllegalArgumentException("Qualified level cannot be negative");
        }
        if (qualified != (qualifiedLevel > 0)) {
            throw new IllegalArgumentException("Qualified flag must match the qualified level");
        }
        levelAssessments = List.copyOf(levelAssessments);
        evidenceReferences = List.copyOf(evidenceReferences);
        Objects.requireNonNull(calculatedAt, "Calculation timestamp cannot be null");
        if (explanation == null || explanation.isBlank()) {
            throw new IllegalArgumentException("Business explanation cannot be blank");
        }
    }
}
