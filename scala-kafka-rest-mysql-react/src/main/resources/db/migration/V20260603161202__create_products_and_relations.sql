-- src/main/resources/db/migration/V1__create_products_and_relations.sql

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descriptions VARCHAR(255) NOT NULL UNIQUE,
    qty INT DEFAULT 0,
    unit VARCHAR(255),
    costprice NUMERIC(10, 2) DEFAULT 0.00,
    sellprice NUMERIC(10, 2) DEFAULT 0.00,
    saleprice NUMERIC(10, 2) DEFAULT 0.00,
    productpicture VARCHAR(255),
    alertstocks INT DEFAULT 0,
    criticalstocks INT DEFAULT 0,
    category_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_product_category 
        FOREIGN KEY (category_id) 
        REFERENCES categories(id) 
        ON DELETE RESTRICT
);
