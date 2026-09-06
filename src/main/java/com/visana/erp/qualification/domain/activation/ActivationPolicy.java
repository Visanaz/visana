package com.visana.erp.qualification.domain.activation;

import com.visana.erp.qualification.domain.rules.BusinessRuleVersion;
import java.time.ZoneId;
import java.util.Objects;

public record ActivationPolicy(
        BusinessRuleVersion ruleVersion,
        ActivationTrigger trigger,
        ActivationDurationPolicy duration,
        ZoneId zoneId,
        ExpirySemantics expirySemantics,
        RepurchaseStartPolicy repurchaseStartPolicy) {

    public ActivationPolicy {
        Objects.requireNonNull(ruleVersion, "Rule version cannot be null");
        Objects.requireNonNull(trigger, "Activation trigger cannot be null");
        Objects.requireNonNull(duration, "Activation duration cannot be null");
        Objects.requireNonNull(zoneId, "Activation timezone cannot be null");
        Objects.requireNonNull(expirySemantics, "Expiry semantics cannot be null");
        Objects.requireNonNull(repurchaseStartPolicy, "Repurchase policy cannot be null");
    }
}
