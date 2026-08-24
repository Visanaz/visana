package com.visana.erp.qualification.domain.model;

import com.visana.erp.core.domain.model.Money;
import java.util.Objects;

public record VolumeRequirement(Money minTeamSales) {
    public VolumeRequirement {
        Objects.requireNonNull(minTeamSales, "Minimum team sales cannot be null");
        if (minTeamSales.isReversal()) {
            throw new IllegalArgumentException("Minimum team sales cannot be a reversal (negative)");
        }
    }
}
