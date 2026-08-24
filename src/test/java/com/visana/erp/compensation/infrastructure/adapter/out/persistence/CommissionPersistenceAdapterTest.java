package com.visana.erp.compensation.infrastructure.adapter.out.persistence;

import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.compensation.domain.model.Commission;
import com.visana.erp.compensation.domain.model.CommissionType;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({CommissionPersistenceAdapter.class, CommissionMapper.class})
class CommissionPersistenceAdapterTest {

    @Autowired
    private CommissionPersistenceAdapter adapter;

    @Autowired
    private CommissionSpringDataRepository repository;

    @Test
    void shouldSaveAllCommissions() {
        Commission commission1 = Commission.calculate(
                TenantId.generate(),
                AffiliateId.generate(),
                OrderId.generate(),
                Money.of(new BigDecimal("50.00")),
                CommissionType.UNILEVEL_BONUS,
                1
        );
        
        Commission commission2 = Commission.calculate(
                TenantId.generate(),
                AffiliateId.generate(),
                OrderId.generate(),
                Money.of(new BigDecimal("25.00")),
                CommissionType.UNILEVEL_BONUS,
                2
        );

        adapter.saveAll(List.of(commission1, commission2));

        assertEquals(2, repository.count());
    }
}
