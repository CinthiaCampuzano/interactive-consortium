CREATE TABLE maintenance_fee_payment (
                                         maintenance_fee_payment_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                         maintenance_fee_id BIGINT UNSIGNED NOT NULL,
                                         department_id BIGINT UNSIGNED NOT NULL,
                                         status VARCHAR(50),
                                         payment_date TIMESTAMP,
                                         amount DECIMAL(10, 2)
);

ALTER TABLE maintenance_fee_payment
    ADD CONSTRAINT fk_maintenance_fee_payment_maintenance_fee_id FOREIGN KEY (maintenance_fee_id) REFERENCES maintenance_fee(maintenance_fee_id);
ALTER TABLE maintenance_fee_payment
    ADD CONSTRAINT fk_maintenance_fee_payment_department_id FOREIGN KEY (department_id) REFERENCES department(department_id);
ALTER TABLE maintenance_fee_payment
    ADD UNIQUE INDEX idx_maintenance_fee_payment_maintenance_fee_id_department_id (maintenance_fee_id, department_id);