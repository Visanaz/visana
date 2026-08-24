package com.visana.erp.migration.application.service;

import com.visana.erp.commerce.application.port.out.OrderRepository;
import com.visana.erp.commerce.domain.model.Order;
import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.commerce.domain.model.OrderType;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.migration.application.dto.LegacyOrderDto;
import com.visana.erp.migration.application.dto.LegacyUserDto;
import com.visana.erp.network.domain.model.AffiliateId;
import com.visana.erp.network.domain.model.GenealogyNode;
import com.visana.erp.network.domain.model.NetworkRole;
import com.visana.erp.network.domain.model.SponsorId;

import java.util.ArrayList;
import java.util.List;

public class LegacyDataMigrationService {

    private final OrderRepository orderRepository;
    // Simulating other repositories...

    private final List<String> conciliationReport = new ArrayList<>();

    public LegacyDataMigrationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void migrateUser(LegacyUserDto userDto) {
        try {
            TenantId tenantId = TenantId.of(userDto.empresaId());
            AffiliateId affiliateId = AffiliateId.of(userDto.id());
            NetworkRole role = NetworkRole.valueOf(userDto.role());
            
            GenealogyNode node;
            if (userDto.sponsorId() != null) {
                SponsorId sponsorId = SponsorId.of(userDto.sponsorId());
                node = GenealogyNode.create(tenantId, affiliateId, sponsorId, role);
            } else {
                node = GenealogyNode.createRoot(tenantId, affiliateId, role);
            }
            
            // Assume we save it to GenealogyRepository
            // genealogyRepository.save(node);
        } catch (Exception e) {
            conciliationReport.add("Error migrating user " + userDto.id() + ": " + e.getMessage());
        }
    }

    public void migrateOrder(LegacyOrderDto orderDto) {
        try {
            TenantId tenantId = TenantId.of(orderDto.empresaId());
            OrderId orderId = OrderId.of(orderDto.id());
            AffiliateId affiliateId = AffiliateId.of(orderDto.affiliateId());
            OrderType type = OrderType.valueOf(orderDto.orderType());
            
            Order order = new Order(tenantId, orderId, affiliateId, type);
            // Ignore items, just confirm status
            if ("PAID".equals(orderDto.status())) {
                // If it was paid, we would validate it. Since we enforce valid amount, it might fail if 0.
                order.confirmPayment();
            }
            orderRepository.save(order);
        } catch (Exception e) {
            conciliationReport.add("Error migrating order " + orderDto.id() + ": " + e.getMessage());
        }
    }

    public List<String> getConciliationReport() {
        return conciliationReport;
    }
}
