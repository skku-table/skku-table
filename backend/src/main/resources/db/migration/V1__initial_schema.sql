-- Create user table
CREATE TABLE IF NOT EXISTS user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255),
  role VARCHAR(50) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_email (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Create festival table
CREATE TABLE IF NOT EXISTS festival (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255),
  description TEXT,
  location VARCHAR(255),
  start_date DATETIME,
  end_date DATETIME,
  poster_image_url VARCHAR(500),
  map_image_url VARCHAR(500),
  like_count INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Create booth table
CREATE TABLE IF NOT EXISTS booth (
  id BIGINT NOT NULL AUTO_INCREMENT,
  festival_id BIGINT,
  name VARCHAR(255),
  host VARCHAR(255),
  location VARCHAR(255),
  description TEXT,
  start_date_time DATETIME,
  end_date_time DATETIME,
  like_count INT DEFAULT 0,
  poster_image_url VARCHAR(500),
  event_image_url VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_booth_festival (festival_id),
  CONSTRAINT fk_booth_festival FOREIGN KEY (festival_id) REFERENCES festival (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Create reservation table
CREATE TABLE IF NOT EXISTS reservation (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  booth_id BIGINT NOT NULL,
  festival_id BIGINT,
  reservation_time DATETIME,
  number_of_people INT, -- people_count가 아니라 number_of_people
  payment_method VARCHAR(20) NOT NULL, -- NOT NULL 추가
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_reservation_user (user_id),
  KEY idx_reservation_booth (booth_id),
  KEY idx_reservation_festival (festival_id),
  CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
  CONSTRAINT fk_reservation_booth FOREIGN KEY (booth_id) REFERENCES booth (id) ON DELETE CASCADE,
  CONSTRAINT fk_reservation_festival FOREIGN KEY (festival_id) REFERENCES festival (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Create user_festival_like table
CREATE TABLE IF NOT EXISTS user_festival_like (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  festival_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_festival (user_id, festival_id),
  KEY idx_user_festival_like_user (user_id),
  KEY idx_user_festival_like_festival (festival_id),
  CONSTRAINT fk_user_festival_like_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
  CONSTRAINT fk_user_festival_like_festival FOREIGN KEY (festival_id) REFERENCES festival (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Create user_booth_like table
CREATE TABLE IF NOT EXISTS user_booth_like (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  booth_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_booth (user_id, booth_id),
  KEY idx_user_booth_like_user (user_id),
  KEY idx_user_booth_like_booth (booth_id),
  CONSTRAINT fk_user_booth_like_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
  CONSTRAINT fk_user_booth_like_booth FOREIGN KEY (booth_id) REFERENCES booth (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;