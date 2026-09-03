package com.visana.erp.platform.domain.identity;

import java.util.Objects;

public record PlatformActor(PlatformActorId id, PlatformActorStatus status) {
    public PlatformActor {
        Objects.requireNonNull(id, "platform actor id cannot be null");
        Objects.requireNonNull(status, "platform actor status cannot be null");
    }

    public boolean isActive() {
        return status == PlatformActorStatus.ACTIVE;
    }
}
