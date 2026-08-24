package com.visana.erp.qualification.domain.service;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.qualification.domain.model.AffiliateMetrics;
import com.visana.erp.qualification.domain.model.LevelQualificationRule;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class QualificationEvaluatorService {

    /**
     * Evaluates if the affiliate is activated based on their purchases and a dynamic configuration.
     * Note: Resolves AUD-001 conflict by not hardcoding activation days (e.g., 29 vs 30 days) nor the amount.
     *
     * @param totalPurchases Affiliate's total valid purchases in the evaluated period.
     * @param lastPurchaseDate The date of the last valid purchase.
     * @param evaluationDate The current date of evaluation.
     * @param requiredActivationAmount The dynamic required amount for activation.
     * @param activationDurationDays The dynamic required days the activation lasts.
     * @return true if activated, false otherwise
     */
    public boolean evaluateActivation(Money totalPurchases, LocalDate lastPurchaseDate, LocalDate evaluationDate,
                                      Money requiredActivationAmount, int activationDurationDays) {
        Objects.requireNonNull(totalPurchases, "Total purchases cannot be null");
        Objects.requireNonNull(requiredActivationAmount, "Required activation amount cannot be null");

        if (lastPurchaseDate == null || evaluationDate == null) {
            return false;
        }

        long daysSinceLastPurchase = ChronoUnit.DAYS.between(lastPurchaseDate, evaluationDate);
        if (daysSinceLastPurchase < 0 || daysSinceLastPurchase > activationDurationDays) {
            return false;
        }

        return totalPurchases.amount().compareTo(requiredActivationAmount.amount()) >= 0;
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
