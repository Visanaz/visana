package com.visana.erp.network.domain.model;

import com.visana.erp.core.domain.model.TenantId;

import java.util.Objects;

public class GenealogyNode {
    private final TenantId tenantId;
    private final AffiliateId affiliateId;
    private SponsorId sponsorId;
    private NetworkRole role;
    private NodeStatus status;

    private GenealogyNode(TenantId tenantId, AffiliateId affiliateId, SponsorId sponsorId, NetworkRole role, NodeStatus status) {
        this.tenantId = Objects.requireNonNull(tenantId, "TenantId cannot be null");
        this.affiliateId = Objects.requireNonNull(affiliateId, "AffiliateId cannot be null");
        this.role = Objects.requireNonNull(role, "NetworkRole cannot be null");
        this.status = Objects.requireNonNull(status, "NodeStatus cannot be null");
        
        setSponsorId(sponsorId);
    }

    public static GenealogyNode createRoot(TenantId tenantId, AffiliateId affiliateId, NetworkRole role) {
        return new GenealogyNode(tenantId, affiliateId, null, role, NodeStatus.ACTIVE);
    }

    public static GenealogyNode create(TenantId tenantId, AffiliateId affiliateId, SponsorId sponsorId, NetworkRole role) {
        Objects.requireNonNull(sponsorId, "SponsorId cannot be null for non-root nodes");
        return new GenealogyNode(tenantId, affiliateId, sponsorId, role, NodeStatus.INACTIVE);
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public AffiliateId getAffiliateId() {
        return affiliateId;
    }

    public SponsorId getSponsorId() {
        return sponsorId;
    }

    public NetworkRole getRole() {
        return role;
    }

    public NodeStatus getStatus() {
        return status;
    }
    
    public boolean isRoot() {
        return this.sponsorId == null;
    }

    public void changeSponsor(SponsorId newSponsor) {
        Objects.requireNonNull(newSponsor, "New SponsorId cannot be null");
        if (this.isRoot()) {
            throw new IllegalStateException("Cannot change sponsor of a root node");
        }
        setSponsorId(newSponsor);
    }

    public void activate() {
        if (this.status == NodeStatus.ACTIVE) {
            throw new IllegalStateException("Node is already active");
        }
        this.status = NodeStatus.ACTIVE;
    }
    
    public void deactivate() {
        if (this.status == NodeStatus.INACTIVE) {
            throw new IllegalStateException("Node is already inactive");
        }
        this.status = NodeStatus.INACTIVE;
    }

    private void setSponsorId(SponsorId newSponsorId) {
        if (newSponsorId != null && newSponsorId.value().equals(this.affiliateId.value())) {
            throw new IllegalArgumentException("A node cannot be its own sponsor");
        }
        this.sponsorId = newSponsorId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenealogyNode that = (GenealogyNode) o;
        return affiliateId.equals(that.affiliateId) && tenantId.equals(that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, affiliateId);
    }
}
