package com.visana.erp.platform.application.identity;

import com.visana.erp.platform.domain.identity.ExternalIdentityLinkStatus;
import com.visana.erp.platform.domain.identity.PlatformActor;

import java.util.Objects;

public record IdentityLinkResolution(PlatformActor actor, ExternalIdentityLinkStatus linkStatus) {
    public IdentityLinkResolution {
        Objects.requireNonNull(actor, "actor cannot be null");
        Objects.requireNonNull(linkStatus, "link status cannot be null");
    }
}
