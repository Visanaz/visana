package com.visana.erp.network.infrastructure.adapter.in.web;

import com.visana.erp.network.application.port.in.CreateNetworkNodeCommand;
import com.visana.erp.network.application.port.in.CreateNetworkNodeUseCase;
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
    @Operation(summary = "Create a new network node", description = "Registers a new Affiliate or Distributor in the network under a specific Sponsor (optional for roots).")
    public ResponseEntity<NetworkNodeResponse> createNode(@RequestBody CreateNetworkNodeCommand command) {
        UUID newAffiliateId = createNetworkNodeUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(new NetworkNodeResponse(newAffiliateId));
    }

    public record NetworkNodeResponse(UUID affiliateId) {}
}
