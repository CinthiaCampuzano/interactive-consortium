CREATE TABLE IF NOT EXISTS consortium
(
    consortium_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name                   VARCHAR(40)     NOT NULL,
    address                VARCHAR(60)     NOT NULL,
    city                   VARCHAR(60)     NOT NULL,
    province               VARCHAR(30)     NOT NULL
);