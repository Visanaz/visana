package com.visana.erp.network.domain.exception;

import com.visana.erp.core.domain.exception.DomainException;
import java.util.UUID;

public class NodeNotFoundException extends DomainException {
    public NodeNotFoundException(UUID nodeId) {
        super("Network node not found for ID: " + nodeId);
    }
}
