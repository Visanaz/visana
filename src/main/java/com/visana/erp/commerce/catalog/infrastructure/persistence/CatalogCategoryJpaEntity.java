package com.visana.erp.commerce.catalog.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product_categories")
public class CatalogCategoryJpaEntity {
    @Id private UUID id;
    @Column(nullable = false, unique = true) private String name;
    @Column(nullable = false) private String status;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected CatalogCategoryJpaEntity() { }
    public CatalogCategoryJpaEntity(UUID id, String name, String status, Instant createdAt, Instant updatedAt) { this.id=id; this.name=name; this.status=status; this.createdAt=createdAt; this.updatedAt=updatedAt; }
    public UUID getId(){return id;} public String getName(){return name;} public String getStatus(){return status;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
