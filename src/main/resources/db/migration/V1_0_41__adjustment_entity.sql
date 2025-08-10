CREATE TABLE adjustment
(
    id                       BIGINT AUTO_INCREMENT       NOT NULL,
    consortium_fee_period_id BIGINT UNSIGNED             NOT NULL,
    department_id            BIGINT UNSIGNED             NOT NULL,
    description              VARCHAR(255)                NULL,
    adjustment_type          VARCHAR(50)                 NOT NULL,
    operation_type           VARCHAR(50)                 NOT NULL,
    amount                   DECIMAL(10, 2) DEFAULT 0.00 NOT NULL,
    CONSTRAINT pk_adjustment PRIMARY KEY (id)
);

ALTER TABLE adjustment
    ADD CONSTRAINT FK_ADJUSTMENT_ON_CONSORTIUM_FEE_PERIOD FOREIGN KEY (consortium_fee_period_id) REFERENCES consortium_fee_period (consortium_fee_period_id);

ALTER TABLE adjustment
    ADD CONSTRAINT FK_ADJUSTMENT_ON_DEPARTMENT FOREIGN KEY (department_id) REFERENCES department (department_id);