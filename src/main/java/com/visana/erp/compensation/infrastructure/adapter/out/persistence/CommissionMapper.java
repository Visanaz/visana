package com.visana.erp.compensation.infrastructure.adapter.out.persistence;

import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.compensation.domain.model.Commission;
import com.visana.erp.compensation.domain.model.CommissionId;
import com.visana.erp.compensation.domain.model.CommissionStatus;
import com.visana.erp.compensation.domain.model.CommissionType;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommissionMapper {

    public CommissionJpaEntity toJpaEntity(Commission commission) {
        CommissionJpaEntity entity = new CommissionJpaEntity();
        entity.setId(commission.getCommissionId().value().toString());
        entity.setEmpresaId(commission.getTenantId().value().toString());
        entity.setBeneficiaryId(commission.getBeneficiaryId().value().toString());
        entity.setOrderId(commission.getSourceOrderId().value().toString());
        entity.setAmount(commission.getAmount().amount());
        entity.setType(commission.getType().name());
        entity.setStatus(commission.getStatus().name());
        entity.setNetworkLevel(commission.getNetworkLevel());
        return entity;
    }

    public Commission toDomainEntity(CommissionJpaEntity entity) {
        return Commission.reconstruct(
                TenantId.of(entity.getEmpresaId()),
                CommissionId.of(entity.getId()),
                AffiliateId.of(entity.getBeneficiaryId()),
                OrderId.of(entity.getOrderId()),
                Money.of(entity.getAmount()),
                CommissionType.valueOf(entity.getType()),
                CommissionStatus.valueOf(entity.getStatus()),
                entity.getNetworkLevel() == null ? 1 : entity.getNetworkLevel()
        );
    }
}
