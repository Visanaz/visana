package com.visana.erp.commerce.infrastructure.adapter.out.persistence;

import com.visana.erp.commerce.domain.model.Order;
import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.commerce.domain.model.OrderItem;
import com.visana.erp.commerce.domain.model.OrderType;
import com.visana.erp.commerce.domain.model.ProductId;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({OrderPersistenceAdapter.class, OrderMapper.class})
class OrderPersistenceAdapterTest {

    @Autowired
    private OrderPersistenceAdapter adapter;

    private TenantId tenantId;
    private OrderId orderId;
    private AffiliateId affiliateId;
    private ProductId productId;

    @BeforeEach
    void setUp() {
        tenantId = TenantId.generate();
        orderId = OrderId.generate();
        affiliateId = AffiliateId.generate();
        productId = ProductId.generate();
    }

    @Test
    void shouldSaveAndFindOrder() {
        Order order = new Order(tenantId, orderId, affiliateId, OrderType.AFILIACION);
        order.addItem(new OrderItem(productId, 2, Money.of(new BigDecimal("50.00"))));

        adapter.save(order);

        Optional<Order> found = adapter.findById(orderId);
        assertTrue(found.isPresent());
        
        Order retrievedOrder = found.get();
        assertEquals(orderId, retrievedOrder.getOrderId());
        assertEquals(tenantId, retrievedOrder.getTenantId());
        assertEquals(affiliateId, retrievedOrder.getAffiliateId());
        assertEquals(1, retrievedOrder.getItems().size());
        assertEquals(0, new BigDecimal("100.0000").compareTo(retrievedOrder.calculateTotal().amount()));
    }
}
