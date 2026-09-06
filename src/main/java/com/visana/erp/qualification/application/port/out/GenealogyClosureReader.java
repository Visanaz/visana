package com.visana.erp.qualification.application.port.out;

import com.visana.erp.qualification.domain.network.GenealogyMemberDepth;
import java.util.List;
import java.util.UUID;

public interface GenealogyClosureReader {
    List<GenealogyMemberDepth> memberAndDescendants(UUID memberId);
}
