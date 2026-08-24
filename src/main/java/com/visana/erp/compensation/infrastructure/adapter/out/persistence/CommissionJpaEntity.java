package com.visana.erp.compensation.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "commissions")
@Getter
@Setter
public class CommissionJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "empresa_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private String empresaId;

    @Column(name = "beneficiary_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private String beneficiaryId;

    @Column(name = "order_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private String orderId;

    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "network_level")
    private Integer networkLevel;
}
