CREATE TABLE IF NOT EXISTS department
(
    department_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    code                   VARCHAR(10)     NOT NULL UNIQUE
);