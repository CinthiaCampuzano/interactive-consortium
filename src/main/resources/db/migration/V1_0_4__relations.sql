ALTER TABLE consortium
    ADD COLUMN administrator_id BIGINT UNSIGNED,
    ADD FOREIGN KEY (administrator_id) REFERENCES administrator(administrator_id);


ALTER TABLE department
    ADD COLUMN consortium_id BIGINT UNSIGNED,
    ADD FOREIGN KEY (consortium_id) REFERENCES consortium(consortium_id);