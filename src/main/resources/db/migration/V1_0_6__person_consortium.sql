CREATE TABLE consortium_person
(
    consortium_id BIGINT UNSIGNED NOT NULL,
    person_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (consortium_id, person_id),
    FOREIGN KEY (consortium_id) REFERENCES consortium(consortium_id) ON DELETE CASCADE,
    FOREIGN KEY (person_id) REFERENCES person(person_id) ON DELETE CASCADE
);