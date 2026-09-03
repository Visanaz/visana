package com.visana.erp.commerce.order.infrastructure.persistence;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
@Entity @Table(name="commerce_order_lines")
public class OwnedOrderLineJpaEntity {
 @Id private UUID id; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="order_id",nullable=false) private OwnedOrderJpaEntity order;
 @Column(name="product_id",nullable=false) private UUID productId; @Column(name="product_code_snapshot",nullable=false) private String productCodeSnapshot; @Column(name="product_name_snapshot",nullable=false) private String productNameSnapshot;
 @Column(name="unit_price_snapshot",nullable=false,precision=19,scale=4) private BigDecimal unitPriceSnapshot; @Column(nullable=false) private int quantity; @Column(name="line_total",nullable=false,precision=19,scale=4) private BigDecimal lineTotal;
 protected OwnedOrderLineJpaEntity(){} public OwnedOrderLineJpaEntity(UUID id,OwnedOrderJpaEntity order,UUID productId,String code,String name,BigDecimal price,int quantity,BigDecimal total){this.id=id;this.order=order;this.productId=productId;this.productCodeSnapshot=code;this.productNameSnapshot=name;this.unitPriceSnapshot=price;this.quantity=quantity;this.lineTotal=total;}
 public UUID getId(){return id;} public UUID getProductId(){return productId;} public String getProductCodeSnapshot(){return productCodeSnapshot;} public String getProductNameSnapshot(){return productNameSnapshot;} public BigDecimal getUnitPriceSnapshot(){return unitPriceSnapshot;} public int getQuantity(){return quantity;} public BigDecimal getLineTotal(){return lineTotal;}
}
