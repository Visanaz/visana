package com.visana.erp.qualification.domain.model;

import com.visana.erp.core.domain.model.Money;
import java.util.Objects;

public record QualificationLevelAssessment(
        int level,
        boolean satisfied,
        boolean activationEligible,
        boolean affiliationRequired,
        boolean affiliated,
        int requiredDirects,
        int actualDirects,
        int requiredIndirects,
        int actualIndirects,
        Money requiredTeamVolume,
        Money actualTeamVolume,
        String explanation) {

    public QualificationLevelAssessment {
        if (level < 1) {
            throw new IllegalArgumentException("Level must be positive");
        }
        Objects.requireNonNull(requiredTeamVolume, "Required team volume cannot be null");
        Objects.requireNonNull(actualTeamVolume, "Actual team volume cannot be null");
        if (explanation == null || explanation.isBlank()) {
            throw new IllegalArgumentException("Business explanation cannot be blank");
        }
    }
}
