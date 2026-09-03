package com.visana.erp.network.application.port.out;

import com.visana.erp.network.domain.model.AffiliateId;
import com.visana.erp.network.domain.model.GenealogyNode;

import java.util.Optional;

/**
 * Application port for network-node persistence and lookup.
 *
 * <p>This source contract restores the dependency already used by the current
 * application services. It intentionally does not prescribe storage, genealogy
 * traversal, or compensation behavior.</p>
 */
public interface NetworkNodeRepository {

    Optional<GenealogyNode> findById(AffiliateId affiliateId);

    void save(GenealogyNode node);
}
