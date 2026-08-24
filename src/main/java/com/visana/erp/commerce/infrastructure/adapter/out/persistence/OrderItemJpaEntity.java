package com.visana.erp.commerce.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItemJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderJpaEntity order;

    @Column(name = "product_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", precision = 19, scale = 4, nullable = false)
    private BigDecimal unitPrice;
}
