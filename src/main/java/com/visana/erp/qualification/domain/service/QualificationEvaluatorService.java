package com.visana.erp.qualification.domain.service;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.qualification.domain.model.AffiliateMetrics;
import com.visana.erp.qualification.domain.model.LevelQualificationRule;
import com.visana.erp.qualification.domain.model.QualificationEvaluation;
import com.visana.erp.qualification.domain.model.QualificationLevelAssessment;
import com.visana.erp.qualification.domain.model.QualificationResult;
import com.visana.erp.qualification.domain.model.VersionedQualificationRuleSet;
import com.visana.erp.qualification.domain.rules.RuleExecutionGuard;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class QualificationEvaluatorService {
    private final RuleExecutionGuard ruleExecutionGuard;

    public QualificationEvaluatorService() {
        this(new RuleExecutionGuard());
    }

    public QualificationEvaluatorService(RuleExecutionGuard ruleExecutionGuard) {
        this.ruleExecutionGuard = Objects.requireNonNull(ruleExecutionGuard);
    }

    public QualificationResult evaluate(
            QualificationEvaluation evaluation,
            VersionedQualificationRuleSet rules,
            Instant calculatedAt) {
        Objects.requireNonNull(evaluation, "Qualification evaluation cannot be null");
        Objects.requireNonNull(rules, "Qualification rules cannot be null");
        Objects.requireNonNull(calculatedAt, "Calculation timestamp cannot be null");
        ruleExecutionGuard.requireEvaluable(rules.ruleVersion(), evaluation.evaluatedAt());

        UUID versionId = rules.ruleVersion().id();
        if (!versionId.equals(evaluation.volumeResult().businessRuleVersionId())
                || !versionId.equals(evaluation.activationWindow().businessRuleVersionId())) {
            throw new IllegalArgumentException(
                    "Activation, volume and qualification must reference the same rule version");
        }
        if (!evaluation.memberId().equals(evaluation.volumeResult().memberId())
                || !evaluation.period().equals(evaluation.volumeResult().period())) {
            throw new IllegalArgumentException("Volume result must belong to the evaluated member and period");
        }

        boolean activationEligible = evaluation.activationWindow().isActiveAt(evaluation.evaluatedAt());
        int qualifiedLevel = 0;
        List<QualificationLevelAssessment> assessments = new ArrayList<>();
        for (LevelQualificationRule rule : rules.levels()) {
            boolean affiliationRequired = rules.affiliationRequiredLevels().contains(rule.getLevel());
            boolean affiliationSatisfied = !affiliationRequired || evaluation.affiliated();
            boolean metricsSatisfied = rule.isSatisfiedBy(
                    evaluation.activeDirects(),
                    evaluation.indirects(),
                    evaluation.volumeResult().teamVolume());
            boolean satisfied = activationEligible && affiliationSatisfied && metricsSatisfied;
            if (satisfied) {
                qualifiedLevel = Math.max(qualifiedLevel, rule.getLevel());
            }
            assessments.add(new QualificationLevelAssessment(
                    rule.getLevel(),
                    satisfied,
                    activationEligible,
                    affiliationRequired,
                    evaluation.affiliated(),
                    rule.getNetworkRequirement().minDirects(),
                    evaluation.activeDirects(),
                    rule.getNetworkRequirement().minIndirects(),
                    evaluation.indirects(),
                    rule.getVolumeRequirement().minTeamSales(),
                    evaluation.volumeResult().teamVolume(),
                    explanation(rule, evaluation, activationEligible, affiliationRequired)));
        }

        String summary = "Qualified level = L" + qualifiedLevel
                + "; activationEligible = " + activationEligible
                + "; ruleVersion = " + rules.ruleVersion().ruleSetId() + ":" + rules.ruleVersion().version();
        return new QualificationResult(
                UUID.randomUUID(), evaluation.memberId(), evaluation.volumeResult().id(), evaluation.period(), versionId,
                qualifiedLevel, qualifiedLevel > 0, activationEligible, assessments,
                evaluation.evidenceReferences(), calculatedAt, summary);
    }

    private String explanation(
            LevelQualificationRule rule,
            QualificationEvaluation evaluation,
            boolean activationEligible,
            boolean affiliationRequired) {
        return "L" + rule.getLevel()
                + ": active directs = " + evaluation.activeDirects()
                + ", required = " + rule.getNetworkRequirement().minDirects()
                + "; indirects = " + evaluation.indirects()
                + ", required = " + rule.getNetworkRequirement().minIndirects()
                + "; Team Sales = " + evaluation.volumeResult().teamVolume().amount()
                + ", required = " + rule.getVolumeRequirement().minTeamSales().amount()
                + "; activationEligible = " + activationEligible
                + "; affiliationRequired = " + affiliationRequired
                + "; affiliated = " + evaluation.affiliated();
    }

    /**
     * Evaluates the maximum qualification level an affiliate has achieved.
     *
     * @param metrics The current metrics of the affiliate.
     * @param rules A list of qualification rules sorted or unsorted.
     * @return the maximum level satisfied. 0 if none is satisfied.
     */
    public int evaluateMaxLevel(AffiliateMetrics metrics, List<LevelQualificationRule> rules) {
        Objects.requireNonNull(metrics, "Affiliate metrics cannot be null");
        if (rules == null || rules.isEmpty()) {
            return 0;
        }

        Optional<LevelQualificationRule> maxRule = rules.stream()
                .filter(rule -> rule.isSatisfiedBy(metrics.directs(), metrics.indirects(), metrics.teamSales()))
                .max(Comparator.comparingInt(LevelQualificationRule::getLevel));

        return maxRule.map(LevelQualificationRule::getLevel).orElse(0);
    }
}
