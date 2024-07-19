CREATE TABLE IF NOT EXISTS person
(
    person_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name              VARCHAR(40)     NOT NULL,
    last_name         VARCHAR(60)     NOT NULL,
    mail              VARCHAR(60)     NOT NULL UNIQUE,
    dni               VARCHAR(10)     NOT NULL UNIQUE,
    phone_Number      VARCHAR(15)     NOT NULL
);

ALTER TABLE department
    ADD COLUMN propietary_id BIGINT UNSIGNED,
    ADD FOREIGN KEY (propietary_id) REFERENCES person(person_id) ON DELETE SET NULL,
    ADD COLUMN resident_id BIGINT UNSIGNED,
    ADD FOREIGN KEY (resident_id) REFERENCES person(person_id) ON DELETE SET NULL;