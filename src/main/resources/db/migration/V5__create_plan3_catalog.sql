CREATE TABLE product_categories (
    id UUID NOT NULL,
    name VARCHAR(160) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_product_categories_name UNIQUE (name),
    CONSTRAINT ck_product_categories_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE catalog_products (
    id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    base_price DECIMAL(19,4) NOT NULL,
    status VARCHAR(20) NOT NULL,
    category_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_catalog_products_sku UNIQUE (sku),
    CONSTRAINT uq_catalog_products_code UNIQUE (code),
    CONSTRAINT fk_catalog_products_category FOREIGN KEY (category_id) REFERENCES product_categories(id),
    CONSTRAINT ck_catalog_products_price CHECK (base_price >= 0),
    CONSTRAINT ck_catalog_products_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX ix_catalog_products_status_name ON catalog_products(status, name);
