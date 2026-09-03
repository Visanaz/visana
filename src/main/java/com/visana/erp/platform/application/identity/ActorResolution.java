package com.visana.erp.platform.application.identity;

import com.visana.erp.platform.domain.identity.PlatformActorId;

import java.util.Objects;

public record ActorResolution(ActorResolutionStatus status, PlatformActorId actorId) {
    public ActorResolution {
        Objects.requireNonNull(status, "actor resolution status cannot be null");
        if (status == ActorResolutionStatus.LINKED && actorId == null) {
            throw new IllegalArgumentException("linked actor resolution requires an actor id");
        }
    }

    public static ActorResolution linked(PlatformActorId actorId) {
        return new ActorResolution(ActorResolutionStatus.LINKED, actorId);
    }

    public static ActorResolution unlinked() {
        return new ActorResolution(ActorResolutionStatus.UNLINKED_IDENTITY, null);
    }

    public static ActorResolution disabled() {
        return new ActorResolution(ActorResolutionStatus.DISABLED_IDENTITY, null);
    }

    public boolean isLinked() {
        return status == ActorResolutionStatus.LINKED;
    }
}
