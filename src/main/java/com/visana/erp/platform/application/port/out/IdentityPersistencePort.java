package com.visana.erp.platform.application.port.out;

import com.visana.erp.platform.application.identity.IdentityLinkResolution;
import com.visana.erp.platform.domain.identity.ExternalIdentity;
import com.visana.erp.platform.domain.identity.ExternalIdentityLink;
import com.visana.erp.platform.domain.identity.PlatformActor;

import java.util.Optional;

public interface IdentityPersistencePort {
    Optional<IdentityLinkResolution> findByExternalIdentity(ExternalIdentity externalIdentity);

    boolean externalIdentityExists(ExternalIdentity externalIdentity);

    void persist(PlatformActor actor, ExternalIdentityLink link);

    boolean disable(ExternalIdentity externalIdentity);
}
