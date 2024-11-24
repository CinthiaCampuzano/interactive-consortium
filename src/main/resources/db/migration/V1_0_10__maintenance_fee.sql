CREATE TABLE maintenance_fee (
    maintenance_fee_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    period DATE NOT NULL,
    consortium_id BIGINT UNSIGNED NOT NULL,
    file_name VARCHAR(100) NOT NULL,
    upload_date TIMESTAMP NOT NULL
);
ALTER TABLE maintenance_fee
    ADD CONSTRAINT fk_maintenance_fee_consortium_id FOREIGN KEY (consortium_id) REFERENCES consortium(consortium_id);
ALTER TABLE maintenance_fee
    ADD UNIQUE INDEX idx_maintenance_fee_period_consortium_id (period, consortium_id);