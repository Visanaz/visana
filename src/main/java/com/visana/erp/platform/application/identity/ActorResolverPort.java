package com.visana.erp.platform.application.identity;

public interface ActorResolverPort {
    ActorResolution resolve(AuthenticatedPrincipal authenticatedPrincipal);
}
