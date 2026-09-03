package com.visana.erp.platform.domain.identity;

import java.util.Objects;
import java.util.UUID;

public record PlatformActorId(UUID value) {
    public PlatformActorId {
        Objects.requireNonNull(value, "platform actor id cannot be null");
    }

    public static PlatformActorId of(UUID value) {
        return new PlatformActorId(value);
    }
}
