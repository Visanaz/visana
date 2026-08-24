CREATE TABLE IF NOT EXISTS commission_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    is_active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS commission_plan_levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id BIGINT,
    level INT,
    percentage DECIMAL(19,4),
    CONSTRAINT fk_test_cpl_plan FOREIGN KEY (plan_id) REFERENCES commission_plans(id)
);

CREATE TABLE IF NOT EXISTS distributor_monthly_volumes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(36),
    affiliate_id VARCHAR(36),
    period_year INT,
    period_month INT,
    is_active BOOLEAN,
    is_qualified BOOLEAN,
    current_qualified_level INT
);

CREATE TABLE IF NOT EXISTS network_nodes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(36),
    affiliate_id VARCHAR(36),
    sponsor_id VARCHAR(36),
    role VARCHAR(50),
    status VARCHAR(50)
);
