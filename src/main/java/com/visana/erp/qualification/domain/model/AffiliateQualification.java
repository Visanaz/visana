package com.visana.erp.qualification.domain.model;

import com.visana.erp.core.domain.model.Period;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import java.util.Objects;

public class AffiliateQualification {
    private final TenantId tenantId;
    private final AffiliateId affiliateId;
    private final Period period;
    private final boolean isActivated;
    private final boolean isQualified;
    private final int currentQualifiedLevel;

    public AffiliateQualification(TenantId tenantId, AffiliateId affiliateId, Period period, 
                                  boolean isActivated, boolean isQualified, int currentQualifiedLevel) {
        this.tenantId = Objects.requireNonNull(tenantId, "TenantId cannot be null");
        this.affiliateId = Objects.requireNonNull(affiliateId, "AffiliateId cannot be null");
        this.period = Objects.requireNonNull(period, "Period cannot be null");
        
        if (currentQualifiedLevel < 0) {
            throw new IllegalArgumentException("Qualified level cannot be negative");
        }
        if (isQualified && !isActivated) {
            throw new IllegalStateException("An affiliate cannot be qualified if they are not activated");
        }
        
        this.isActivated = isActivated;
        this.isQualified = isQualified;
        this.currentQualifiedLevel = currentQualifiedLevel;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public AffiliateId getAffiliateId() {
        return affiliateId;
    }

    public Period getPeriod() {
        return period;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public boolean isQualified() {
        return isQualified;
    }

    public int getCurrentQualifiedLevel() {
        return currentQualifiedLevel;
    }
}
