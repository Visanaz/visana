package com.visana.erp.qualification.domain.activation;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

public record ActivationWindow(
        UUID businessRuleVersionId,
        ActivationTrigger trigger,
        Instant startInclusive,
        Instant endExclusive,
        ZoneId zoneId,
        ExpirySemantics expirySemantics) {

    public ActivationWindow {
        Objects.requireNonNull(businessRuleVersionId, "Rule version id cannot be null");
        Objects.requireNonNull(trigger, "Trigger cannot be null");
        Objects.requireNonNull(startInclusive, "Activation start cannot be null");
        Objects.requireNonNull(endExclusive, "Activation end cannot be null");
        Objects.requireNonNull(zoneId, "Timezone cannot be null");
        Objects.requireNonNull(expirySemantics, "Expiry semantics cannot be null");
        if (!endExclusive.isAfter(startInclusive)) {
            throw new IllegalArgumentException("Activation end must be after its start");
        }
    }

    public boolean isActiveAt(Instant instant) {
        Objects.requireNonNull(instant, "Evaluation timestamp cannot be null");
        return !instant.isBefore(startInclusive) && instant.isBefore(endExclusive);
    }
}
