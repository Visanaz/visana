package com.visana.erp.qualification.domain.volume;

import com.visana.erp.core.domain.model.Money;

@FunctionalInterface
public interface QualificationBasePolicy {
    Money qualificationBase(SaleEvidence evidence);
}
