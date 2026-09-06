package com.visana.erp.qualification.domain.model;

import com.visana.erp.qualification.domain.rules.BusinessRuleVersion;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record VersionedQualificationRuleSet(
        BusinessRuleVersion ruleVersion,
        List<LevelQualificationRule> levels,
        Set<Integer> affiliationRequiredLevels) {

    public VersionedQualificationRuleSet {
        Objects.requireNonNull(ruleVersion, "Rule version cannot be null");
        levels = levels.stream()
                .sorted(Comparator.comparingInt(LevelQualificationRule::getLevel))
                .toList();
        if (levels.isEmpty()) {
            throw new IllegalArgumentException("At least one qualification level is required");
        }
        Set<Integer> seen = new HashSet<>();
        for (LevelQualificationRule level : levels) {
            if (!seen.add(level.getLevel())) {
                throw new IllegalArgumentException("Qualification level numbers must be unique");
            }
        }
        affiliationRequiredLevels = Set.copyOf(affiliationRequiredLevels);
    }
}
