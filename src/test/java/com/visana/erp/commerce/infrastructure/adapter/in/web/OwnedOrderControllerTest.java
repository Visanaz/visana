package com.visana.erp.commerce.infrastructure.adapter.in.web;

import com.visana.erp.commerce.domain.model.OrderStatus;
import com.visana.erp.commerce.order.application.OwnedOrderService;
import com.visana.erp.commerce.order.domain.OwnedOrder;
import com.visana.erp.commerce.order.domain.OwnedOrderLine;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.infrastructure.config.security.KeycloakAuthenticatedPrincipalAdapter;
import com.visana.erp.platform.application.identity.*;
import com.visana.erp.platform.application.ownership.OwnershipDeniedException;
import com.visana.erp.platform.domain.identity.PlatformActorId;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnedOrderController.class)
class OwnedOrderControllerTest {
 @Autowired MockMvc mvc; @MockBean OwnedOrderService orders; @MockBean KeycloakAuthenticatedPrincipalAdapter principalAdapter; @MockBean ActorResolverPort actors;
 UUID actorId=UUID.randomUUID();
 @BeforeEach void identity(){when(principalAdapter.from(any(Jwt.class),anySet())).thenReturn(new AuthenticatedPrincipal("OIDC","https://issuer.test","subject",java.util.Set.of()));when(actors.resolve(any())).thenReturn(ActorResolution.linked(PlatformActorId.of(actorId)));}
 @Test void unauthenticatedCreateIsDenied() throws Exception {
  mvc.perform(post("/api/v1/orders")
    .contentType("application/json")
    .content("{\"lines\":[{\"productId\":\"11111111-1111-1111-1111-111111111111\",\"quantity\":1}]}"))
   .andExpect(status().isForbidden());
 }
 @Test void unlinkedIdentityCannotCreate() throws Exception {when(actors.resolve(any())).thenReturn(ActorResolution.unlinked());mvc.perform(post("/api/v1/orders").contentType("application/json").content("{\"lines\":[{\"productId\":\"11111111-1111-1111-1111-111111111111\",\"quantity\":1}]}").with(jwt())).andExpect(status().isForbidden());}
 @Test void linkedActorCanCreateAndServerReturnsOwnedOrder() throws Exception {OwnedOrder order=order();when(orders.create(eq(actorId),any())).thenReturn(order);mvc.perform(post("/api/v1/orders").contentType("application/json").content("{\"lines\":[{\"productId\":\"11111111-1111-1111-1111-111111111111\",\"quantity\":2}]}").with(jwt())).andExpect(status().isCreated()).andExpect(jsonPath("$.ownerActorId").value(actorId.toString())).andExpect(jsonPath("$.lines[0].unitPrice").value(100));}
 @Test void foreignOrderReadIsDenied() throws Exception {UUID order=UUID.randomUUID();when(orders.read(eq(actorId),eq(order))).thenThrow(new OwnershipDeniedException());mvc.perform(get("/api/v1/orders/{id}",order).with(jwt())).andExpect(status().isForbidden());}
 private OwnedOrder order(){Instant now=Instant.parse("2026-09-03T00:00:00Z");OwnedOrderLine line=new OwnedOrderLine(UUID.randomUUID(),UUID.fromString("11111111-1111-1111-1111-111111111111"),"P-001","Synthetic",Money.of("100"),2,Money.of("200"));return new OwnedOrder(UUID.randomUUID(),actorId,OrderStatus.PENDING,Money.of("200"),List.of(line),now,now);}
}
