package com.visana.erp.qualification.domain.volume;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.qualification.domain.period.QualificationPeriod;
import com.visana.erp.qualification.domain.rules.BusinessRuleVersion;
import com.visana.erp.qualification.domain.rules.RuleExecutionGuard;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class TeamSalesAggregator {
    private final RuleExecutionGuard ruleExecutionGuard;

    public TeamSalesAggregator(RuleExecutionGuard ruleExecutionGuard) {
        this.ruleExecutionGuard = Objects.requireNonNull(ruleExecutionGuard);
    }

    public VolumeResult aggregate(
            UUID memberId,
            QualificationPeriod period,
            BusinessRuleVersion ruleVersion,
            Set<UUID> memberAndDescendants,
            Collection<SaleEvidence> sales,
            QualifyingSalePolicy salePolicy,
            QualificationBasePolicy basePolicy,
            Instant calculatedAt) {
        Objects.requireNonNull(memberId, "Member id cannot be null");
        Objects.requireNonNull(period, "Period cannot be null");
        Objects.requireNonNull(ruleVersion, "Rule version cannot be null");
        Objects.requireNonNull(memberAndDescendants, "Genealogy scope cannot be null");
        Objects.requireNonNull(sales, "Sale evidence cannot be null");
        Objects.requireNonNull(salePolicy, "Sale eligibility policy cannot be null");
        Objects.requireNonNull(basePolicy, "Qualification base policy cannot be null");
        Objects.requireNonNull(calculatedAt, "Calculation timestamp cannot be null");
        ruleExecutionGuard.requireEvaluable(ruleVersion, calculatedAt);

        Set<UUID> scope = new LinkedHashSet<>(memberAndDescendants);
        scope.add(memberId);
        Money personal = Money.zero();
        Money team = Money.zero();
        Money adjustments = Money.zero();
        Set<String> evidence = new LinkedHashSet<>();

        for (SaleEvidence sale : sales) {
            if (!period.contains(sale.occurredAt()) || !scope.contains(sale.sellerMemberId())
                    || !salePolicy.qualifies(sale)) {
                continue;
            }
            Money contribution = basePolicy.qualificationBase(sale).add(sale.adjustment());
            team = team.add(contribution);
            adjustments = adjustments.add(sale.adjustment());
            if (memberId.equals(sale.sellerMemberId())) {
                personal = personal.add(contribution);
            }
            evidence.add(sale.sourceReference());
        }

        return new VolumeResult(
                UUID.randomUUID(), memberId, period, ruleVersion.id(), personal, team, team,
                adjustments, evidence.stream().toList(), calculatedAt);
    }
}
