package com.visana.erp.commerce.order.application;

import com.visana.erp.commerce.catalog.application.CatalogProductPort;
import com.visana.erp.commerce.catalog.domain.CatalogProduct;
import com.visana.erp.commerce.domain.model.OrderStatus;
import com.visana.erp.commerce.order.domain.*;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.platform.application.audit.AuditEventWriter;
import com.visana.erp.platform.application.authorization.AuthorizationPolicy;
import com.visana.erp.platform.application.ownership.OwnershipDeniedException;
import com.visana.erp.platform.application.ownership.ResourceOwnershipService;
import com.visana.erp.platform.domain.audit.AuditEvent;
import com.visana.erp.platform.domain.identity.PlatformActorId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.visana.erp.core.infrastructure.adapter.in.web.filter.CorrelationIdFilter.MDC_KEY;

@Service
public class OwnedOrderService {
    private final CatalogProductPort products; private final OwnedOrderPort orders; private final ResourceOwnershipService ownerships;
    private final AuthorizationPolicy authorization; private final AuditEventWriter audit;
    public OwnedOrderService(CatalogProductPort products, OwnedOrderPort orders, ResourceOwnershipService ownerships, AuthorizationPolicy authorization, AuditEventWriter audit) { this.products=products; this.orders=orders; this.ownerships=ownerships; this.authorization=authorization; this.audit=audit; }
    @Transactional public OwnedOrder create(UUID actorId, CreateOwnedOrderCommand command) {
        if (command == null || command.lines() == null || command.lines().isEmpty()) throw new IllegalArgumentException("order lines are required");
        List<OwnedOrderLine> lines = new ArrayList<>(); Money total = Money.zero();
        for (CreateOwnedOrderCommand.Line requested : command.lines()) {
            if (requested == null || requested.productId() == null || requested.quantity() <= 0) throw new IllegalArgumentException("each order line requires a productId and positive quantity");
            CatalogProduct product = products.findById(requested.productId()).filter(CatalogProduct::isActive)
                    .orElseThrow(() -> new IllegalArgumentException("requested product is not available"));
            Money lineTotal = product.basePrice().multiply(requested.quantity()); total = total.add(lineTotal);
            lines.add(new OwnedOrderLine(UUID.randomUUID(), product.id(), product.code(), product.name(), product.basePrice(), requested.quantity(), lineTotal));
        }
        Instant now = Instant.now(); OwnedOrder order = new OwnedOrder(UUID.randomUUID(), actorId, OrderStatus.PENDING, total, lines, now, now);
        orders.save(order); ownerships.assign("ORDER", order.id(), PlatformActorId.of(actorId), correlationId());
        audit.append(new AuditEvent(actorId.toString(), "ORDER_CREATED", "ORDER", order.id().toString(), now, correlationId(), Map.of("lineCount", String.valueOf(lines.size()))));
        return order;
    }
    @Transactional(readOnly = true) public OwnedOrder read(UUID actorId, UUID orderId) {
        OwnedOrder order = orders.findById(orderId).orElseThrow(OwnedOrderNotFoundException::new);
        if (!authorization.canReadOrder(actorId, orderId)) {
            audit.append(new AuditEvent(actorId.toString(), "ORDER_READ_DENIED", "ORDER", orderId.toString(), Instant.now(), correlationId(), Map.of()));
            throw new OwnershipDeniedException();
        }
        return order;
    }
    @Transactional(readOnly = true) public Page<OwnedOrder> list(UUID actorId, Pageable pageable) { return orders.findByOwnerActorId(actorId, pageable); }
    private String correlationId() { String value=MDC.get(MDC_KEY); return value == null ? "system" : value; }
}
