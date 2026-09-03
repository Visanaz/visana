package com.visana.erp.commerce.order.application;

import com.visana.erp.commerce.catalog.application.CatalogProductPort;
import com.visana.erp.commerce.catalog.domain.*;
import com.visana.erp.commerce.order.domain.OwnedOrder;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.platform.application.audit.AuditEventWriter;
import com.visana.erp.platform.application.authorization.AuthorizationPolicy;
import com.visana.erp.platform.application.ownership.ResourceOwnershipService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnedOrderServiceTest {
 @Mock CatalogProductPort products; @Mock OwnedOrderPort orders; @Mock ResourceOwnershipService ownerships; @Mock AuthorizationPolicy authorization; @Mock AuditEventWriter audit;
 @InjectMocks OwnedOrderService service;
 @Test void pricesAndTotalsComeFromCatalogNotTheRequest() {UUID actor=UUID.randomUUID(), product=UUID.randomUUID();when(products.findById(product)).thenReturn(Optional.of(product(product,"90.0000"))); OwnedOrder created=service.create(actor,new CreateOwnedOrderCommand(List.of(new CreateOwnedOrderCommand.Line(product,3))));assertThat(created.total().amount()).isEqualByComparingTo("270.0000");assertThat(created.lines().getFirst().unitPriceSnapshot().amount()).isEqualByComparingTo("90.0000");verify(orders).save(created);verify(ownerships).assign(eq("ORDER"),eq(created.id()),any(),anyString());verify(audit).append(argThat(event->event.action().equals("ORDER_CREATED")));}
 @Test void inactiveProductCannotBeOrdered(){UUID product=UUID.randomUUID();when(products.findById(product)).thenReturn(Optional.of(new CatalogProduct(product,"SKU","CODE","inactive",null,Money.of("1"),CatalogProductStatus.INACTIVE,null,Instant.now(),Instant.now())));assertThatThrownBy(()->service.create(UUID.randomUUID(),new CreateOwnedOrderCommand(List.of(new CreateOwnedOrderCommand.Line(product,1))))).isInstanceOf(IllegalArgumentException.class);verifyNoInteractions(orders,ownerships,audit);}
 private CatalogProduct product(UUID id,String price){Instant now=Instant.now();return new CatalogProduct(id,"SKU-1","CODE-1","Synthetic",null,Money.of(price),CatalogProductStatus.ACTIVE,null,now,now);}
}
