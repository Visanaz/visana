package com.visana.erp.platform.application.ownership;

public class OwnershipDeniedException extends RuntimeException {
    public OwnershipDeniedException() {
        super("Access to the requested resource is denied.");
    }
}
