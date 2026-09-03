CREATE TABLE platform_actors (
    actor_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (actor_id),
    CONSTRAINT chk_platform_actors_status CHECK (status IN ('ACTIVE', 'DISABLED'))
);

CREATE TABLE external_identity_links (
    link_id VARCHAR(36) NOT NULL,
    provider VARCHAR(40) NOT NULL,
    issuer VARCHAR(512) NOT NULL,
    subject VARCHAR(512) NOT NULL,
    actor_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,
    linked_at TIMESTAMP NOT NULL,
    disabled_at TIMESTAMP NULL,
    PRIMARY KEY (link_id),
    CONSTRAINT uq_external_identity_links_provider_issuer_subject UNIQUE (provider, issuer, subject),
    CONSTRAINT fk_external_identity_links_actor FOREIGN KEY (actor_id) REFERENCES platform_actors(actor_id),
    CONSTRAINT chk_external_identity_links_status CHECK (status IN ('ACTIVE', 'DISABLED'))
);

CREATE INDEX idx_external_identity_links_actor ON external_identity_links (actor_id);
