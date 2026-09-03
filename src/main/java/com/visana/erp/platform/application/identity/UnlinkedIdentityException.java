package com.visana.erp.platform.application.identity;

public class UnlinkedIdentityException extends RuntimeException {
    public UnlinkedIdentityException() {
        super("The authenticated identity is not linked to a platform actor.");
    }
}
