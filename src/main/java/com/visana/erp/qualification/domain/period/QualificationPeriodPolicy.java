package com.visana.erp.qualification.domain.period;

import java.time.Instant;

@FunctionalInterface
public interface QualificationPeriodPolicy {
    QualificationPeriod resolve(Instant evaluationTimestamp);
}
