package com.visana.erp.compensation.domain.model;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.network.domain.model.AffiliateId;

import java.util.Objects;

/**
 * Entidad inmutable que representa una comisión generada para un Afiliado
 * como resultado de una Orden de Compra pagada dentro de su red.
 *
 * Por la Regla del Ledger Inmutable (architecture-rules.mcp.md), esta entidad
 * NO se modifica. Si hay un ajuste, se crea una nueva Commission de tipo REVERSED.
 */
public final class Commission {
    private final TenantId tenantId;
    private final CommissionId commissionId;
    private final AffiliateId beneficiaryId;
    private final OrderId sourceOrderId;
    private final Money amount;
    private final CommissionType type;
    private final CommissionStatus status;
    private final int networkLevel;

    private Commission(TenantId tenantId, CommissionId commissionId, AffiliateId beneficiaryId,
                       OrderId sourceOrderId, Money amount, CommissionType type,
                       CommissionStatus status, int networkLevel) {
        this.tenantId = Objects.requireNonNull(tenantId, "TenantId cannot be null");
        this.commissionId = Objects.requireNonNull(commissionId, "CommissionId cannot be null");
        this.beneficiaryId = Objects.requireNonNull(beneficiaryId, "Beneficiary AffiliateId cannot be null");
        this.sourceOrderId = Objects.requireNonNull(sourceOrderId, "Source OrderId cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.type = Objects.requireNonNull(type, "CommissionType cannot be null");
        this.status = Objects.requireNonNull(status, "CommissionStatus cannot be null");
        if (networkLevel < 1) {
            throw new IllegalArgumentException("Network level must be >= 1");
        }
        this.networkLevel = networkLevel;
    }

    public static Commission calculate(TenantId tenantId, AffiliateId beneficiaryId,
                                       OrderId sourceOrderId, Money amount,
                                       CommissionType type, int networkLevel) {
        return new Commission(
                tenantId,
                CommissionId.generate(),
                beneficiaryId,
                sourceOrderId,
                amount,
                type,
                CommissionStatus.CALCULATED,
                networkLevel
        );
    }

    /**
     * Crea una comisión de reverso por el principio de Ledger Inmutable.
     * Nunca se modifica una comisión histórica; en cambio, se genera su opuesto.
     */
    public Commission reverse() {
        return new Commission(
                this.tenantId,
                CommissionId.generate(),
                this.beneficiaryId,
                this.sourceOrderId,
                Money.reversal(this.amount.amount()),
                this.type,
                CommissionStatus.REVERSED,
                this.networkLevel
        );
    }

    public static Commission reconstruct(TenantId tenantId, CommissionId commissionId, AffiliateId beneficiaryId, OrderId sourceOrderId, Money amount, CommissionType type, CommissionStatus status, int networkLevel) {
        return new Commission(tenantId, commissionId, beneficiaryId, sourceOrderId, amount, type, status, networkLevel);
    }

    public TenantId getTenantId() { return tenantId; }
    public CommissionId getCommissionId() { return commissionId; }
    public AffiliateId getBeneficiaryId() { return beneficiaryId; }
    public OrderId getSourceOrderId() { return sourceOrderId; }
    public Money getAmount() { return amount; }
    public CommissionType getType() { return type; }
    public CommissionStatus getStatus() { return status; }
    public int getNetworkLevel() { return networkLevel; }
}
