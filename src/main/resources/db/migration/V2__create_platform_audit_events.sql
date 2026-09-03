CREATE TABLE platform_audit_events (
    audit_id VARCHAR(36) NOT NULL,
    actor_id VARCHAR(128),
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id VARCHAR(128) NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    correlation_id VARCHAR(128) NOT NULL,
    metadata TEXT NOT NULL,
    PRIMARY KEY (audit_id)
);

CREATE INDEX idx_platform_audit_events_resource ON platform_audit_events (resource_type, resource_id);
CREATE INDEX idx_platform_audit_events_occurred_at ON platform_audit_events (occurred_at);
