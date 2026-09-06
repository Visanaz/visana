package com.visana.erp.qualification.domain.network;

import java.util.Collection;

@FunctionalInterface
public interface IndirectCountPolicy {
    int count(Collection<GenealogyMemberDepth> genealogy);
}
