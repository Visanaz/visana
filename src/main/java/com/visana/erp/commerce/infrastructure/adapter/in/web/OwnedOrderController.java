package com.visana.erp.commerce.infrastructure.adapter.in.web;

import com.visana.erp.commerce.order.application.*;
import com.visana.erp.commerce.order.domain.*;
import com.visana.erp.core.infrastructure.config.security.KeycloakAuthenticatedPrincipalAdapter;
import com.visana.erp.platform.application.identity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/orders") @Tag(name="Plan 3 Orders", description="Actor-owned commerce orders; not payment confirmation")
public class OwnedOrderController {
 private final OwnedOrderService orders; private final KeycloakAuthenticatedPrincipalAdapter principalAdapter; private final ActorResolverPort actors;
 public OwnedOrderController(OwnedOrderService orders,KeycloakAuthenticatedPrincipalAdapter principalAdapter,ActorResolverPort actors){this.orders=orders;this.principalAdapter=principalAdapter;this.actors=actors;}
 @PostMapping @Operation(operationId="createOwnedOrder",summary="Create an actor-owned order",description="Only productId and quantity are accepted. The server snapshots catalog prices; this endpoint does not confirm payment.")
 public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request,@AuthenticationPrincipal Jwt jwt){ OwnedOrder order=orders.create(actor(jwt),new CreateOwnedOrderCommand(request.lines().stream().map(line->new CreateOwnedOrderCommand.Line(line.productId(),line.quantity())).toList())); return ResponseEntity.status(HttpStatus.CREATED).body(response(order)); }
 @GetMapping("/{orderId}") @Operation(operationId="getOwnedOrder",summary="Read an owned order") public OrderResponse read(@PathVariable UUID orderId,@AuthenticationPrincipal Jwt jwt){return response(orders.read(actor(jwt),orderId));}
 @GetMapping @Operation(operationId="listOwnedOrders",summary="List orders owned by the current actor") public Page<OrderResponse> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,@RequestParam(defaultValue="createdAt") String sort,@AuthenticationPrincipal Jwt jwt){if(page<0||size<1||size>100)throw new IllegalArgumentException("page must be non-negative and size must be between 1 and 100");if(!sort.equals("createdAt")&&!sort.equals("status"))throw new IllegalArgumentException("unsupported order sort");return orders.list(actor(jwt),PageRequest.of(page,size,Sort.by(sort).descending())).map(this::response);}
 private UUID actor(Jwt jwt){ActorResolution resolved=actors.resolve(principalAdapter.from(jwt,java.util.Set.of()));if(!resolved.isLinked())throw new UnlinkedIdentityException();return resolved.actorId().value();}
 private OrderResponse response(OwnedOrder o){return new OrderResponse(o.id(),o.ownerActorId(),o.status().name(),o.total().amount(),o.lines().stream().map(line->new OrderLineResponse(line.productId(),line.productCodeSnapshot(),line.productNameSnapshot(),line.unitPriceSnapshot().amount(),line.quantity(),line.lineTotal().amount())).toList(),o.createdAt(),o.updatedAt());}
 public record CreateOrderRequest(List<CreateOrderLineRequest> lines){} public record CreateOrderLineRequest(UUID productId,int quantity){} public record OrderResponse(UUID orderId,UUID ownerActorId,String status,BigDecimal total,List<OrderLineResponse> lines,java.time.Instant createdAt,java.time.Instant updatedAt){} public record OrderLineResponse(UUID productId,String productCode,String productName,BigDecimal unitPrice,int quantity,BigDecimal lineTotal){}
}
