CREATE TABLE business_profiles (
    id UUID NOT NULL,
    profile_type VARCHAR(40) NOT NULL,
    record_status VARCHAR(30) NOT NULL,
    created_by_actor_id VARCHAR(36),
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_business_profiles_type CHECK (profile_type IN ('NETWORK_MEMBER')),
    CONSTRAINT ck_business_profiles_status CHECK (record_status IN ('RECORD_ACTIVE', 'RECORD_DISABLED')),
    CONSTRAINT fk_business_profiles_creator FOREIGN KEY (created_by_actor_id) REFERENCES platform_actors(actor_id)
);

CREATE TABLE business_profile_actor_links (
    id UUID NOT NULL,
    business_profile_id UUID NOT NULL,
    actor_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_business_profile_actor UNIQUE (actor_id),
    CONSTRAINT uq_actor_business_profile UNIQUE (business_profile_id),
    CONSTRAINT fk_profile_actor_link_profile FOREIGN KEY (business_profile_id) REFERENCES business_profiles(id),
    CONSTRAINT fk_profile_actor_link_actor FOREIGN KEY (actor_id) REFERENCES platform_actors(actor_id)
);

CREATE TABLE network_members (
    id UUID NOT NULL,
    business_profile_id UUID NOT NULL,
    record_status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_network_member_profile UNIQUE (business_profile_id),
    CONSTRAINT ck_network_member_status CHECK (record_status IN ('RECORD_ACTIVE', 'RECORD_DISABLED')),
    CONSTRAINT fk_network_member_profile FOREIGN KEY (business_profile_id) REFERENCES business_profiles(id)
);

CREATE TABLE sponsor_relationships (
    id UUID NOT NULL,
    sponsor_member_id UUID NOT NULL,
    member_id UUID NOT NULL,
    effective_from TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_sponsor_relationship_member UNIQUE (member_id),
    CONSTRAINT ck_sponsor_relationship_status CHECK (status = 'ACTIVE'),
    CONSTRAINT ck_sponsor_relationship_not_self CHECK (sponsor_member_id <> member_id),
    CONSTRAINT fk_sponsor_relationship_sponsor FOREIGN KEY (sponsor_member_id) REFERENCES network_members(id),
    CONSTRAINT fk_sponsor_relationship_member FOREIGN KEY (member_id) REFERENCES network_members(id)
);

CREATE TABLE genealogy_closure (
    ancestor_member_id UUID NOT NULL,
    descendant_member_id UUID NOT NULL,
    depth INTEGER NOT NULL,
    PRIMARY KEY (ancestor_member_id, descendant_member_id),
    CONSTRAINT ck_genealogy_closure_depth CHECK (depth >= 0),
    CONSTRAINT fk_genealogy_closure_ancestor FOREIGN KEY (ancestor_member_id) REFERENCES network_members(id),
    CONSTRAINT fk_genealogy_closure_descendant FOREIGN KEY (descendant_member_id) REFERENCES network_members(id)
);

CREATE INDEX ix_sponsor_relationship_sponsor ON sponsor_relationships(sponsor_member_id);
CREATE INDEX ix_genealogy_closure_descendant_depth ON genealogy_closure(descendant_member_id, depth);
CREATE INDEX ix_genealogy_closure_ancestor_depth ON genealogy_closure(ancestor_member_id, depth);
