package com.visana.erp.platform.application.authorization;

import com.visana.erp.platform.application.audit.AuditEventWriter;
import com.visana.erp.platform.application.port.out.ResourceOwnershipPersistencePort;
import com.visana.erp.platform.domain.audit.AuditEvent;
import com.visana.erp.platform.domain.identity.PlatformActorId;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static com.visana.erp.core.infrastructure.adapter.in.web.filter.CorrelationIdFilter.MDC_KEY;

/** Ownership-only policy. Role overrides remain blocked until a source-backed role matrix exists. */
@Component
public class PersistentOwnershipAuthorizationPolicy implements AuthorizationPolicy {
    private static final String ORDER = "ORDER";
    private static final String SPONSOR_RELATIONSHIP = "SPONSOR_RELATIONSHIP";

    private final ResourceOwnershipPersistencePort ownershipPersistencePort;
    private final AuditEventWriter auditEventWriter;
    private final Counter authorizationDenied;

    public PersistentOwnershipAuthorizationPolicy(ResourceOwnershipPersistencePort ownershipPersistencePort,
                                                  AuditEventWriter auditEventWriter, MeterRegistry meterRegistry) {
        this.ownershipPersistencePort = ownershipPersistencePort;
        this.auditEventWriter = auditEventWriter;
        this.authorizationDenied = Counter.builder("authorization_denied").register(meterRegistry);
    }

    @Override
    public boolean canReadOrder(UUID actorId, UUID orderId) {
        return owns(actorId, "ORDER_READ", ORDER, orderId);
    }

    @Override
    public boolean canModifyOrder(UUID actorId, UUID orderId) {
        return owns(actorId, "ORDER_MODIFY", ORDER, orderId);
    }

    @Override
    public boolean canConfirmPayment(UUID actorId, UUID orderId) {
        return owns(actorId, "ORDER_CONFIRM_PAYMENT", ORDER, orderId);
    }

    @Override
    public boolean canAssignSponsor(UUID actorId, UUID sponsorId) {
        return owns(actorId, "SPONSOR_ASSIGN", SPONSOR_RELATIONSHIP, sponsorId);
    }

    private boolean owns(UUID actorId, String action, String resourceType, UUID resourceId) {
        boolean allowed = ownershipPersistencePort.findOwner(resourceType, resourceId)
                .map(owner -> owner.equals(PlatformActorId.of(actorId)))
                .orElse(false);
        if (!allowed) {
            authorizationDenied.increment();
            auditEventWriter.append(new AuditEvent(actorId.toString(), "AUTHORIZATION_DENIED", resourceType,
                    resourceId.toString(), Instant.now(), correlationId(), Map.of("action", action)));
        }
        return allowed;
    }

    private String correlationId() {
        String value = MDC.get(MDC_KEY);
        return value == null ? "system" : value;
    }
}
