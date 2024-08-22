CREATE TABLE IF NOT EXISTS booking(
    booking_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    start_date DATE NOT NULL,
    shift      VARCHAR(40)     NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    amenity_id BIGINT UNSIGNED NOT NULL,
    resident_id BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (amenity_id) REFERENCES amenity(amenity_id) ON DELETE CASCADE,
    FOREIGN KEY (resident_id) REFERENCES person(person_id)  ON DELETE CASCADE

);