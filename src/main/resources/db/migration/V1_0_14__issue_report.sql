CREATE TABLE issuer_report (
    issuer_report_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    status VARCHAR(50) NOT NULL,
    subject VARCHAR(100) NOT NULL,
    issue VARCHAR(250),
    response VARCHAR(250),
    created_date TIMESTAMP NOT NULL,
    response_date TIMESTAMP NOT NULL,
    person_id BIGINT UNSIGNED NOT NULL,
    consortium_id BIGINT UNSIGNED NOT NULL
);

ALTER TABLE issuer_report
    ADD CONSTRAINT fk_issuer_report_person_id FOREIGN KEY (person_id) REFERENCES person(person_id);

ALTER TABLE issuer_report
    ADD CONSTRAINT fk_issuer_report_consortium_id FOREIGN KEY (consortium_id) REFERENCES consortium(consortium_id);