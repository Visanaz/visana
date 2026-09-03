package com.visana.erp.commerce.order.infrastructure.persistence;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;
@Entity @Table(name="commerce_orders")
public class OwnedOrderJpaEntity {
 @Id private UUID id; @Column(name="owner_actor_id",nullable=false) @JdbcTypeCode(Types.VARCHAR) private UUID ownerActorId; @Column(nullable=false) private String status;
 @Column(nullable=false,precision=19,scale=4) private BigDecimal total; @Column(name="created_at",nullable=false) private Instant createdAt; @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @OneToMany(mappedBy="order",cascade=CascadeType.ALL,orphanRemoval=true) private List<OwnedOrderLineJpaEntity> lines=new ArrayList<>();
 protected OwnedOrderJpaEntity(){} public OwnedOrderJpaEntity(UUID id,UUID ownerActorId,String status,BigDecimal total,Instant createdAt,Instant updatedAt){this.id=id;this.ownerActorId=ownerActorId;this.status=status;this.total=total;this.createdAt=createdAt;this.updatedAt=updatedAt;}
 public void addLine(OwnedOrderLineJpaEntity line){lines.add(line);} public UUID getId(){return id;} public UUID getOwnerActorId(){return ownerActorId;} public String getStatus(){return status;} public BigDecimal getTotal(){return total;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;} public List<OwnedOrderLineJpaEntity> getLines(){return List.copyOf(lines);}
}
