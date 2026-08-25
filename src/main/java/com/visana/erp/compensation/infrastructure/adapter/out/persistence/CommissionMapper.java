package com.visana.erp.compensation.infrastructure.adapter.out.persistence;

import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.compensation.domain.model.Commission;
import com.visana.erp.compensation.domain.model.CommissionId;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import org.springframework.stereotype.Component;

@Component
public class CommissionMapper {

    public CommissionJpaEntity toJpaEntity(Commission commission) {
        CommissionJpaEntity entity = new CommissionJpaEntity();
        entity.setId(commission.getCommissionId().value());
        entity.setTenantId(commission.getTenantId().value());
        entity.setBeneficiaryId(commission.getBeneficiaryId().value());
        entity.setOrderId(commission.getSourceOrderId().value());
        entity.setAmount(commission.getAmount().amount());
        entity.setType(commission.getType());
        entity.setStatus(commission.getStatus());
        entity.setNetworkLevel(commission.getNetworkLevel());
        return entity;
    }

    public Commission toDomainEntity(CommissionJpaEntity entity) {
        return Commission.reconstruct(
                TenantId.of(entity.getTenantId()),
                CommissionId.of(entity.getId()),
                AffiliateId.of(entity.getBeneficiaryId()),
                OrderId.of(entity.getOrderId()),
                Money.of(entity.getAmount()),
                entity.getType(),
                entity.getStatus(),
                entity.getNetworkLevel() == null ? 1 : entity.getNetworkLevel()
        );
    }
}
