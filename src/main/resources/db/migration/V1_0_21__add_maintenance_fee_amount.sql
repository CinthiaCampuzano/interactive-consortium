ALTER TABLE maintenance_fee
    ADD COLUMN total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00;