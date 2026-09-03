package com.visana.erp.commerce.catalog.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "catalog_products")
public class CatalogProductJpaEntity {
    @Id private UUID id;
    @Column(nullable = false, unique = true) private String sku;
    @Column(nullable = false, unique = true) private String code;
    @Column(nullable = false) private String name;
    private String description;
    @Column(name = "base_price", nullable = false, precision = 19, scale = 4) private BigDecimal basePrice;
    @Column(nullable = false) private String status;
    @Column(name = "category_id") private UUID categoryId;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected CatalogProductJpaEntity() { }
    public CatalogProductJpaEntity(UUID id, String sku, String code, String name, String description, BigDecimal basePrice, String status, UUID categoryId, Instant createdAt, Instant updatedAt) {
        this.id=id; this.sku=sku; this.code=code; this.name=name; this.description=description; this.basePrice=basePrice; this.status=status; this.categoryId=categoryId; this.createdAt=createdAt; this.updatedAt=updatedAt;
    }
    public UUID getId(){return id;} public String getSku(){return sku;} public String getCode(){return code;} public String getName(){return name;} public String getDescription(){return description;} public BigDecimal getBasePrice(){return basePrice;} public String getStatus(){return status;} public UUID getCategoryId(){return categoryId;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
