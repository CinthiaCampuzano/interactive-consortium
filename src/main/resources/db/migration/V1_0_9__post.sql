CREATE TABLE IF NOT EXISTS post (

  post_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  content VARCHAR(1000) NOT NULL,
  creation_post_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  consortium_id     BIGINT UNSIGNED NOT NULL,
  FOREIGN KEY (consortium_id) REFERENCES consortium(consortium_id) ON DELETE CASCADE

);