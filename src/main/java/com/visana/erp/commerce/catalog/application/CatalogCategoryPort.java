package com.visana.erp.commerce.catalog.application;

import com.visana.erp.commerce.catalog.domain.CatalogCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CatalogCategoryPort {
    Page<CatalogCategory> findActiveCategories(Pageable pageable);
    CatalogCategory save(CatalogCategory category);
}
