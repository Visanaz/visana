package com.visana.erp.compensation.application.port.out;

import com.visana.erp.core.domain.model.Period;
import com.visana.erp.network.domain.model.AffiliateId;
import com.visana.erp.qualification.domain.model.AffiliateQualification;

public interface QualificationProviderPort {
    AffiliateQualification getQualification(AffiliateId affiliateId, Period period);
}
