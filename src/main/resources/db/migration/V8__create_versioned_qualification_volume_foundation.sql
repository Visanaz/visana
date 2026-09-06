CREATE TABLE business_rule_versions (
    id UUID NOT NULL,
    rule_set_id VARCHAR(160) NOT NULL,
    version_number INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    effective_from TIMESTAMP NOT NULL,
    effective_to TIMESTAMP NULL,
    source VARCHAR(500) NOT NULL,
    approved_at TIMESTAMP NULL,
    approved_by VARCHAR(160) NULL,
    provisional_confidence VARCHAR(20) NULL,
    notes VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_business_rule_version UNIQUE (rule_set_id, version_number),
    CONSTRAINT ck_business_rule_version_status CHECK (status IN ('DRAFT', 'PROVISIONAL', 'APPROVED', 'RETIRED')),
    CONSTRAINT ck_business_rule_version_dates CHECK (effective_to IS NULL OR effective_to > effective_from),
    CONSTRAINT ck_business_rule_version_provisional CHECK (status <> 'PROVISIONAL' OR provisional_confidence IS NOT NULL),
    CONSTRAINT ck_business_rule_version_approval CHECK (status <> 'APPROVED' OR (approved_at IS NOT NULL AND approved_by IS NOT NULL))
);

CREATE TABLE qualification_rank_thresholds (
    id UUID NOT NULL,
    business_rule_version_id UUID NOT NULL,
    rank_level INTEGER NOT NULL,
    affiliation_required BOOLEAN NOT NULL,
    min_active_directs INTEGER NOT NULL,
    min_indirects INTEGER NOT NULL,
    min_team_volume DECIMAL(19,4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_qualification_threshold_version_level UNIQUE (business_rule_version_id, rank_level),
    CONSTRAINT fk_qualification_threshold_rule_version FOREIGN KEY (business_rule_version_id) REFERENCES business_rule_versions(id),
    CONSTRAINT ck_qualification_threshold_level CHECK (rank_level BETWEEN 1 AND 8),
    CONSTRAINT ck_qualification_threshold_counts CHECK (min_active_directs >= 0 AND min_indirects >= 0),
    CONSTRAINT ck_qualification_threshold_volume CHECK (min_team_volume >= 0)
);

CREATE TABLE volume_results (
    id UUID NOT NULL,
    member_id UUID NOT NULL,
    period_key VARCHAR(120) NOT NULL,
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL,
    business_rule_version_id UUID NOT NULL,
    personal_volume DECIMAL(19,4) NOT NULL,
    team_volume DECIMAL(19,4) NOT NULL,
    qualification_base DECIMAL(19,4) NOT NULL,
    adjustment_total DECIMAL(19,4) NOT NULL,
    calculated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_volume_result_member FOREIGN KEY (member_id) REFERENCES network_members(id),
    CONSTRAINT fk_volume_result_rule_version FOREIGN KEY (business_rule_version_id) REFERENCES business_rule_versions(id),
    CONSTRAINT ck_volume_result_period CHECK (period_end > period_start)
);

CREATE TABLE volume_result_evidence (
    volume_result_id UUID NOT NULL,
    evidence_reference VARCHAR(500) NOT NULL,
    PRIMARY KEY (volume_result_id, evidence_reference),
    CONSTRAINT fk_volume_evidence_result FOREIGN KEY (volume_result_id) REFERENCES volume_results(id)
);

CREATE TABLE qualification_results (
    id UUID NOT NULL,
    member_id UUID NOT NULL,
    volume_result_id UUID NOT NULL,
    period_key VARCHAR(120) NOT NULL,
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL,
    business_rule_version_id UUID NOT NULL,
    qualified_level INTEGER NOT NULL,
    qualified BOOLEAN NOT NULL,
    activation_eligible BOOLEAN NOT NULL,
    calculated_at TIMESTAMP NOT NULL,
    explanation VARCHAR(2000) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_qualification_result_member FOREIGN KEY (member_id) REFERENCES network_members(id),
    CONSTRAINT fk_qualification_result_volume FOREIGN KEY (volume_result_id) REFERENCES volume_results(id),
    CONSTRAINT fk_qualification_result_rule_version FOREIGN KEY (business_rule_version_id) REFERENCES business_rule_versions(id),
    CONSTRAINT ck_qualification_result_level CHECK (qualified_level BETWEEN 0 AND 8),
    CONSTRAINT ck_qualification_result_flag CHECK ((qualified = TRUE AND qualified_level > 0) OR (qualified = FALSE AND qualified_level = 0)),
    CONSTRAINT ck_qualification_result_period CHECK (period_end > period_start)
);

CREATE TABLE qualification_result_evidence (
    qualification_result_id UUID NOT NULL,
    evidence_reference VARCHAR(500) NOT NULL,
    PRIMARY KEY (qualification_result_id, evidence_reference),
    CONSTRAINT fk_qualification_evidence_result FOREIGN KEY (qualification_result_id) REFERENCES qualification_results(id)
);

CREATE TABLE qualification_result_assessments (
    qualification_result_id UUID NOT NULL,
    rank_level INTEGER NOT NULL,
    satisfied BOOLEAN NOT NULL,
    activation_eligible BOOLEAN NOT NULL,
    affiliation_required BOOLEAN NOT NULL,
    affiliated BOOLEAN NOT NULL,
    required_directs INTEGER NOT NULL,
    actual_directs INTEGER NOT NULL,
    required_indirects INTEGER NOT NULL,
    actual_indirects INTEGER NOT NULL,
    required_team_volume DECIMAL(19,4) NOT NULL,
    actual_team_volume DECIMAL(19,4) NOT NULL,
    explanation VARCHAR(2000) NOT NULL,
    PRIMARY KEY (qualification_result_id, rank_level),
    CONSTRAINT fk_qualification_assessment_result FOREIGN KEY (qualification_result_id) REFERENCES qualification_results(id),
    CONSTRAINT ck_qualification_assessment_level CHECK (rank_level BETWEEN 1 AND 8),
    CONSTRAINT ck_qualification_assessment_counts CHECK (
        required_directs >= 0 AND actual_directs >= 0 AND required_indirects >= 0 AND actual_indirects >= 0
    )
);

CREATE INDEX ix_business_rule_versions_effective ON business_rule_versions(rule_set_id, status, effective_from, effective_to);
CREATE INDEX ix_volume_results_member_period ON volume_results(member_id, period_start, period_end);
CREATE INDEX ix_volume_results_rule_version ON volume_results(business_rule_version_id);
CREATE INDEX ix_qualification_results_member_period ON qualification_results(member_id, period_start, period_end);
CREATE INDEX ix_qualification_results_rule_version ON qualification_results(business_rule_version_id);

INSERT INTO business_rule_versions (
    id, rule_set_id, version_number, status, effective_from, effective_to, source,
    approved_at, approved_by, provisional_confidence, notes, created_at
) VALUES (
    '8d8ef6e5-0905-4c01-9000-000000000001',
    'BUSINESS_RULES_WORKING_BASELINE_CATHERINE_2026-09-05_PROVISIONAL',
    1,
    'PROVISIONAL',
    '2026-09-05 00:00:00',
    NULL,
    'VISANA verbal clarification 2026-09-05',
    NULL,
    NULL,
    'HIGH',
    'Not client-approved; financial production effects are forbidden',
    '2026-09-05 00:00:00'
);

INSERT INTO qualification_rank_thresholds (
    id, business_rule_version_id, rank_level, affiliation_required,
    min_active_directs, min_indirects, min_team_volume, currency_code
) VALUES
    ('8d8ef6e5-0905-4c01-9100-000000000001', '8d8ef6e5-0905-4c01-9000-000000000001', 1, TRUE, 0, 0, 0.0000, 'COP'),
    ('8d8ef6e5-0905-4c01-9100-000000000002', '8d8ef6e5-0905-4c01-9000-000000000001', 2, FALSE, 5, 0, 3000000.0000, 'COP'),
    ('8d8ef6e5-0905-4c01-9100-000000000003', '8d8ef6e5-0905-4c01-9000-000000000001', 3, FALSE, 7, 25, 25000000.0000, 'COP'),
    ('8d8ef6e5-0905-4c01-9100-000000000004', '8d8ef6e5-0905-4c01-9000-000000000001', 4, FALSE, 9, 110, 80000000.0000, 'COP'),
    ('8d8ef6e5-0905-4c01-9100-000000000005', '8d8ef6e5-0905-4c01-9000-000000000001', 5, FALSE, 12, 350, 250000000.0000, 'COP'),
    ('8d8ef6e5-0905-4c01-9100-000000000006', '8d8ef6e5-0905-4c01-9000-000000000001', 6, FALSE, 15, 1200, 1000000000.0000, 'COP'),
    ('8d8ef6e5-0905-4c01-9100-000000000007', '8d8ef6e5-0905-4c01-9000-000000000001', 7, FALSE, 20, 3500, 2500000000.0000, 'COP'),
    ('8d8ef6e5-0905-4c01-9100-000000000008', '8d8ef6e5-0905-4c01-9000-000000000001', 8, FALSE, 25, 15000, 30000000000.0000, 'COP');
