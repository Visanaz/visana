package com.visana.erp.platform.application.identity;

public class IdentityConflictException extends RuntimeException {
    public IdentityConflictException() {
        super("The external identity is already linked.");
    }
}
