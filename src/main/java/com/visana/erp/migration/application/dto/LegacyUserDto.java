package com.visana.erp.migration.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record LegacyUserDto(
        UUID id,
        UUID empresaId,
        UUID sponsorId,
        String role,
        String status
) {
}
