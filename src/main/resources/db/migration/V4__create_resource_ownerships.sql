CREATE TABLE resource_ownerships (
    ownership_id VARCHAR(36) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id VARCHAR(36) NOT NULL,
    owner_actor_id VARCHAR(36) NOT NULL,
    assigned_at TIMESTAMP NOT NULL,
    PRIMARY KEY (ownership_id),
    CONSTRAINT uq_resource_ownerships_resource UNIQUE (resource_type, resource_id),
    CONSTRAINT fk_resource_ownerships_actor FOREIGN KEY (owner_actor_id) REFERENCES platform_actors(actor_id)
);

CREATE INDEX idx_resource_ownerships_owner ON resource_ownerships (owner_actor_id);
