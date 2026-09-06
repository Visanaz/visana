package com.visana.erp.qualification.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AllDepthsIndirectCountPolicyTest {
    @Test
    void countsUniqueDescendantsBelowDirectDepth() {
        UUID direct = UUID.randomUUID();
        UUID indirect = UUID.randomUUID();

        int count = new AllDepthsIndirectCountPolicy().count(List.of(
                new GenealogyMemberDepth(UUID.randomUUID(), 0),
                new GenealogyMemberDepth(direct, 1),
                new GenealogyMemberDepth(indirect, 2),
                new GenealogyMemberDepth(indirect, 3)));

        assertEquals(1, count);
    }
}
