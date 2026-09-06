package com.visana.erp.qualification.domain.volume;

@FunctionalInterface
public interface QualifyingSalePolicy {
    boolean qualifies(SaleEvidence evidence);
}
