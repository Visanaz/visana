package com.visana.erp.commerce.catalog.application;

import com.visana.erp.commerce.catalog.domain.CatalogProduct;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CatalogProductPort {
    Optional<CatalogProduct> findById(UUID id);
    Page<CatalogProduct> findActive(Pageable pageable);
    CatalogProduct save(CatalogProduct product);
}
