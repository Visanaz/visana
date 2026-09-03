package com.visana.erp.platform.domain.identity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ExternalIdentityLink(
        UUID id,
        ExternalIdentity externalIdentity,
        PlatformActorId actorId,
        ExternalIdentityLinkStatus status,
        Instant linkedAt) {
    public ExternalIdentityLink {
        Objects.requireNonNull(id, "external identity link id cannot be null");
        Objects.requireNonNull(externalIdentity, "external identity cannot be null");
        Objects.requireNonNull(actorId, "platform actor id cannot be null");
        Objects.requireNonNull(status, "external identity link status cannot be null");
        Objects.requireNonNull(linkedAt, "linked at cannot be null");
    }
}
