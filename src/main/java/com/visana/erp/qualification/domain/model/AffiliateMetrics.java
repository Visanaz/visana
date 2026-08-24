package com.visana.erp.qualification.domain.model;

import com.visana.erp.core.domain.model.Money;
import java.util.Objects;

public record AffiliateMetrics(int directs, int indirects, Money teamSales) {
    public AffiliateMetrics {
        Objects.requireNonNull(teamSales, "Team sales cannot be null");
        if (directs < 0 || indirects < 0) {
            throw new IllegalArgumentException("Directs and indirects cannot be negative");
        }
    }
}
