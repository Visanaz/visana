package com.visana.erp.core.infrastructure.adapter.in.web.filter;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void shouldPreserveSafeClientCorrelationIdAndClearMdc() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CorrelationIdFilter.HEADER_NAME, "order-123_ABC");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) ->
                assertEquals("order-123_ABC", MDC.get(CorrelationIdFilter.MDC_KEY)));

        assertEquals("order-123_ABC", response.getHeader(CorrelationIdFilter.HEADER_NAME));
        assertFalse(MDC.getCopyOfContextMap() != null && MDC.getCopyOfContextMap().containsKey(CorrelationIdFilter.MDC_KEY));
    }

    @Test
    void shouldGenerateCorrelationIdWhenHeaderIsUnsafe() {
        String resolved = CorrelationIdFilter.resolveCorrelationId("unsafe\r\nvalue");

        assertNotNull(resolved);
        assertFalse(resolved.contains("\r"));
        assertFalse(resolved.contains("\n"));
    }
}
