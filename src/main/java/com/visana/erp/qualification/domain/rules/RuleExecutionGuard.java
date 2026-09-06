package com.visana.erp.qualification.domain.rules;

import java.time.Instant;
import java.util.Objects;

public final class RuleExecutionGuard {

    public void requireApprovedForFinancialEffect(BusinessRuleVersion ruleVersion) {
        Objects.requireNonNull(ruleVersion, "Rule version cannot be null");
        if (ruleVersion.status() != BusinessRuleStatus.APPROVED) {
            throw new IllegalStateException(
                    "Only APPROVED rule versions may drive financial production effects");
        }
    }

    public void requireEvaluable(BusinessRuleVersion ruleVersion) {
        Objects.requireNonNull(ruleVersion, "Rule version cannot be null");
        if (ruleVersion.status() == BusinessRuleStatus.RETIRED) {
            throw new IllegalStateException("RETIRED rule versions cannot drive new evaluations");
        }
    }

    public void requireEvaluable(BusinessRuleVersion ruleVersion, Instant evaluationTimestamp) {
        requireEvaluable(ruleVersion);
        if (!ruleVersion.isEffectiveAt(evaluationTimestamp)) {
            throw new IllegalStateException("Rule version is not effective at the evaluation timestamp");
        }
    }
}
