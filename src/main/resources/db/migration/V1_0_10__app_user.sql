CREATE TABLE app_user (
    app_user_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    authority VARCHAR(255) NOT NULL,
    person_person_id BIGINT UNSIGNED REFERENCES person(person_id),
    administrator_administrator_id BIGINT UNSIGNED REFERENCES administrator(administrator_id)
);