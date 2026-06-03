-- src/main/resources/db/migration/V1__create_sales_table.sql
CREATE TABLE sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    salesamount NUMERIC(10, 2) DEFAULT 0.00,
    salesdate TIMESTAMP, -- Added missing comma here
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
