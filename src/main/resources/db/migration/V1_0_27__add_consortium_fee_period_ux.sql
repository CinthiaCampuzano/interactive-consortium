ALTER TABLE consortium_fee_period
    ADD UNIQUE (consortium_id, period_date);
