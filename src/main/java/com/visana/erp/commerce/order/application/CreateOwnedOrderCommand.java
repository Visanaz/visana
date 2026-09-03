package com.visana.erp.commerce.order.application;
import java.util.List;
import java.util.UUID;
public record CreateOwnedOrderCommand(List<Line> lines) { public record Line(UUID productId, int quantity) {} }
