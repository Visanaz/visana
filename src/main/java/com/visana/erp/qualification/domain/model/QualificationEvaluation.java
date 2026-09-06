package com.visana.erp.qualification.domain.model;

import com.visana.erp.qualification.domain.activation.ActivationWindow;
import com.visana.erp.qualification.domain.period.QualificationPeriod;
import com.visana.erp.qualification.domain.volume.VolumeResult;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record QualificationEvaluation(
        UUID memberId,
        QualificationPeriod period,
        VolumeResult volumeResult,
        int activeDirects,
        int indirects,
        boolean affiliated,
        ActivationWindow activationWindow,
        Instant evaluatedAt,
        List<String> evidenceReferences) {

    public QualificationEvaluation {
        Objects.requireNonNull(memberId, "Member id cannot be null");
        Objects.requireNonNull(period, "Period cannot be null");
        Objects.requireNonNull(volumeResult, "Volume result cannot be null");
        if (activeDirects < 0 || indirects < 0) {
            throw new IllegalArgumentException("Network counts cannot be negative");
        }
        Objects.requireNonNull(activationWindow, "Activation window cannot be null");
        Objects.requireNonNull(evaluatedAt, "Evaluation timestamp cannot be null");
        evidenceReferences = List.copyOf(evidenceReferences);
    }
}
