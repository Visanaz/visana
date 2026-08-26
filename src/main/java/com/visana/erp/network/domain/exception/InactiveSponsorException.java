package com.visana.erp.network.domain.exception;

import com.visana.erp.core.domain.exception.DomainException;
import java.util.UUID;

public class InactiveSponsorException extends DomainException {
    public InactiveSponsorException(UUID sponsorId) {
        super("Sponsor with ID " + sponsorId + " is not ACTIVE.");
    }
}
