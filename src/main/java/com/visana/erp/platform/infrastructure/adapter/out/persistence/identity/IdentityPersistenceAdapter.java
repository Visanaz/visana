package com.visana.erp.platform.infrastructure.adapter.out.persistence.identity;

import com.visana.erp.platform.application.identity.IdentityConflictException;
import com.visana.erp.platform.application.identity.IdentityLinkResolution;
import com.visana.erp.platform.application.port.out.IdentityPersistencePort;
import com.visana.erp.platform.domain.identity.ExternalIdentity;
import com.visana.erp.platform.domain.identity.ExternalIdentityLink;
import com.visana.erp.platform.domain.identity.PlatformActor;
import com.visana.erp.platform.domain.identity.PlatformActorId;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class IdentityPersistenceAdapter implements IdentityPersistencePort {
    private final PlatformActorSpringDataRepository actorRepository;
    private final ExternalIdentityLinkSpringDataRepository linkRepository;

    public IdentityPersistenceAdapter(PlatformActorSpringDataRepository actorRepository,
                                      ExternalIdentityLinkSpringDataRepository linkRepository) {
        this.actorRepository = actorRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    public Optional<IdentityLinkResolution> findByExternalIdentity(ExternalIdentity externalIdentity) {
        return linkRepository.findByProviderAndIssuerAndSubject(externalIdentity.provider(), externalIdentity.issuer(), externalIdentity.subject())
                .flatMap(link -> actorRepository.findById(link.actorId())
                        .map(actor -> new IdentityLinkResolution(new PlatformActor(PlatformActorId.of(actor.actorId()), actor.status()), link.status())));
    }

    @Override
    public boolean externalIdentityExists(ExternalIdentity externalIdentity) {
        return linkRepository.findByProviderAndIssuerAndSubject(externalIdentity.provider(), externalIdentity.issuer(), externalIdentity.subject()).isPresent();
    }

    @Override
    public void persist(PlatformActor actor, ExternalIdentityLink link) {
        actorRepository.save(new PlatformActorJpaEntity(actor.id().value(), actor.status(), Instant.now()));
        try {
            linkRepository.saveAndFlush(new ExternalIdentityLinkJpaEntity(link.id(), link.externalIdentity().provider(),
                    link.externalIdentity().issuer(), link.externalIdentity().subject(), link.actorId().value(),
                    link.status(), link.linkedAt()));
        } catch (DataIntegrityViolationException ex) {
            throw new IdentityConflictException();
        }
    }

    @Override
    public boolean disable(ExternalIdentity externalIdentity) {
        return linkRepository.findByProviderAndIssuerAndSubject(externalIdentity.provider(), externalIdentity.issuer(), externalIdentity.subject())
                .map(link -> {
                    link.disable(Instant.now());
                    linkRepository.save(link);
                    return true;
                })
                .orElse(false);
    }
}
