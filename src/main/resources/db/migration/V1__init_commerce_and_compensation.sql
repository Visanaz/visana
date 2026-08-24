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
