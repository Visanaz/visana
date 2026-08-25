package com.visana.erp.compensation.application.service;

import com.visana.erp.commerce.application.dto.OrderPaidEvent;
import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.compensation.application.port.out.CommissionPlanProviderPort;
import com.visana.erp.compensation.application.port.out.CommissionRepository;
import com.visana.erp.compensation.application.port.out.GenealogyProviderPort;
import com.visana.erp.compensation.application.port.out.QualificationProviderPort;
import com.visana.erp.compensation.domain.model.Commission;
import com.visana.erp.compensation.domain.service.UnilevelCompensationCalculatorService;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.Percentage;
import com.visana.erp.core.domain.model.Period;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import com.visana.erp.network.domain.model.GenealogyNode;
import com.visana.erp.network.domain.model.NetworkRole;
import com.visana.erp.network.domain.model.SponsorId;
import com.visana.erp.qualification.domain.model.AffiliateQualification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateCommissionsServiceTest {

    @Mock
    private GenealogyProviderPort genealogyProvider;

    @Mock
    private QualificationProviderPort qualificationProvider;

    @Mock
    private CommissionPlanProviderPort commissionPlanProvider;

    @Mock
    private CommissionRepository commissionRepository;

    private UnilevelCompensationCalculatorService calculatorService;
    private CalculateCommissionsService service;

    private TenantId tenantId;
    private OrderId orderId;
    private AffiliateId buyerId;
    private Money total;

    @BeforeEach
    void setUp() {
        calculatorService = new UnilevelCompensationCalculatorService(); // Use real domain service
        service = new CalculateCommissionsService(
                genealogyProvider, qualificationProvider, commissionPlanProvider, commissionRepository, calculatorService);

        tenantId = TenantId.generate();
        orderId = OrderId.generate();
        buyerId = AffiliateId.generate();
        total = Money.of(new BigDecimal("1000.00"));
    }

    @Test
    void shouldCalculateAndSaveCommissions() {
        OrderPaidEvent event = new OrderPaidEvent(tenantId, orderId, buyerId, total);

        Map<Integer, Percentage> plan = Map.of(1, Percentage.of("0.10"));
        when(commissionPlanProvider.getUnilevelPlan()).thenReturn(plan);

        AffiliateId sponsorId = AffiliateId.generate();
        GenealogyNode sponsorNode = GenealogyNode.createRoot(tenantId, sponsorId, NetworkRole.AFFILIATE);
        when(genealogyProvider.getUpline(buyerId, 8)).thenReturn(List.of(sponsorNode));

        AffiliateQualification sponsorQualification = new AffiliateQualification(tenantId, sponsorId, Period.of(YearMonth.now()), true, true, 1);
        when(qualificationProvider.getQualification(eq(sponsorId), any(Period.class))).thenReturn(sponsorQualification);

        service.execute(event);

        ArgumentCaptor<List<Commission>> captor = ArgumentCaptor.forClass(List.class);
        verify(commissionRepository).saveAll(captor.capture());

        List<Commission> savedCommissions = captor.getValue();
        assertEquals(1, savedCommissions.size());
        
        Commission commission = savedCommissions.get(0);
        assertEquals(sponsorId, commission.getBeneficiaryId());
        assertEquals(0, new BigDecimal("100.0000").compareTo(commission.getAmount().amount()));
    }

    @Test
    void shouldNotSaveCommissionsIfUplineIsEmpty() {
        OrderPaidEvent event = new OrderPaidEvent(tenantId, orderId, buyerId, total);

        when(commissionPlanProvider.getUnilevelPlan()).thenReturn(Map.of(1, Percentage.of("0.10")));
        when(genealogyProvider.getUpline(buyerId, 8)).thenReturn(List.of());

        service.execute(event);

        verify(commissionRepository, never()).saveAll(any());
    }
}
