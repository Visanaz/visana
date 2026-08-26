package com.visana.erp.commerce.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(UUID productId, int quantity, BigDecimal unitPrice) {}
