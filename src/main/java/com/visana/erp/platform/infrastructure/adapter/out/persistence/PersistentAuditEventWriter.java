package com.visana.erp.platform.infrastructure.adapter.out.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visana.erp.platform.application.audit.AuditEventWriter;
import com.visana.erp.platform.domain.audit.AuditEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class PersistentAuditEventWriter implements AuditEventWriter {
    private static final Set<String> FORBIDDEN_METADATA_KEYS = Set.of("authorization", "password", "secret", "token", "apikey", "api_key", "card", "bankaccount", "bank_account");
    private final AuditEventSpringDataRepository repository;
    private final ObjectMapper objectMapper;

    public PersistentAuditEventWriter(AuditEventSpringDataRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void append(AuditEvent event) {
        repository.save(new AuditEventJpaEntity(UUID.randomUUID(), event.actor(), event.action(), event.resourceType(),
                event.resourceId(), event.occurredAt(), event.correlationId(), serialize(sanitize(event.metadata()))));
    }

    private Map<String, String> sanitize(Map<String, String> metadata) {
        Map<String, String> safe = new LinkedHashMap<>();
        metadata.forEach((key, value) -> {
            String normalized = key.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
            if (FORBIDDEN_METADATA_KEYS.stream().noneMatch(normalized::contains)) {
                safe.put(key, value);
            }
        });
        return safe;
    }

    private String serialize(Map<String, String> metadata) {
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Audit metadata could not be serialized", ex);
        }
    }
}
