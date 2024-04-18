CREATE TABLE IF NOT EXISTS administrator
(
    administrator_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name              VARCHAR(40)     NOT NULL,
    last_name         VARCHAR(60)     NOT NULL,
    mail              VARCHAR(60)     NOT NULL UNIQUE,
    dni               VARCHAR(10)     NOT NULL UNIQUE
);
