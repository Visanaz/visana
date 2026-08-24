package com.visana.erp.commerce.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "empresa_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private String empresaId;

    @Column(name = "affiliate_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private String affiliateId;

    @Column(name = "order_type", nullable = false)
    private String orderType;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemJpaEntity> items = new ArrayList<>();
}
