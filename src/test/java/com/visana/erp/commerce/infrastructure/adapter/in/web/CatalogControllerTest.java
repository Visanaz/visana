package com.visana.erp.commerce.infrastructure.adapter.in.web;

import com.visana.erp.commerce.catalog.application.CatalogProductNotFoundException;
import com.visana.erp.commerce.catalog.application.CatalogQueryService;
import com.visana.erp.commerce.catalog.domain.CatalogProduct;
import com.visana.erp.commerce.catalog.domain.CatalogProductStatus;
import com.visana.erp.core.domain.model.Money;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
class CatalogControllerTest {
    @Autowired MockMvc mvc;
    @MockBean CatalogQueryService catalog;

    @Test void authenticatedCallerGetsExistingActiveProductDto() throws Exception {
        UUID id = UUID.randomUUID();
        when(catalog.activeProduct(id)).thenReturn(product(id));

        mvc.perform(get("/api/v1/products/{id}", id).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(id.toString()))
                .andExpect(jsonPath("$.sku").value("SKU-1"))
                .andExpect(jsonPath("$.basePrice").value(15000))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test void unknownProductReturnsProblemDetailNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(catalog.activeProduct(id)).thenThrow(new CatalogProductNotFoundException(id));

        mvc.perform(get("/api/v1/products/{id}", id).with(jwt()).header("X-Correlation-ID", "catalog-test"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.correlationId").value("catalog-test"));
    }

    @Test void unauthenticatedCallerIsRejected() throws Exception {
        mvc.perform(get("/api/v1/products/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    private CatalogProduct product(UUID id) {
        Instant now = Instant.parse("2026-09-04T00:00:00Z");
        return new CatalogProduct(id, "SKU-1", "CODE-1", "Synthetic", "Public description", Money.of("15000"),
                CatalogProductStatus.ACTIVE, null, now, now);
    }
}
