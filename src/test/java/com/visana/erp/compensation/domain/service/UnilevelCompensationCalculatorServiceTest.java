package com.visana.erp.compensation.domain.service;

import com.visana.erp.commerce.domain.model.Order;
import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.commerce.domain.model.OrderItem;
import com.visana.erp.commerce.domain.model.OrderType;
import com.visana.erp.compensation.domain.model.Commission;
import com.visana.erp.compensation.domain.model.CommissionStatus;
import com.visana.erp.compensation.domain.model.CommissionType;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.Period;
import com.visana.erp.core.domain.model.Percentage;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import com.visana.erp.network.domain.model.GenealogyNode;
import com.visana.erp.network.domain.model.NetworkRole;
import com.visana.erp.network.domain.model.SponsorId;
import com.visana.erp.qualification.domain.model.AffiliateQualification;
import com.visana.erp.commerce.domain.model.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UnilevelCompensationCalculatorServiceTest {

    private UnilevelCompensationCalculatorService service;
    private TenantId tenantId;
    private Period period;

    // El plan configurable — simula lo que vendría de la BD en producción
    private Map<Integer, Percentage> buildStandardUnilevelPlan() {
        return Map.of(
                1, Percentage.of("0.15"),  // L1: 15%
                2, Percentage.of("0.10"),  // L2: 10%
                3, Percentage.of("0.05"),  // L3: 5%
                4, Percentage.of("0.04"),  // L4: 4%
                5, Percentage.of("0.03"),  // L5: 3%
                6, Percentage.of("0.02"),  // L6: 2%
                7, Percentage.of("0.01"),  // L7: 1%
                8, Percentage.of("0.02")   // L8: 2%
        );
    }

    @BeforeEach
    void setUp() {
        service = new UnilevelCompensationCalculatorService();
        tenantId = TenantId.generate();
        period = Period.of(YearMonth.of(2026, 6));
    }

    private Order buildPaidOrder(AffiliateId buyer, Money orderAmount) {
        Order order = new Order(tenantId, OrderId.generate(), buyer, OrderType.REPURCHASE);
        order.addItem(new OrderItem(ProductId.generate(), 1, orderAmount));
        order.confirmPayment();
        return order;
    }

    private GenealogyNode buildQualifiedNode(AffiliateId affiliateId, SponsorId sponsorId) {
        GenealogyNode node = GenealogyNode.create(tenantId, affiliateId, sponsorId, NetworkRole.AFFILIATE);
        node.activate();
        return node;
    }

    private AffiliateQualification buildQualification(AffiliateId affiliateId, boolean activated, boolean qualified) {
        return new AffiliateQualification(tenantId, affiliateId, period, activated, qualified, qualified ? 1 : 0);
    }

    // ─── HAPPY PATH ──────────────────────────────────────────────────────────────

    @Test
    void shouldCalculateCommissionsForAllQualifiedAncestors() {
        AffiliateId buyer = AffiliateId.generate();
        AffiliateId level1 = AffiliateId.generate();
        AffiliateId level2 = AffiliateId.generate();

        GenealogyNode node1 = buildQualifiedNode(level1, SponsorId.generate());
        GenealogyNode node2 = buildQualifiedNode(level2, SponsorId.of(level1.value()));

        Order order = buildPaidOrder(buyer, Money.of(new BigDecimal("1000.00")));

        Map<AffiliateId, AffiliateQualification> qualifications = Map.of(
                level1, buildQualification(level1, true, true),
                level2, buildQualification(level2, true, true)
        );

        List<Commission> commissions = service.calculateUnilevelCommissions(
                tenantId, order.getOrderId(), order.calculateTotal(), List.of(node1, node2), qualifications, buildStandardUnilevelPlan());

        assertEquals(2, commissions.size());

        Commission c1 = commissions.get(0);
        assertEquals(level1, c1.getBeneficiaryId());
        assertEquals(1, c1.getNetworkLevel());
        assertEquals(CommissionType.UNILEVEL_BONUS, c1.getType());
        assertEquals(CommissionStatus.CALCULATED, c1.getStatus());
        // 1000 * 15% = 150
        assertEquals(0, new BigDecimal("150.0000").compareTo(c1.getAmount().amount()));

        Commission c2 = commissions.get(1);
        assertEquals(level2, c2.getBeneficiaryId());
        assertEquals(2, c2.getNetworkLevel());
        // 1000 * 10% = 100
        assertEquals(0, new BigDecimal("100.0000").compareTo(c2.getAmount().amount()));
    }

    @Test
    void shouldSkipNonQualifiedAncestor() {
        AffiliateId buyer = AffiliateId.generate();
        AffiliateId level1Unqualified = AffiliateId.generate();
        AffiliateId level2Qualified = AffiliateId.generate();

        GenealogyNode node1 = buildQualifiedNode(level1Unqualified, SponsorId.generate());
        GenealogyNode node2 = buildQualifiedNode(level2Qualified, SponsorId.of(level1Unqualified.value()));

        Order order = buildPaidOrder(buyer, Money.of(new BigDecimal("1000.00")));

        // Nivel 1 NO calificado, nivel 2 SÍ calificado
        Map<AffiliateId, AffiliateQualification> qualifications = Map.of(
                level1Unqualified, buildQualification(level1Unqualified, true, false),
                level2Qualified, buildQualification(level2Qualified, true, true)
        );

        List<Commission> commissions = service.calculateUnilevelCommissions(
                tenantId, order.getOrderId(), order.calculateTotal(), List.of(node1, node2), qualifications, buildStandardUnilevelPlan());

        // Solo 1 comisión: nivel 1 fue saltado por no calificar
        assertEquals(1, commissions.size());
        assertEquals(level2Qualified, commissions.get(0).getBeneficiaryId());
        assertEquals(2, commissions.get(0).getNetworkLevel());
        // 1000 * 10% = 100
        assertEquals(0, new BigDecimal("100.0000").compareTo(commissions.get(0).getAmount().amount()));
    }

    @Test
    void shouldSkipAncestorWithoutQualificationRecord() {
        AffiliateId buyer = AffiliateId.generate();
        AffiliateId level1 = AffiliateId.generate();

        GenealogyNode node1 = buildQualifiedNode(level1, SponsorId.generate());

        Order order = buildPaidOrder(buyer, Money.of(new BigDecimal("500.00")));

        // Ninguna calificación registrada para level1
        List<Commission> commissions = service.calculateUnilevelCommissions(
                tenantId, order.getOrderId(), order.calculateTotal(), List.of(node1), Map.of(), buildStandardUnilevelPlan());

        assertTrue(commissions.isEmpty());
    }

    @Test
    void shouldNotExceedEightLevels() {
        AffiliateId buyer = AffiliateId.generate();
        List<GenealogyNode> deepUpline = new java.util.ArrayList<>();
        Map<AffiliateId, AffiliateQualification> qualifications = new java.util.HashMap<>();

        SponsorId prevSponsor = SponsorId.generate();
        for (int i = 0; i < 12; i++) {  // 12 nodos, solo deben pagarse 8
            AffiliateId id = AffiliateId.generate();
            deepUpline.add(buildQualifiedNode(id, prevSponsor));
            qualifications.put(id, buildQualification(id, true, true));
            prevSponsor = SponsorId.of(id.value());
        }

        Order order = buildPaidOrder(buyer, Money.of(new BigDecimal("1000.00")));

        List<Commission> commissions = service.calculateUnilevelCommissions(
                tenantId, order.getOrderId(), order.calculateTotal(), deepUpline, qualifications, buildStandardUnilevelPlan());

        // Máximo 8 niveles
        assertEquals(8, commissions.size());
    }

    @Test
    void shouldReturnEmptyWhenUplineIsEmpty() {
        AffiliateId buyer = AffiliateId.generate();
        Order order = buildPaidOrder(buyer, Money.of(new BigDecimal("1000.00")));

        List<Commission> commissions = service.calculateUnilevelCommissions(
                tenantId, order.getOrderId(), order.calculateTotal(), List.of(), Map.of(), buildStandardUnilevelPlan());

        assertTrue(commissions.isEmpty());
    }

    @Test
    void shouldSkipLevelWithNoConfiguredPercentage() {
        AffiliateId buyer = AffiliateId.generate();
        AffiliateId level1 = AffiliateId.generate();
        AffiliateId level2 = AffiliateId.generate();

        GenealogyNode node1 = buildQualifiedNode(level1, SponsorId.generate());
        GenealogyNode node2 = buildQualifiedNode(level2, SponsorId.of(level1.value()));

        Order order = buildPaidOrder(buyer, Money.of(new BigDecimal("1000.00")));

        Map<AffiliateId, AffiliateQualification> qualifications = Map.of(
                level1, buildQualification(level1, true, true),
                level2, buildQualification(level2, true, true)
        );

        // Plan incompleto: solo define nivel 1, omite nivel 2
        Map<Integer, Percentage> partialPlan = Map.of(1, Percentage.of("0.15"));

        List<Commission> commissions = service.calculateUnilevelCommissions(
                tenantId, order.getOrderId(), order.calculateTotal(), List.of(node1, node2), qualifications, partialPlan);

        assertEquals(1, commissions.size());
        assertEquals(level1, commissions.get(0).getBeneficiaryId());
    }

//    @Test
//    void shouldThrowExceptionWhenOrderIsNotPaid() {
//        AffiliateId buyer = AffiliateId.generate();
//        Order order = new Order(tenantId, OrderId.generate(), buyer, OrderType.RECOMPRA);
//        // Orden en estado PENDING
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
//                service.calculateUnilevelCommissions(tenantId, order.getOrderId(), order.calculateTotal(), List.of(), Map.of(), buildStandardUnilevelPlan())
//        );
//
//        assertTrue(exception.getMessage().contains("PAID"));
//    }

    // ─── REVERSE / LEDGER INMUTABLE ───────────────────────────────────────────

    @Test
    void shouldCreateReversalOfCommission() {
        AffiliateId buyer = AffiliateId.generate();
        AffiliateId level1 = AffiliateId.generate();

        GenealogyNode node1 = buildQualifiedNode(level1, SponsorId.generate());
        Order order = buildPaidOrder(buyer, Money.of(new BigDecimal("1000.00")));

        Map<AffiliateId, AffiliateQualification> qualifications = Map.of(
                level1, buildQualification(level1, true, true));

        List<Commission> commissions = service.calculateUnilevelCommissions(
                tenantId, order.getOrderId(), order.calculateTotal(), List.of(node1), qualifications, buildStandardUnilevelPlan());

        assertEquals(1, commissions.size());
        Commission original = commissions.get(0);

        Commission reversal = original.reverse();

        assertEquals(CommissionStatus.REVERSED, reversal.getStatus());
        assertTrue(reversal.getAmount().isReversal());
        assertEquals(0, original.getAmount().amount().negate().compareTo(reversal.getAmount().amount()));
        // El reverso es una nueva entidad (ID diferente, Ledger inmutable)
        assertNotEquals(original.getCommissionId(), reversal.getCommissionId());
        assertEquals(original.getBeneficiaryId(), reversal.getBeneficiaryId());
    }
}
