package com.visana.erp.qualification.domain.network;

import java.util.Objects;
import java.util.UUID;

public record GenealogyMemberDepth(UUID memberId, int depth) {
    public GenealogyMemberDepth {
        Objects.requireNonNull(memberId, "Member id cannot be null");
        if (depth < 0) {
            throw new IllegalArgumentException("Genealogy depth cannot be negative");
        }
    }
}
