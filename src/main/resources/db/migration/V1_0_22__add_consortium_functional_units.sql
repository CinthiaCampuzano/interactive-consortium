ALTER TABLE consortium
    ADD COLUMN consortium_type VARCHAR(40)     NOT NULL,
    ADD COLUMN functional_units INT NOT NULL,
    ADD COLUMN floors INT,
    ADD COLUMN apartments_per_floor INT ;