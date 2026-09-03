package com.visana.erp.platform.application.identity;

import com.visana.erp.core.infrastructure.adapter.in.web.filter.CorrelationIdFilter;
import com.visana.erp.platform.application.audit.AuditEventWriter;
import com.visana.erp.platform.application.port.out.IdentityPersistencePort;
import com.visana.erp.platform.domain.audit.AuditEvent;
import com.visana.erp.platform.domain.identity.ExternalIdentityLinkStatus;
import com.visana.erp.platform.domain.identity.PlatformActorStatus;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class ActorResolverService implements ActorResolverPort {
    private final IdentityPersistencePort identityPersistencePort;
    private final IdentityResolutionMetrics metrics;
    private final AuditEventWriter auditEventWriter;

    public ActorResolverService(IdentityPersistencePort identityPersistencePort, IdentityResolutionMetrics metrics,
                                AuditEventWriter auditEventWriter) {
        this.identityPersistencePort = identityPersistencePort;
        this.metrics = metrics;
        this.auditEventWriter = auditEventWriter;
    }

    @Override
    public ActorResolution resolve(AuthenticatedPrincipal authenticatedPrincipal) {
        return identityPersistencePort.findByExternalIdentity(authenticatedPrincipal.externalIdentity())
                .map(link -> resolveLinked(link, authenticatedPrincipal))
                .orElseGet(() -> unresolved(ActorResolutionStatus.UNLINKED_IDENTITY));
    }

    private ActorResolution resolveLinked(IdentityLinkResolution link, AuthenticatedPrincipal principal) {
        if (link.linkStatus() == ExternalIdentityLinkStatus.DISABLED
                || link.actor().status() == PlatformActorStatus.DISABLED) {
            return unresolved(ActorResolutionStatus.DISABLED_IDENTITY);
        }
        return ActorResolution.linked(link.actor().id());
    }

    private ActorResolution unresolved(ActorResolutionStatus status) {
        if (status == ActorResolutionStatus.UNLINKED_IDENTITY) {
            metrics.recordUnlinked();
        } else {
            metrics.recordDisabled();
        }
        auditEventWriter.append(new AuditEvent(null, "IDENTITY_RESOLUTION_DENIED", "PLATFORM_IDENTITY",
                status.name(), Instant.now(), correlationId(), Map.of("status", status.name())));
        return status == ActorResolutionStatus.UNLINKED_IDENTITY ? ActorResolution.unlinked() : ActorResolution.disabled();
    }

    private String correlationId() {
        String value = MDC.get(CorrelationIdFilter.MDC_KEY);
        return value == null ? "system" : value;
    }
}
