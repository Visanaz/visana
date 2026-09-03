package com.visana.erp.platform.application.authorization;

import java.util.Optional;

/**
 * Infrastructure-neutral foundation: absent or denied relationship evidence never grants access.
 * Resource-specific ownership mappings are intentionally supplied by later approved adapters.
 */
public final class FailClosedAccessPolicy {

    public boolean isAllowed(Optional<Boolean> explicitDecision) {
        return explicitDecision.orElse(false);
    }
}
