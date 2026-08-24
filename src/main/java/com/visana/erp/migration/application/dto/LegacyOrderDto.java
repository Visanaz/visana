package com.visana.erp.migration.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record LegacyOrderDto(
        UUID id,
        UUID empresaId,
        UUID affiliateId,
        String orderType,
        String status,
        BigDecimal totalAmount
) {
}
