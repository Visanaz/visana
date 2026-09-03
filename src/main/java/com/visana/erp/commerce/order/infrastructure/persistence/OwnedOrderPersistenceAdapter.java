package com.visana.erp.commerce.order.infrastructure.persistence;
import com.visana.erp.commerce.domain.model.OrderStatus;
import com.visana.erp.commerce.order.application.OwnedOrderPort;
import com.visana.erp.commerce.order.domain.*;
import com.visana.erp.core.domain.model.Money;
import java.util.*;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable; import org.springframework.stereotype.Component;
@Component public class OwnedOrderPersistenceAdapter implements OwnedOrderPort {
 private final OwnedOrderSpringDataRepository repository; public OwnedOrderPersistenceAdapter(OwnedOrderSpringDataRepository repository){this.repository=repository;}
 @Override public OwnedOrder save(OwnedOrder order){OwnedOrderJpaEntity entity=new OwnedOrderJpaEntity(order.id(),order.ownerActorId(),order.status().name(),order.total().amount(),order.createdAt(),order.updatedAt()); for(OwnedOrderLine line:order.lines()) entity.addLine(new OwnedOrderLineJpaEntity(line.id(),entity,line.productId(),line.productCodeSnapshot(),line.productNameSnapshot(),line.unitPriceSnapshot().amount(),line.quantity(),line.lineTotal().amount())); repository.save(entity);return order;}
 @Override public Optional<OwnedOrder> findById(UUID id){return repository.findById(id).map(this::map);} @Override public Page<OwnedOrder> findByOwnerActorId(UUID actor,Pageable pageable){return repository.findByOwnerActorId(actor,pageable).map(this::map);}
 private OwnedOrder map(OwnedOrderJpaEntity entity){List<OwnedOrderLine> lines=entity.getLines().stream().map(line->new OwnedOrderLine(line.getId(),line.getProductId(),line.getProductCodeSnapshot(),line.getProductNameSnapshot(),Money.of(line.getUnitPriceSnapshot()),line.getQuantity(),Money.of(line.getLineTotal()))).toList(); return new OwnedOrder(entity.getId(),entity.getOwnerActorId(),OrderStatus.valueOf(entity.getStatus()),Money.of(entity.getTotal()),lines,entity.getCreatedAt(),entity.getUpdatedAt());}
}
