package com.visana.erp.commerce.catalog.application;

import com.visana.erp.commerce.catalog.domain.CatalogCategory;
import com.visana.erp.commerce.catalog.domain.CatalogProduct;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CatalogQueryService {
    private final CatalogProductPort products;
    private final CatalogCategoryPort categories;
    public CatalogQueryService(CatalogProductPort products, CatalogCategoryPort categories) { this.products = products; this.categories = categories; }
    public Page<CatalogProduct> activeProducts(Pageable pageable) { return products.findActive(pageable); }
    public CatalogProduct activeProduct(UUID productId) {
        return products.findById(productId)
                .filter(CatalogProduct::isActive)
                .orElseThrow(() -> new CatalogProductNotFoundException(productId));
    }
    public Page<CatalogCategory> activeCategories(Pageable pageable) { return categories.findActiveCategories(pageable); }
}
