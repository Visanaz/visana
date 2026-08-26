package com.visana.erp.network.application.port.in;

import java.util.UUID;

public record CreateNetworkNodeCommand(UUID tenantId, UUID sponsorId, String role) {
}
