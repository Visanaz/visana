package com.visana.erp.core.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TenantIdTest {

    @Test
    void shouldCreateTenantIdFromUUID() {
        UUID uuid = UUID.randomUUID();
        TenantId tenantId = TenantId.of(uuid);
        assertEquals(uuid, tenantId.value());
    }

    @Test
    void shouldCreateTenantIdFromString() {
        String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
        TenantId tenantId = TenantId.of(uuidStr);
        assertEquals(UUID.fromString(uuidStr), tenantId.value());
    }
    
    @Test
    void shouldGenerateRandomTenantId() {
        TenantId tenantId1 = TenantId.generate();
        TenantId tenantId2 = TenantId.generate();
        
        assertNotNull(tenantId1.value());
        assertNotNull(tenantId2.value());
        assertNotEquals(tenantId1, tenantId2);
    }

    @Test
    void shouldThrowExceptionOnInvalidUUIDFormat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            TenantId.of("invalid-uuid");
        });
        assertTrue(exception.getMessage().contains("Invalid UUID format"));
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        assertThrows(NullPointerException.class, () -> new TenantId(null));
    }
}
