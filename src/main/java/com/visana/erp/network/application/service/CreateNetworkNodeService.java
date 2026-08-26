package com.visana.erp.network.application.service;

import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.application.port.in.CreateNetworkNodeCommand;
import com.visana.erp.network.application.port.in.CreateNetworkNodeUseCase;
import com.visana.erp.network.application.port.out.NetworkNodeRepository;
import com.visana.erp.network.domain.exception.InactiveSponsorException;
import com.visana.erp.network.domain.exception.NodeNotFoundException;
import com.visana.erp.network.domain.model.AffiliateId;
import com.visana.erp.network.domain.model.GenealogyNode;
import com.visana.erp.network.domain.model.NetworkRole;
import com.visana.erp.network.domain.model.NodeStatus;
import com.visana.erp.network.domain.model.SponsorId;

import java.util.UUID;

public class CreateNetworkNodeService implements CreateNetworkNodeUseCase {

    private final NetworkNodeRepository repository;

    public CreateNetworkNodeService(NetworkNodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public UUID execute(CreateNetworkNodeCommand command) {
        TenantId tenantId = TenantId.of(command.tenantId());
        AffiliateId newAffiliateId = AffiliateId.of(UUID.randomUUID());
        NetworkRole role = NetworkRole.valueOf(command.role());

        GenealogyNode newNode;

        if (command.sponsorId() != null) {
            SponsorId sponsorId = SponsorId.of(command.sponsorId());
            AffiliateId sponsorAffiliateId = AffiliateId.of(command.sponsorId());

            GenealogyNode sponsorNode = repository.findById(sponsorAffiliateId)
                    .orElseThrow(() -> new NodeNotFoundException(command.sponsorId()));

            if (sponsorNode.getStatus() != NodeStatus.ACTIVE) {
                throw new InactiveSponsorException(command.sponsorId());
            }

            newNode = GenealogyNode.create(tenantId, newAffiliateId, sponsorId, role);
            
            // As required by the business rule, the node must be created with ACTIVE status 
            // after the sponsor is verified. 
            newNode.activate();
        } else {
            newNode = GenealogyNode.createRoot(tenantId, newAffiliateId, role);
        }

        repository.save(newNode);
        return newAffiliateId.value();
    }
}
