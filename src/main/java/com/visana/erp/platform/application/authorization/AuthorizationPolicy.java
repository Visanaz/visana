package com.visana.erp.platform.application.authorization;

import java.util.UUID;

/**
 * Policy seam for role authorization plus resource ownership.
 * Implementations are deliberately deferred until the ownership decisions are approved.
 */
public interface AuthorizationPolicy {

    boolean canReadOrder(UUID actorId, UUID orderId);

    boolean canModifyOrder(UUID actorId, UUID orderId);

    boolean canConfirmPayment(UUID actorId, UUID orderId);

    boolean canAssignSponsor(UUID actorId, UUID sponsorId);
}
