package com.visana.erp.network.foundation.domain;
import java.time.Instant; import java.util.UUID;
public record NetworkMember(UUID id, UUID businessProfileId, RecordStatus status, Instant createdAt) { public enum RecordStatus { RECORD_ACTIVE, RECORD_DISABLED } }
