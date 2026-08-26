package com.visana.erp.commerce.domain.exception;

import com.visana.erp.core.domain.exception.DomainException;
import java.util.UUID;

public class InvalidOrderStateException extends DomainException {
    public InvalidOrderStateException(UUID orderId, String state) {
        super("Invalid operation for order " + orderId + " in state " + state);
    }
}
