package com.visana.erp.commerce.catalog.application;

import java.util.UUID;

public class CatalogProductNotFoundException extends RuntimeException {
    public CatalogProductNotFoundException(UUID productId) {
        super("catalog product was not found: " + productId);
    }
}
