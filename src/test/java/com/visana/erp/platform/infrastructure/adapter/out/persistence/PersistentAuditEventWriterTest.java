package com.visana.erp.platform.infrastructure.adapter.out.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visana.erp.platform.domain.audit.AuditEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PersistentAuditEventWriterTest {

    @Test
    void removesSensitiveMetadataBeforePersisting() {
        AuditEventSpringDataRepository repository = mock(AuditEventSpringDataRepository.class);
        PersistentAuditEventWriter writer = new PersistentAuditEventWriter(repository, new ObjectMapper());

        writer.append(new AuditEvent("actor", "ACCESS_DENIED", "ORDER", "order", Instant.now(), "correlation",
                Map.of("result", "denied", "access_token", "must-not-persist", "password", "must-not-persist")));

        ArgumentCaptor<AuditEventJpaEntity> captor = ArgumentCaptor.forClass(AuditEventJpaEntity.class);
        verify(repository).save(captor.capture());
        assertTrue(captor.getValue().metadata().contains("result"));
        assertFalse(captor.getValue().metadata().contains("must-not-persist"));
    }
}
