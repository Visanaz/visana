CREATE TABLE IF NOT EXISTS commission_plans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    is_active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS commission_plan_levels (
    id INT AUTO_INCREMENT PRIMARY KEY,
    commission_plan_id INT,
    level_number INT,
    payout_value DECIMAL(10,2),
    CONSTRAINT fk_test_cpl_plan FOREIGN KEY (commission_plan_id) REFERENCES commission_plans(id)
);

CREATE TABLE IF NOT EXISTS distributor_monthly_volumes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(36),
    affiliate_id VARCHAR(36),
    period_year INT,
    period_month INT,
    is_active BOOLEAN,
    is_qualified BOOLEAN,
    current_qualified_level INT
);

CREATE TABLE IF NOT EXISTS network_nodes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(36),
    affiliate_id VARCHAR(36),
    sponsor_id VARCHAR(36),
    role VARCHAR(50),
    status VARCHAR(50)
);
