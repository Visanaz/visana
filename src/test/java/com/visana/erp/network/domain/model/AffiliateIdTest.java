package com.visana.erp.network.domain.model;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class AffiliateIdTest {

    @Test
    void shouldCreateAffiliateIdFromUUID() {
        UUID uuid = UUID.randomUUID();
        AffiliateId id = AffiliateId.of(uuid);
        assertEquals(uuid, id.value());
    }

    @Test
    void shouldCreateAffiliateIdFromString() {
        String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
        AffiliateId id = AffiliateId.of(uuidStr);
        assertEquals(UUID.fromString(uuidStr), id.value());
    }
    
    @Test
    void shouldGenerateRandomAffiliateId() {
        AffiliateId id1 = AffiliateId.generate();
        AffiliateId id2 = AffiliateId.generate();
        assertNotNull(id1.value());
        assertNotNull(id2.value());
        assertNotEquals(id1, id2);
    }

    @Test
    void shouldThrowExceptionOnInvalidUUIDFormat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            AffiliateId.of("invalid-uuid");
        });
        assertTrue(exception.getMessage().contains("Invalid UUID format"));
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        assertThrows(NullPointerException.class, () -> new AffiliateId(null));
    }
}
