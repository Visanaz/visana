package com.visana.erp.qualification.domain.activation;

import java.time.ZonedDateTime;

@FunctionalInterface
public interface ActivationDurationPolicy {
    ZonedDateTime expiryFrom(ZonedDateTime activationStart);
}
