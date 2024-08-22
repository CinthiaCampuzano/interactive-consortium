CREATE TABLE IF NOT EXISTS amenity
(
    amenity_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name              VARCHAR(40)     NOT NULL,
    max_bookings      INT UNSIGNED NOT NULL,
    consortium_id     BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (consortium_id) REFERENCES consortium(consortium_id) ON DELETE CASCADE

);
