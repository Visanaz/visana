package com.visana.erp.network.domain.model;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class SponsorIdTest {

    @Test
    void shouldCreateSponsorIdFromUUID() {
        UUID uuid = UUID.randomUUID();
        SponsorId id = SponsorId.of(uuid);
        assertEquals(uuid, id.value());
    }

    @Test
    void shouldCreateSponsorIdFromString() {
        String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
        SponsorId id = SponsorId.of(uuidStr);
        assertEquals(UUID.fromString(uuidStr), id.value());
    }
    
    @Test
    void shouldGenerateRandomSponsorId() {
        SponsorId id1 = SponsorId.generate();
        SponsorId id2 = SponsorId.generate();
        assertNotNull(id1.value());
        assertNotNull(id2.value());
        assertNotEquals(id1, id2);
    }

    @Test
    void shouldThrowExceptionOnInvalidUUIDFormat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            SponsorId.of("invalid-uuid");
        });
        assertTrue(exception.getMessage().contains("Invalid UUID format"));
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        assertThrows(NullPointerException.class, () -> new SponsorId(null));
    }
}
