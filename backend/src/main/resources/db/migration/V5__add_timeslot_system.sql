/* ================================================================
   V5__add_timeslot_system.sql
   
   TimeSlot 기반 예약 시스템 도입:
   1. time_slot 테이블 생성
   2. booth 테이블에 reservation_open_time 컬럼 추가
   3. reservation 테이블에 time_slot_id 컬럼 추가
================================================================ */

-- 1. Create time_slot table
CREATE TABLE IF NOT EXISTS time_slot (
  id BIGINT NOT NULL AUTO_INCREMENT,
  booth_id BIGINT NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  max_capacity INT NOT NULL,
  current_capacity INT NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_time_slot_booth (booth_id),
  KEY idx_time_slot_status (status),
  KEY idx_time_slot_time (start_time, end_time),
  CONSTRAINT fk_time_slot_booth FOREIGN KEY (booth_id) REFERENCES booth (id) ON DELETE CASCADE,
  CONSTRAINT chk_time_slot_time CHECK (start_time < end_time),
  CONSTRAINT chk_max_capacity CHECK (max_capacity > 0),
  CONSTRAINT chk_current_capacity CHECK (current_capacity >= 0 AND current_capacity <= max_capacity),
  CONSTRAINT chk_status CHECK (status IN ('AVAILABLE', 'FULL', 'CLOSED'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 2. Add reservation_open_time column to booth table
ALTER TABLE booth 
ADD COLUMN reservation_open_time DATETIME NULL
AFTER end_date_time;

-- 3. Update existing booths with default reservation_open_time
-- Set reservation open time to 1 day before start_date_time
UPDATE booth 
SET reservation_open_time = DATE_SUB(start_date_time, INTERVAL 1 DAY)
WHERE reservation_open_time IS NULL AND start_date_time IS NOT NULL;

-- 4. Add time_slot_id column to reservation table (nullable for backward compatibility)
ALTER TABLE reservation 
ADD COLUMN time_slot_id BIGINT NULL
AFTER festival_id;

-- 5. Add foreign key constraint for time_slot_id
ALTER TABLE reservation 
ADD CONSTRAINT fk_reservation_time_slot 
FOREIGN KEY (time_slot_id) REFERENCES time_slot (id) ON DELETE SET NULL;

-- 6. Add index for time_slot_id
ALTER TABLE reservation 
ADD KEY idx_reservation_time_slot (time_slot_id);

-- 7. Create unique constraint to prevent duplicate reservations in same time slot by same user
-- Note: This allows NULL time_slot_id (for legacy reservations)
ALTER TABLE reservation 
ADD CONSTRAINT uk_user_time_slot 
UNIQUE (user_id, time_slot_id);

-- 8. Add comment for documentation
ALTER TABLE time_slot
  COMMENT = 'Time slots for booth reservations with capacity management';

ALTER TABLE booth
  MODIFY COLUMN reservation_open_time DATETIME NULL
  COMMENT 'When reservations open for this booth';

ALTER TABLE reservation
  MODIFY COLUMN time_slot_id BIGINT NULL
  COMMENT 'Optional reference to time slot for slot-based reservations';