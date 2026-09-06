package com.visana.erp.qualification.domain.volume;

import static com.visana.erp.qualification.support.QualificationTestFixtures.components;
import static com.visana.erp.qualification.support.QualificationTestFixtures.period;
import static com.visana.erp.qualification.support.QualificationTestFixtures.provisionalRule;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.qualification.domain.rules.BusinessRuleVersion;
import com.visana.erp.qualification.domain.rules.RuleExecutionGuard;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TeamSalesAggregatorTest {
    private final TeamSalesAggregator aggregator = new TeamSalesAggregator(new RuleExecutionGuard());

    @Test
    void aggregatesOwnAndAllDescendantQualifyingSalesSeparatelyFromEligibilityPolicy() {
        UUID member = UUID.randomUUID();
        UUID child = UUID.randomUUID();
        UUID grandchild = UUID.randomUUID();
        UUID unrelated = UUID.randomUUID();
        BusinessRuleVersion rule = provisionalRule(UUID.randomUUID(), 1);
        Instant inPeriod = Instant.parse("2026-09-10T12:00:00Z");
        List<SaleEvidence> evidence = List.of(
                sale(member, inPeriod, "FIXTURE_ELIGIBLE", "100", Money.zero(), "order-own"),
                sale(child, inPeriod, "FIXTURE_ELIGIBLE", "200", Money.zero(), "order-child"),
                sale(grandchild, inPeriod, "FIXTURE_ELIGIBLE", "100", Money.reversal(new BigDecimal("50")), "order-grandchild-adjusted"),
                sale(grandchild, inPeriod, "FIXTURE_EXCLUDED", "900", Money.zero(), "order-excluded"),
                sale(unrelated, inPeriod, "FIXTURE_ELIGIBLE", "700", Money.zero(), "order-unrelated"),
                sale(child, Instant.parse("2026-10-02T12:00:00Z"), "FIXTURE_ELIGIBLE", "800", Money.zero(), "order-outside-period"));

        VolumeResult result = aggregator.aggregate(
                member,
                period(),
                rule,
                Set.of(child, grandchild),
                evidence,
                sale -> sale.lifecycleState().equals("FIXTURE_ELIGIBLE"),
                sale -> sale.monetaryComponents().netBeforeTax(),
                Instant.parse("2026-10-01T01:00:00Z"));

        assertMoney("100", result.personalVolume());
        assertMoney("350", result.teamVolume());
        assertMoney("350", result.qualificationBase());
        assertMoney("-50", result.adjustmentTotal());
        assertEquals(List.of("order-own", "order-child", "order-grandchild-adjusted"), result.evidenceReferences());
        assertEquals(rule.id(), result.businessRuleVersionId());
    }

    @Test
    void baseFormulaIsPolicyDrivenRatherThanEmbeddedInAggregation() {
        UUID member = UUID.randomUUID();
        SaleMonetaryComponents amounts = new SaleMonetaryComponents(
                Money.of("150"), Money.of("25"), Money.of("125"),
                Money.of("24"), Money.of("10"), Money.of("159"), Money.zero());
        SaleEvidence sale = new SaleEvidence(
                UUID.randomUUID(), member, Instant.parse("2026-09-10T12:00:00Z"),
                "ANY_EXPLICIT_FIXTURE_STATE", amounts, Money.zero(), "order-policy");
        BusinessRuleVersion rule = provisionalRule(UUID.randomUUID(), 1);

        VolumeResult gross = aggregator.aggregate(
                member, period(), rule, Set.of(), List.of(sale), ignored -> true,
                item -> item.monetaryComponents().gross(), Instant.parse("2026-10-01T01:00:00Z"));
        VolumeResult paid = aggregator.aggregate(
                member, period(), rule, Set.of(), List.of(sale), ignored -> true,
                item -> item.monetaryComponents().paidAmount(), Instant.parse("2026-10-01T01:00:00Z"));

        assertMoney("150", gross.qualificationBase());
        assertMoney("159", paid.qualificationBase());
    }

    private SaleEvidence sale(
            UUID seller,
            Instant occurredAt,
            String state,
            String base,
            Money adjustment,
            String reference) {
        return new SaleEvidence(
                UUID.randomUUID(), seller, occurredAt, state, components(base), adjustment, reference);
    }

    private void assertMoney(String expected, Money actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual.amount()));
    }
}
