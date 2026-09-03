CREATE TABLE commerce_orders (
    id UUID NOT NULL,
    owner_actor_id VARCHAR(36) NOT NULL,
    status VARCHAR(50) NOT NULL,
    total DECIMAL(19,4) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_commerce_orders_owner FOREIGN KEY (owner_actor_id) REFERENCES platform_actors(actor_id),
    CONSTRAINT ck_commerce_orders_status CHECK (status IN ('PENDING', 'PAID', 'CANCELLED')),
    CONSTRAINT ck_commerce_orders_total CHECK (total >= 0)
);

CREATE TABLE commerce_order_lines (
    id UUID NOT NULL,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_code_snapshot VARCHAR(100) NOT NULL,
    product_name_snapshot VARCHAR(200) NOT NULL,
    unit_price_snapshot DECIMAL(19,4) NOT NULL,
    quantity INTEGER NOT NULL,
    line_total DECIMAL(19,4) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_commerce_order_lines_order FOREIGN KEY (order_id) REFERENCES commerce_orders(id),
    CONSTRAINT fk_commerce_order_lines_product FOREIGN KEY (product_id) REFERENCES catalog_products(id),
    CONSTRAINT ck_commerce_order_lines_quantity CHECK (quantity > 0),
    CONSTRAINT ck_commerce_order_lines_unit_price CHECK (unit_price_snapshot >= 0),
    CONSTRAINT ck_commerce_order_lines_total CHECK (line_total >= 0)
);

CREATE INDEX ix_commerce_orders_owner_created ON commerce_orders(owner_actor_id, created_at DESC);
CREATE INDEX ix_commerce_order_lines_order ON commerce_order_lines(order_id);
