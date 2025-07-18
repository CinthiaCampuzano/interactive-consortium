-- Step 1: Drop old constraints and indexes to allow column modifications.
-- It's safer to drop constraints by name before altering the columns they use.
ALTER TABLE payment DROP FOREIGN KEY fk_maintenance_fee_payment_maintenance_fee_id;
ALTER TABLE payment DROP FOREIGN KEY fk_maintenance_fee_payment_department_id;
ALTER TABLE payment DROP INDEX idx_maintenance_fee_payment_maintenance_fee_id_department_id;

-- Step 2: Drop and modify columns to match PaymentEntity
ALTER TABLE payment DROP COLUMN maintenance_fee_id;
ALTER TABLE payment DROP COLUMN status;
ALTER TABLE payment CHANGE COLUMN maintenance_fee_payment_id payment_id BIGINT UNSIGNED AUTO_INCREMENT;
ALTER TABLE payment CHANGE COLUMN department_id department_fee_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE payment CHANGE COLUMN payment_date payment_date DATE;

-- Step 3: Add new columns from PaymentEntity
ALTER TABLE payment ADD COLUMN payment_method VARCHAR(255);
ALTER TABLE payment ADD COLUMN reference_number VARCHAR(255);
ALTER TABLE payment ADD COLUMN notes TEXT;

-- Step 4: Add the new, correct foreign key
ALTER TABLE payment
    ADD CONSTRAINT fk_payment_department_fee_id FOREIGN KEY (department_fee_id) REFERENCES department_fee(department_fee_id);