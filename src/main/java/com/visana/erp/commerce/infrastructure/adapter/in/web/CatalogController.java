package com.visana.erp.commerce.infrastructure.adapter.in.web;

import com.visana.erp.commerce.catalog.application.CatalogQueryService;
import com.visana.erp.commerce.catalog.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Read-only until an evidence-backed catalog administration role matrix exists. */
@RestController @RequestMapping("/api/v1") @Tag(name="Catalog", description="Authenticated Plan 3 catalog queries")
public class CatalogController {
    private final CatalogQueryService catalog; public CatalogController(CatalogQueryService catalog){this.catalog=catalog;}
    @GetMapping("/products") @Operation(operationId="listCatalogProducts", summary="List active catalog products")
    public Page<ProductResponse> products(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,@RequestParam(defaultValue="name") String sort) { return catalog.activeProducts(pageable(page,size,sort)).map(this::product); }
    @GetMapping("/products/{id}") @Operation(operationId="getProductById", summary="Get an active catalog product by ID")
    public ProductResponse productById(@PathVariable UUID id) { return product(catalog.activeProduct(id)); }
    @GetMapping("/categories") @Operation(operationId="listCatalogCategories", summary="List active catalog categories")
    public Page<CategoryResponse> categories(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,@RequestParam(defaultValue="name") String sort) { return catalog.activeCategories(categoryPageable(page,size,sort)).map(this::category); }
    private Pageable pageable(int page,int size,String sort){ if(page<0||size<1||size>100) throw new IllegalArgumentException("page must be non-negative and size must be between 1 and 100"); if(!sort.equals("name")&&!sort.equals("sku")&&!sort.equals("createdAt")) throw new IllegalArgumentException("unsupported catalog sort"); String field=sort.equals("createdAt")?"createdAt":sort; return PageRequest.of(page,size,Sort.by(field).ascending()); }
    private Pageable categoryPageable(int page,int size,String sort){ if(page<0||size<1||size>100) throw new IllegalArgumentException("page must be non-negative and size must be between 1 and 100"); if(!sort.equals("name")&&!sort.equals("createdAt")) throw new IllegalArgumentException("unsupported category sort"); return PageRequest.of(page,size,Sort.by(sort).ascending()); }
    private ProductResponse product(CatalogProduct p){return new ProductResponse(p.id(),p.sku(),p.code(),p.name(),p.description(),p.basePrice().amount(),p.status().name(),p.categoryId());}
    private CategoryResponse category(CatalogCategory c){return new CategoryResponse(c.id(),c.name(),c.status().name());}
    public record ProductResponse(UUID productId,String sku,String code,String name,String description,BigDecimal basePrice,String status,UUID categoryId){}
    public record CategoryResponse(UUID categoryId,String name,String status){}
}
