package com.visana.erp.qualification.domain.activation;

import com.visana.erp.qualification.domain.rules.RuleExecutionGuard;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

public final class ActivationCalculator {
    private final RuleExecutionGuard ruleExecutionGuard;

    public ActivationCalculator(RuleExecutionGuard ruleExecutionGuard) {
        this.ruleExecutionGuard = Objects.requireNonNull(ruleExecutionGuard);
    }

    public ActivationWindow calculate(Instant paymentConfirmedAt, ActivationPolicy policy) {
        return calculate(paymentConfirmedAt, Optional.empty(), policy);
    }

    public ActivationWindow calculate(
            Instant paymentConfirmedAt,
            Optional<Instant> currentActivationExpiry,
            ActivationPolicy policy) {
        Objects.requireNonNull(paymentConfirmedAt, "Payment confirmation timestamp cannot be null");
        Objects.requireNonNull(currentActivationExpiry, "Current expiry optional cannot be null");
        Objects.requireNonNull(policy, "Activation policy cannot be null");
        ruleExecutionGuard.requireEvaluable(policy.ruleVersion(), paymentConfirmedAt);

        Instant start = resolveStart(paymentConfirmedAt, currentActivationExpiry, policy.repurchaseStartPolicy());
        ZonedDateTime zonedStart = start.atZone(policy.zoneId());
        ZonedDateTime anniversary = policy.duration().expiryFrom(zonedStart);
        Instant end = switch (policy.expirySemantics()) {
            case EXACT_ANNIVERSARY_TIMESTAMP -> anniversary.toInstant();
            case END_OF_ANNIVERSARY_DAY -> anniversary.toLocalDate()
                    .plusDays(1)
                    .atStartOfDay(policy.zoneId())
                    .toInstant();
        };

        return new ActivationWindow(
                policy.ruleVersion().id(), policy.trigger(), start, end, policy.zoneId(), policy.expirySemantics());
    }

    private Instant resolveStart(
            Instant paymentConfirmedAt,
            Optional<Instant> currentActivationExpiry,
            RepurchaseStartPolicy repurchasePolicy) {
        boolean overlap = currentActivationExpiry.filter(expiry -> expiry.isAfter(paymentConfirmedAt)).isPresent();
        return switch (repurchasePolicy) {
            case PAYMENT_CONFIRMATION -> paymentConfirmedAt;
            case CURRENT_ACTIVATION_EXPIRY -> currentActivationExpiry
                    .filter(expiry -> expiry.isAfter(paymentConfirmedAt))
                    .orElse(paymentConfirmedAt);
            case PENDING_OFFICIAL_CONFIRMATION -> {
                if (overlap) {
                    throw new IllegalStateException(
                            "Repurchase overlap requires an officially confirmed start policy");
                }
                yield paymentConfirmedAt;
            }
        };
    }
}
