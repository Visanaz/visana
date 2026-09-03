package com.visana.erp.platform.domain.ownership;

import com.visana.erp.platform.domain.identity.PlatformActorId;

import java.util.Objects;
import java.util.UUID;

/** Explicit ownership for Plan 3 resources; it does not infer legacy business-profile relations. */
public record ResourceOwnership(String resourceType, UUID resourceId, PlatformActorId ownerActorId) {
    public ResourceOwnership {
        Objects.requireNonNull(resourceType, "resource type cannot be null");
        if (resourceType.isBlank()) {
            throw new IllegalArgumentException("resource type cannot be blank");
        }
        Objects.requireNonNull(resourceId, "resource id cannot be null");
        Objects.requireNonNull(ownerActorId, "owner actor id cannot be null");
    }
}
