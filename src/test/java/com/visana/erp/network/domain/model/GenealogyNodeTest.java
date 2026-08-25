package com.visana.erp.network.domain.model;

import com.visana.erp.core.domain.model.TenantId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenealogyNodeTest {

    private final TenantId defaultTenant = TenantId.generate();
    private final AffiliateId defaultAffiliate = AffiliateId.generate();
    private final SponsorId defaultSponsor = SponsorId.generate();

    @Test
    void shouldCreateRootNode() {
        GenealogyNode root = GenealogyNode.createRoot(defaultTenant, defaultAffiliate, NetworkRole.ADMIN);
        
        assertEquals(defaultTenant, root.getTenantId());
        assertEquals(defaultAffiliate, root.getAffiliateId());
        assertNull(root.getSponsorId());
        assertEquals(NetworkRole.ADMIN, root.getRole());
        assertEquals(NodeStatus.ACTIVE, root.getStatus());
        assertTrue(root.isRoot());
    }

    @Test
    void shouldCreateNormalNode() {
        GenealogyNode node = GenealogyNode.create(defaultTenant, defaultAffiliate, defaultSponsor, NetworkRole.AFFILIATE);
        
        assertEquals(defaultTenant, node.getTenantId());
        assertEquals(defaultAffiliate, node.getAffiliateId());
        assertEquals(defaultSponsor, node.getSponsorId());
        assertEquals(NetworkRole.AFFILIATE, node.getRole());
        assertEquals(NodeStatus.INACTIVE, node.getStatus());
        assertFalse(node.isRoot());
    }

    @Test
    void shouldThrowExceptionIfNormalNodeHasNullSponsor() {
        assertThrows(NullPointerException.class, () -> {
            GenealogyNode.create(defaultTenant, defaultAffiliate, null, NetworkRole.AFFILIATE);
        });
    }

    @Test
    void shouldThrowExceptionIfNodeIsItsOwnSponsor() {
        SponsorId selfSponsor = SponsorId.of(defaultAffiliate.value());
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            GenealogyNode.create(defaultTenant, defaultAffiliate, selfSponsor, NetworkRole.AFFILIATE);
        });
        
        assertTrue(exception.getMessage().contains("A node cannot be its own sponsor"));
    }

    @Test
    void shouldChangeSponsor() {
        GenealogyNode node = GenealogyNode.create(defaultTenant, defaultAffiliate, defaultSponsor, NetworkRole.AFFILIATE);
        SponsorId newSponsor = SponsorId.generate();
        
        node.changeSponsor(newSponsor);
        
        assertEquals(newSponsor, node.getSponsorId());
    }

    @Test
    void shouldThrowExceptionWhenChangingSponsorToSelf() {
        GenealogyNode node = GenealogyNode.create(defaultTenant, defaultAffiliate, defaultSponsor, NetworkRole.AFFILIATE);
        SponsorId selfSponsor = SponsorId.of(defaultAffiliate.value());
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            node.changeSponsor(selfSponsor);
        });
        
        assertTrue(exception.getMessage().contains("A node cannot be its own sponsor"));
    }

    @Test
    void shouldThrowExceptionWhenChangingSponsorOfRootNode() {
        GenealogyNode root = GenealogyNode.createRoot(defaultTenant, defaultAffiliate, NetworkRole.ADMIN);
        SponsorId newSponsor = SponsorId.generate();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            root.changeSponsor(newSponsor);
        });
        
        assertTrue(exception.getMessage().contains("Cannot change sponsor of a root node"));
    }
    
    @Test
    void shouldActivateNode() {
        GenealogyNode node = GenealogyNode.create(defaultTenant, defaultAffiliate, defaultSponsor, NetworkRole.AFFILIATE);
        assertEquals(NodeStatus.INACTIVE, node.getStatus());
        
        node.activate();
        
        assertEquals(NodeStatus.ACTIVE, node.getStatus());
    }
    
    @Test
    void shouldThrowExceptionWhenActivatingAlreadyActiveNode() {
        GenealogyNode root = GenealogyNode.createRoot(defaultTenant, defaultAffiliate, NetworkRole.ADMIN);
        assertEquals(NodeStatus.ACTIVE, root.getStatus());
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            root.activate();
        });
        
        assertTrue(exception.getMessage().contains("Node is already active"));
    }

    @Test
    void shouldDeactivateNode() {
        GenealogyNode node = GenealogyNode.createRoot(defaultTenant, defaultAffiliate, NetworkRole.ADMIN);
        assertEquals(NodeStatus.ACTIVE, node.getStatus());
        
        node.deactivate();
        
        assertEquals(NodeStatus.INACTIVE, node.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenDeactivatingAlreadyInactiveNode() {
        GenealogyNode node = GenealogyNode.create(defaultTenant, defaultAffiliate, defaultSponsor, NetworkRole.AFFILIATE);
        assertEquals(NodeStatus.INACTIVE, node.getStatus());
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            node.deactivate();
        });
        
        assertTrue(exception.getMessage().contains("Node is already inactive"));
    }
}
