package com.visana.erp.network.infrastructure.adapter.in.web;

import com.visana.erp.network.application.port.in.CreateNetworkNodeCommand;
import com.visana.erp.network.application.port.in.CreateNetworkNodeUseCase;
import com.visana.erp.network.foundation.application.NetworkOperationForbiddenException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/network-nodes")
@Tag(name = "Network Nodes", description = "Endpoints for managing the network genealogy (Affiliates and Distributors)")
public class NetworkNodeController {

    private final CreateNetworkNodeUseCase createNetworkNodeUseCase;

    public NetworkNodeController(CreateNetworkNodeUseCase createNetworkNodeUseCase) {
        this.createNetworkNodeUseCase = createNetworkNodeUseCase;
    }

    @PostMapping
    @Operation(operationId = "createLegacyNetworkNodeBlocked", summary = "Create a legacy network node", description = "LEGACY / BLOCKED: this route always denies creation until affiliation controls are approved; it does not create a node.")
    public ResponseEntity<NetworkNodeResponse> createNode(
            @RequestBody CreateNetworkNodeRequest request,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        
        throw new NetworkOperationForbiddenException("legacy network-node creation is blocked until affiliation controls are approved");
        /* UUID tenantId = UUID.fromString(jwt.getClaimAsString("tenant_id"));
        CreateNetworkNodeCommand command = new CreateNetworkNodeCommand(tenantId, request.sponsorId(), request.role());
        UUID newAffiliateId = createNetworkNodeUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(new NetworkNodeResponse(newAffiliateId)); */
    }

    public record CreateNetworkNodeRequest(UUID sponsorId, String role) {}

    public record NetworkNodeResponse(UUID affiliateId) {}
}
