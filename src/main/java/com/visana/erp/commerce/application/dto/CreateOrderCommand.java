package com.visana.erp.commerce.application.dto;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        UUID tenantId,
        UUID affiliateId,
        String orderType,
        List<OrderItemDto> items
) {}
