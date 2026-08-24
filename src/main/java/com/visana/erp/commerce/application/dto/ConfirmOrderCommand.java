package com.visana.erp.commerce.application.dto;

import java.util.Objects;
import java.util.UUID;

public record ConfirmOrderCommand(UUID orderId) {
    public ConfirmOrderCommand {
        Objects.requireNonNull(orderId, "OrderId cannot be null");
    }
}
