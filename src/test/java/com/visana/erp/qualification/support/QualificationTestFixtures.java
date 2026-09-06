package com.visana.erp.qualification.support;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.qualification.domain.activation.ActivationCalculator;
import com.visana.erp.qualification.domain.activation.ActivationPolicy;
import com.visana.erp.qualification.domain.activation.ActivationTrigger;
import com.visana.erp.qualification.domain.activation.CalendarMonthActivationDuration;
import com.visana.erp.qualification.domain.activation.ExpirySemantics;
import com.visana.erp.qualification.domain.activation.RepurchaseStartPolicy;
import com.visana.erp.qualification.domain.period.QualificationPeriod;
import com.visana.erp.qualification.domain.rules.BusinessRuleStatus;
import com.visana.erp.qualification.domain.rules.BusinessRuleVersion;
import com.visana.erp.qualification.domain.rules.ProvisionalConfidence;
import com.visana.erp.qualification.domain.rules.RuleExecutionGuard;
import com.visana.erp.qualification.domain.volume.SaleMonetaryComponents;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

public final class QualificationTestFixtures {
    public static final ZoneId BUSINESS_ZONE = ZoneId.of("America/Bogota");
    public static final Instant EFFECTIVE_FROM = Instant.parse("2026-09-05T00:00:00Z");

    private QualificationTestFixtures() {
    }

    public static BusinessRuleVersion provisionalRule(UUID id, int version) {
        return new BusinessRuleVersion(
                id,
                "BUSINESS_RULES_WORKING_BASELINE_CATHERINE_2026-09-05_PROVISIONAL",
                version,
                BusinessRuleStatus.PROVISIONAL,
                EFFECTIVE_FROM,
                null,
                "VISANA verbal clarification 2026-09-05",
                null,
                null,
                ProvisionalConfidence.HIGH,
                "Not client-approved");
    }

    public static ActivationPolicy activationPolicy(BusinessRuleVersion version) {
        return new ActivationPolicy(
                version,
                ActivationTrigger.PAYMENT_CONFIRMED,
                new CalendarMonthActivationDuration(1),
                BUSINESS_ZONE,
                ExpirySemantics.EXACT_ANNIVERSARY_TIMESTAMP,
                RepurchaseStartPolicy.PENDING_OFFICIAL_CONFIRMATION);
    }

    public static com.visana.erp.qualification.domain.activation.ActivationWindow activeWindow(
            BusinessRuleVersion version,
            Instant paymentConfirmedAt) {
        return new ActivationCalculator(new RuleExecutionGuard())
                .calculate(paymentConfirmedAt, activationPolicy(version));
    }

    public static QualificationPeriod period() {
        return new QualificationPeriod(
                "SYNTHETIC-PERIOD-A",
                Instant.parse("2026-09-01T00:00:00Z"),
                Instant.parse("2026-10-01T00:00:00Z"));
    }

    public static SaleMonetaryComponents components(String netBeforeTax) {
        Money net = Money.of(netBeforeTax);
        return new SaleMonetaryComponents(
                net, Money.zero(), net, Money.zero(), Money.zero(), net, Money.zero());
    }
}
