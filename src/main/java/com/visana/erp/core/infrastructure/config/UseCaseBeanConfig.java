package com.visana.erp.core.infrastructure.config;

import com.visana.erp.commerce.application.port.in.ConfirmOrderPaymentUseCase;
import com.visana.erp.commerce.application.port.out.DomainEventPublisher;
import com.visana.erp.commerce.application.port.out.OrderRepository;
import com.visana.erp.commerce.application.service.ConfirmOrderPaymentService;
import com.visana.erp.compensation.application.port.in.CalculateCommissionsUseCase;
import com.visana.erp.compensation.application.port.out.CommissionPlanProviderPort;
import com.visana.erp.compensation.application.port.out.CommissionRepository;
import com.visana.erp.compensation.application.port.out.GenealogyProviderPort;
import com.visana.erp.compensation.application.port.out.QualificationProviderPort;
import com.visana.erp.compensation.application.service.CalculateCommissionsService;
import com.visana.erp.compensation.domain.service.UnilevelCompensationCalculatorService;
import com.visana.erp.ledger.domain.service.SettlementService;
import com.visana.erp.migration.application.service.LegacyDataMigrationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseBeanConfig {

    @Bean
    public ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase(
            OrderRepository orderRepository, 
            DomainEventPublisher eventPublisher) {
        return new ConfirmOrderPaymentService(orderRepository, eventPublisher);
    }

    @Bean
    public UnilevelCompensationCalculatorService unilevelCompensationCalculatorService() {
        return new UnilevelCompensationCalculatorService();
    }

    @Bean
    public CalculateCommissionsUseCase calculateCommissionsUseCase(
            GenealogyProviderPort genealogyProvider,
            QualificationProviderPort qualificationProvider,
            CommissionPlanProviderPort commissionPlanProvider,
            CommissionRepository commissionRepository,
            UnilevelCompensationCalculatorService calculatorService) {
        return new CalculateCommissionsService(
                genealogyProvider, 
                qualificationProvider, 
                commissionPlanProvider, 
                commissionRepository, 
                calculatorService);
    }

    @Bean
    public SettlementService settlementService() {
        return new SettlementService();
    }

    @Bean
    public LegacyDataMigrationService legacyDataMigrationService(OrderRepository orderRepository) {
        return new LegacyDataMigrationService(orderRepository);
    }
}
