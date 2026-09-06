package com.visana.erp.qualification.domain.volume;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.qualification.domain.period.QualificationPeriod;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record VolumeResult(
        UUID id,
        UUID memberId,
        QualificationPeriod period,
        UUID businessRuleVersionId,
        Money personalVolume,
        Money teamVolume,
        Money qualificationBase,
        Money adjustmentTotal,
        List<String> evidenceReferences,
        Instant calculatedAt) {

    public VolumeResult {
        Objects.requireNonNull(id, "Volume result id cannot be null");
        Objects.requireNonNull(memberId, "Member id cannot be null");
        Objects.requireNonNull(period, "Period cannot be null");
        Objects.requireNonNull(businessRuleVersionId, "Rule version id cannot be null");
        Objects.requireNonNull(personalVolume, "Personal volume cannot be null");
        Objects.requireNonNull(teamVolume, "Team volume cannot be null");
        Objects.requireNonNull(qualificationBase, "Qualification base cannot be null");
        Objects.requireNonNull(adjustmentTotal, "Adjustment total cannot be null");
        evidenceReferences = List.copyOf(evidenceReferences);
        Objects.requireNonNull(calculatedAt, "Calculation timestamp cannot be null");
    }
}
