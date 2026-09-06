package com.visana.erp.qualification.domain.volume;

import com.visana.erp.core.domain.model.Money;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record SaleEvidence(
        UUID evidenceId,
        UUID sellerMemberId,
        Instant occurredAt,
        String lifecycleState,
        SaleMonetaryComponents monetaryComponents,
        Money adjustment,
        String sourceReference) {

    public SaleEvidence {
        Objects.requireNonNull(evidenceId, "Evidence id cannot be null");
        Objects.requireNonNull(sellerMemberId, "Seller member id cannot be null");
        Objects.requireNonNull(occurredAt, "Sale timestamp cannot be null");
        if (lifecycleState == null || lifecycleState.isBlank()) {
            throw new IllegalArgumentException("Lifecycle state cannot be blank");
        }
        Objects.requireNonNull(monetaryComponents, "Monetary components cannot be null");
        Objects.requireNonNull(adjustment, "Adjustment cannot be null");
        if (sourceReference == null || sourceReference.isBlank()) {
            throw new IllegalArgumentException("Evidence source cannot be blank");
        }
    }
}
