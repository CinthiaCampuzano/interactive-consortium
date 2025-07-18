CREATE TABLE consortium_fee_concept (
    consortium_fee_concept_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    consortium_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    default_amount DECIMAL(10, 2) NOT NULL,
    concept_type VARCHAR(50) NOT NULL,
    fee_type VARCHAR(50) NOT NULL,
    distribution_type VARCHAR(50) NULL,
    active BOOLEAN NOT NULL,
    FOREIGN KEY (consortium_id) REFERENCES consortium(consortium_id) ON DELETE CASCADE
);

CREATE TABLE consortium_fee_period (
    consortium_fee_period_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    consortium_id BIGINT UNSIGNED NOT NULL,
    period_date DATE NOT NULL,
    generation_date DATE NOT NULL,
    due_date DATE NOT NULL,
    fee_period_status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    notes VARCHAR(255) NULL,
    FOREIGN KEY (consortium_id) REFERENCES consortium(consortium_id) ON DELETE CASCADE
);

CREATE TABLE consortium_fee_period_item (
    consortium_fee_period_item_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    consortium_fee_period_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    amount DECIMAL(10, 2) NOT NULL,
    concept_type VARCHAR(50) NOT NULL,
    fee_type VARCHAR(50) NOT NULL,
    distribution_type VARCHAR(50) NULL,
    FOREIGN KEY (consortium_fee_period_id) REFERENCES consortium_fee_period(consortium_fee_period_id)
);

CREATE TABLE department_fee (
    department_fee_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    consortium_fee_period_id BIGINT UNSIGNED NOT NULL,
    department_id BIGINT UNSIGNED NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    due_amount DECIMAL(10, 2) NOT NULL,
    paid_amount DECIMAL(10, 2) NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    FOREIGN KEY (consortium_fee_period_id) REFERENCES consortium_fee_period(consortium_fee_period_id),
    FOREIGN KEY (department_id) REFERENCES department(department_id)
);

CREATE TABLE department_fee_item (
    department_fee_item_id        BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    department_fee_id             BIGINT UNSIGNED NOT NULL,
    consortium_fee_period_item_id BIGINT UNSIGNED NOT NULL,
    proportional                  DECIMAL(4, 2)   NULL,
    amount                        DECIMAL(10, 2)  NULL
)


