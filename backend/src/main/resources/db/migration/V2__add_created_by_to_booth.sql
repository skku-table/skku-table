/* 1) nullable → 데이터 이관 → NOT NULL 로 두 단계 */
ALTER TABLE booth
ADD COLUMN created_by BIGINT NULL
AFTER description;

-- 이미 존재하는 부스에 기본 작성자(admin) 부여
UPDATE booth
SET
  created_by = (
    SELECT
      id
    FROM
      user
    WHERE
      email = 'admin@test.com'
  )
WHERE
  created_by IS NULL;

/* 2) 제약조건 & NOT NULL 확정 */
ALTER TABLE booth MODIFY COLUMN created_by BIGINT NOT NULL,
ADD CONSTRAINT fk_booth_created_by FOREIGN KEY (created_by) REFERENCES user(id) ON UPDATE CASCADE ON DELETE RESTRICT;