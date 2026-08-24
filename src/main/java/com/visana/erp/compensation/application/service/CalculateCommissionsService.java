package com.visana.erp.compensation.application.service;

import com.visana.erp.commerce.application.dto.OrderPaidEvent;
import com.visana.erp.compensation.application.port.in.CalculateCommissionsUseCase;
import com.visana.erp.compensation.application.port.out.CommissionPlanProviderPort;
import com.visana.erp.compensation.application.port.out.CommissionRepository;
import com.visana.erp.compensation.application.port.out.GenealogyProviderPort;
import com.visana.erp.compensation.application.port.out.QualificationProviderPort;
import com.visana.erp.compensation.domain.model.Commission;
import com.visana.erp.compensation.domain.service.UnilevelCompensationCalculatorService;
import com.visana.erp.core.domain.model.Percentage;
import com.visana.erp.core.domain.model.Period;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import com.visana.erp.network.domain.model.GenealogyNode;
import com.visana.erp.qualification.domain.model.AffiliateQualification;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateCommissionsService implements CalculateCommissionsUseCase {

    private final GenealogyProviderPort genealogyProvider;
    private final QualificationProviderPort qualificationProvider;
    private final CommissionPlanProviderPort commissionPlanProvider;
    private final CommissionRepository commissionRepository;
    private final UnilevelCompensationCalculatorService calculatorService;

    public CalculateCommissionsService(
            GenealogyProviderPort genealogyProvider,
            QualificationProviderPort qualificationProvider,
            CommissionPlanProviderPort commissionPlanProvider,
            CommissionRepository commissionRepository,
            UnilevelCompensationCalculatorService calculatorService) {
        
        this.genealogyProvider = genealogyProvider;
        this.qualificationProvider = qualificationProvider;
        this.commissionPlanProvider = commissionPlanProvider;
        this.commissionRepository = commissionRepository;
        this.calculatorService = calculatorService;
    }

    @Override
    public void execute(OrderPaidEvent event) {
        Map<Integer, Percentage> unilevelPlan = commissionPlanProvider.getUnilevelPlan();
        
        List<GenealogyNode> upline = genealogyProvider.getUpline(event.affiliateId(), 8);
        
        Period currentPeriod = Period.of(YearMonth.now());
        
        Map<AffiliateId, AffiliateQualification> qualifications = new HashMap<>();
        TenantId activeTenantId = event.tenantId();
        
        for (GenealogyNode node : upline) {
            AffiliateQualification qualification = qualificationProvider.getQualification(node.getAffiliateId(), currentPeriod);
            if (qualification != null) {
                qualifications.put(node.getAffiliateId(), qualification);
            }
        }
        
        if (activeTenantId == null) {
            activeTenantId = TenantId.generate(); 
        }
        
        List<Commission> commissions = calculatorService.calculateUnilevelCommissions(
                activeTenantId,
                event.orderId(),
                event.total(),
                upline,
                qualifications,
                unilevelPlan
        );
        
        if (!commissions.isEmpty()) {
            commissionRepository.saveAll(commissions);
        }
    }
}
