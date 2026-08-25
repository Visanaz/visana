package com.visana.erp.compensation.infrastructure.adapter.out.persistence;

import com.visana.erp.compensation.domain.model.CommissionStatus;
import com.visana.erp.compensation.domain.model.CommissionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "commissions")
@Getter
@Setter
public class CommissionJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "tenant_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private UUID tenantId;

    @Column(name = "beneficiary_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private UUID beneficiaryId;

    @Column(name = "order_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private UUID orderId;

    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CommissionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CommissionStatus status;

    @Column(name = "network_level")
    private Integer networkLevel;
}
