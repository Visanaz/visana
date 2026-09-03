package com.visana.erp.commerce.order.application;

public class OwnedOrderNotFoundException extends RuntimeException {
    public OwnedOrderNotFoundException() { super("order was not found"); }
}
