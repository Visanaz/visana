package com.visana.erp.platform.application.identity;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class IdentityResolutionMetrics {
    private final Counter resolutionFailure;
    private final Counter unlinkedIdentity;

    public IdentityResolutionMetrics(MeterRegistry meterRegistry) {
        this.resolutionFailure = Counter.builder("identity_resolution_failure").register(meterRegistry);
        this.unlinkedIdentity = Counter.builder("unlinked_identity").register(meterRegistry);
    }

    public void recordUnlinked() {
        resolutionFailure.increment();
        unlinkedIdentity.increment();
    }

    public void recordDisabled() {
        resolutionFailure.increment();
    }
}
