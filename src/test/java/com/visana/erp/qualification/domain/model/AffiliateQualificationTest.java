package com.visana.erp.qualification.domain.model;

import com.visana.erp.core.domain.model.Period;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class AffiliateQualificationTest {

    private final TenantId defaultTenant = TenantId.generate();
    private final AffiliateId defaultAffiliate = AffiliateId.generate();
    private final Period defaultPeriod = Period.of(YearMonth.of(2026, 6));

    @Test
    void shouldCreateQualification() {
        AffiliateQualification qualification = new AffiliateQualification(
                defaultTenant, defaultAffiliate, defaultPeriod, true, true, 3);
        
        assertEquals(defaultTenant, qualification.getTenantId());
        assertEquals(defaultAffiliate, qualification.getAffiliateId());
        assertEquals(defaultPeriod, qualification.getPeriod());
        assertTrue(qualification.isActivated());
        assertTrue(qualification.isQualified());
        assertEquals(3, qualification.getCurrentQualifiedLevel());
    }

    @Test
    void shouldThrowExceptionWhenQualifiedLevelIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new AffiliateQualification(defaultTenant, defaultAffiliate, defaultPeriod, true, true, -1);
        });
        
        assertTrue(exception.getMessage().contains("Qualified level cannot be negative"));
    }

    @Test
    void shouldThrowExceptionWhenQualifiedButNotActivated() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            new AffiliateQualification(defaultTenant, defaultAffiliate, defaultPeriod, false, true, 3);
        });
        
        assertTrue(exception.getMessage().contains("An affiliate cannot be qualified if they are not activated"));
    }
}
