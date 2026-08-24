CREATE TABLE orders (
    id VARCHAR(36) NOT NULL,
    empresa_id VARCHAR(36) NOT NULL,
    affiliate_id VARCHAR(36) NOT NULL,
    order_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE order_items (
    id VARCHAR(36) NOT NULL,
    order_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19,4) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE commissions (
    id VARCHAR(36) NOT NULL,
    empresa_id VARCHAR(36) NOT NULL,
    beneficiary_id VARCHAR(36) NOT NULL,
    order_id VARCHAR(36) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    network_level INT,
    PRIMARY KEY (id)
);

CREATE TABLE commission_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    is_active BOOLEAN NOT NULL
);

CREATE TABLE commission_plan_levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id BIGINT,
    level INT,
    percentage DECIMAL(19,4),
    CONSTRAINT fk_cpl_plan FOREIGN KEY (plan_id) REFERENCES commission_plans(id)
);

CREATE TABLE distributor_monthly_volumes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(36),
    affiliate_id VARCHAR(36),
    period_year INT,
    period_month INT,
    is_active BOOLEAN,
    is_qualified BOOLEAN,
    current_qualified_level INT
);

CREATE TABLE network_nodes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(36),
    affiliate_id VARCHAR(36),
    sponsor_id VARCHAR(36),
    role VARCHAR(50),
    status VARCHAR(50)
);
