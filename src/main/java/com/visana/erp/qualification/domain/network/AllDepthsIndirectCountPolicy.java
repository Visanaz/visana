package com.visana.erp.qualification.domain.network;

import java.util.Collection;

/** Provisional strategy: indirects are all unique descendants below direct depth. */
public final class AllDepthsIndirectCountPolicy implements IndirectCountPolicy {
    @Override
    public int count(Collection<GenealogyMemberDepth> genealogy) {
        return (int) genealogy.stream()
                .filter(member -> member.depth() > 1)
                .map(GenealogyMemberDepth::memberId)
                .distinct()
                .count();
    }
}
