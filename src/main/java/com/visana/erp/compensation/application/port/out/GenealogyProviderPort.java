package com.visana.erp.compensation.application.port.out;

import com.visana.erp.network.domain.model.AffiliateId;
import com.visana.erp.network.domain.model.GenealogyNode;
import java.util.List;

public interface GenealogyProviderPort {
    List<GenealogyNode> getUpline(AffiliateId affiliateId, int maxLevels);
}
