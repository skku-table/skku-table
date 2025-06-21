/* ──────────────────────────────────────────────────────────────
Repeatable  R__dev_seed_data.sql  –  DEV/LOCAL 초기화 + 시드
① 부팅마다 기존 데이터 전체 삭제(TRUNCATE)
② 최신 시드 데이터 다시 삽입
※ 운영 경로에는 포함하지 마세요!
────────────────────────────────────────────────────────────── */
/* =============================================================
0. RESET  –  FK 잠시 해제 후 TRUNCATE
------------------------------------------------------------- */
SET
  FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE reservation;

TRUNCATE TABLE time_slot;

TRUNCATE TABLE user_booth_like;

TRUNCATE TABLE user_festival_like;

TRUNCATE TABLE booth;

TRUNCATE TABLE festival;

TRUNCATE TABLE user;

SET
  FOREIGN_KEY_CHECKS = 1;

/* =============================================================
1. USERS   –  비밀번호는 모두 'password123'
------------------------------------------------------------- */
INSERT INTO
  user(
    id,
    name,
    email,
    password,
    role,
    university,
    major,
    profile_image_url,
    created_at,
    updated_at
  )
VALUES
  (
    1,
    '전수민',
    'user1@skku.edu',
    '$2a$10$fihxE1rSIi64vEXARNikYu7vvrFt6OS.PyLqMp4B31bJzQM8Z4chO',
    'USER',
    '성균관대학교',
    '컴퓨터교육',
    '/src/userprofile.png',
    NOW(),
    NOW()
  ),
  (
    2,
    '황현진',
    'user2@skku.edu',
    '$2a$10$fihxE1rSIi64vEXARNikYu7vvrFt6OS.PyLqMp4B31bJzQM8Z4chO',
    'USER',
    '성균관대학교',
    '경제학',
    '/src/userprofile.png',
    NOW(),
    NOW()
  ),
  (
    3,
    '이균서',
    'admin@skku.edu',
    '$2a$10$fihxE1rSIi64vEXARNikYu7vvrFt6OS.PyLqMp4B31bJzQM8Z4chO',
    'ADMIN',
    '성균관대학교',
    '경영학',
    '/src/userprofile.png',
    NOW(),
    NOW()
  ),
  (
    4,
    '권태환',
    'host1@skku.edu',
    '$2a$10$fihxE1rSIi64vEXARNikYu7vvrFt6OS.PyLqMp4B31bJzQM8Z4chO',
    'HOST',
    '성균관대학교',
    '전자공학',
    '/src/userprofile.png',
    NOW(),
    NOW()
  ),
  (
    5,
    '타메르',
    'host2@skku.edu',
    '$2a$10$fihxE1rSIi64vEXARNikYu7vvrFt6OS.PyLqMp4B31bJzQM8Z4chO',
    'HOST',
    '성균관대학교',
    '디자인',
    '/src/userprofile.png',
    NOW(),
    NOW()
  );

/* =============================================================
2. FESTIVALS   –  ADMIN(3)이 생성 (2025년 6월 21-22일로 업데이트)
------------------------------------------------------------- */
INSERT INTO
  festival (
    id,
    name,
    description,
    location,
    start_date,
    end_date,
    poster_image_url,
    map_image_url,
    like_count,
    created_at,
    updated_at
  )
VALUES
  (
    1,
    '2025 성균관대 여름 축제',
    '성균관대학교 여름 축제입니다! 다양한 공연과 부스가 준비되어 있습니다.',
    '성균관대학교 자연과학캠퍼스',
    '2025-06-21',
    '2025-06-22',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749455884/kcicwce5sgn5onqfzea8.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749455854/plzvsshapqguzjbjpixy.png',
    150,
    NOW(),
    NOW()
  ),
  (
    2,
    '2025 성균관대 여름 특별전',
    '성균관대학교 여름 특별전! 풍성한 먹거리와 볼거리가 가득합니다.',
    '성균관대학교 인문사회과학캠퍼스',
    '2025-06-22',
    '2025-06-22',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749456311/snjku9czhglrsjxbcnwl.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749455854/plzvsshapqguzjbjpixy.png',
    200,
    NOW(),
    NOW()
  ),
  (
    3,
    '2025 대동제',
    '학생회 주최 대동제! 모든 성균인이 하나되는 축제입니다.',
    '성균관대학교 자연과학캠퍼스',
    '2025-06-21',
    '2025-06-22',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749457780/werbb5bcsyuzn4eooy77.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749456313/zogfhbmwdthzkdcrjdg1.jpg',
    300,
    NOW(),
    NOW()
  );

/* =============================================================
3. BOOTHS  – created_by FK 필수 (2025년 6월 21-22일로 업데이트)
------------------------------------------------------------- */
INSERT INTO
  booth (
    id,
    festival_id,
    created_by,
    name,
    host,
    location,
    description,
    start_date_time,
    end_date_time,
    reservation_open_time,
    like_count,
    poster_image_url,
    event_image_url,
    created_at,
    updated_at
  )
VALUES
  /* 여름 축제 – host1 */
  (
    1,
    1,
    4,
    '떡볶이 부스',
    '요리 동아리',
    '학생회관 앞',
    '매콤달콤한 떡볶이를 판매합니다!',
    '2025-06-21 11:00',
    '2025-06-21 18:00',
    '2025-06-21 10:00',
    50,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749451072/wa37se78uujl1cxlgqwp.jpg',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749119788/cld-sample-4.jpg',
    NOW(),
    NOW()
  ),
  (
    2,
    1,
    4,
    '포토존',
    '사진 동아리',
    '중앙광장',
    '예쁜 사진을 찍어드립니다!',
    '2025-06-21 10:00',
    '2025-06-22 20:00',
    '2025-06-21 09:00',
    80,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749452445/txlx2ubqm7nkhnkynuvn.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749452444/yxqv3hkmpx4lw3uu7ouk.jpg',
    NOW(),
    NOW()
  ),
  (
    3,
    1,
    4,
    '타로 부스',
    '심리학과',
    '도서관 앞',
    '타로로 당신의 운세를 봐드립니다',
    '2025-06-22 12:00',
    '2025-06-22 17:00',
    '2025-06-21 10:00',
    30,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749455091/lxnqsi8efdbjr7ek620k.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749455088/fi2tyk0xw3f59mstdbhx.jpg',
    NOW(),
    NOW()
  ),
  (
    4,
    1,
    4,
    '버스킹 공연',
    '음악 동아리',
    '야외 무대',
    '다양한 장르의 음악 공연',
    '2025-06-21 17:00',
    '2025-06-22 21:00',
    '2025-06-21 09:00',
    100,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749455362/suq5t8iaricxlgd86vez.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749455360/o1lsd2kqls6nj7mszugc.jpg',
    NOW(),
    NOW()
  ),
  /* 여름 특별전 – host1 */
  (
    5,
    2,
    4,
    '호떡 부스',
    '봉사 동아리',
    '정문 앞',
    '따뜻한 호떡을 판매합니다',
    '2025-06-22 11:00',
    '2025-06-22 18:00',
    '2025-06-21 10:00',
    60,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749456545/j78bhadk7nzfet4vcvrv.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749456558/sjj67jexg8w48li3ik7w.png',
    NOW(),
    NOW()
  ),
  (
    6,
    2,
    4,
    '플리마켓',
    '경영학과',
    '중앙도서관 앞',
    '다양한 수공예품 판매',
    '2025-06-22 10:00',
    '2025-06-22 19:00',
    '2025-06-21 10:00',
    90,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749456822/ghog4i3zhin6vazojn1z.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749456820/w9gd6276jkf4tf3fl3hn.png',
    NOW(),
    NOW()
  ),
  (
    7,
    2,
    4,
    '미니게임',
    '컴퓨터학과',
    '공학관 앞',
    '재미있는 미니게임과 상품!',
    '2025-06-22 12:00',
    '2025-06-22 18:00',
    '2025-06-21 10:00',
    40,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749456940/pw88m9eitbhjbpi3dbno.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749456940/pw88m9eitbhjbpi3dbno.png',
    NOW(),
    NOW()
  ),
  /* 대동제 – host2 */
  (
    8,
    3,
    5,
    '주점',
    '경제학과',
    '학생회관 3층',
    '다양한 안주와 음료 판매',
    '2025-06-21 17:00',
    '2025-06-22 23:00',
    '2025-06-21 09:00',
    150,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749458261/k6ugculwmvsilctv0m82.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749458261/k6ugculwmvsilctv0m82.png',
    NOW(),
    NOW()
  ),
  (
    9,
    3,
    5,
    '귀신의 집',
    '연극 동아리',
    '체육관',
    '무서운 귀신의 집 체험',
    '2025-06-21 15:00',
    '2025-06-22 22:00',
    '2025-06-21 09:00',
    120,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749458292/wn9rawl8mmb0ezy5mqwb.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749458292/wn9rawl8mmb0ezy5mqwb.png',
    NOW(),
    NOW()
  ),
  (
    10,
    3,
    5,
    '푸드트럭',
    '학생회',
    '중앙광장',
    '다양한 세계 음식',
    '2025-06-21 11:00',
    '2025-06-22 21:00',
    '2025-06-21 09:00',
    200,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749119779/samples/food/spices.jpg',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749119779/samples/food/spices.jpg',
    NOW(),
    NOW()
  ),
  /* 추가 부스 (페이지네이션 샘플) */
  (
    11,
    1,
    4,
    '솜사탕 부스',
    '화학과',
    '과학관 앞',
    '달콤한 솜사탕 판매',
    '2025-06-22 11:00',
    '2025-06-22 17:00',
    '2025-06-21 10:00',
    25,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749455727/dgu0drgvrzlxbizefzfh.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749455727/dgu0drgvrzlxbizefzfh.png',
    NOW(),
    NOW()
  ),
  (
    12,
    1,
    4,
    '네일아트',
    '미술학과',
    '예술관 로비',
    '예쁜 네일아트 서비스',
    '2025-06-21 12:00',
    '2025-06-22 18:00',
    '2025-06-21 09:00',
    35,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749456065/pqqs6mctdvyxp7ufbd8u.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749456065/pqqs6mctdvyxp7ufbd8u.png',
    NOW(),
    NOW()
  ),
  (
    13,
    2,
    4,
    '커피 부스',
    '바리스타 동아리',
    '경영관 앞',
    '직접 내린 커피 판매',
    '2025-06-22 09:00',
    '2025-06-22 18:00',
    '2025-06-21 10:00',
    45,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749457065/wju1kfwkrjifqdzdokfj.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749457065/wju1kfwkrjifqdzdokfj.png',
    NOW(),
    NOW()
  ),
  (
    14,
    2,
    4,
    '페이스페인팅',
    '디자인학과',
    '미술관 앞',
    '다양한 페이스페인팅',
    '2025-06-22 11:00',
    '2025-06-22 17:00',
    '2025-06-21 10:00',
    20,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749457744/xb5muwvibwlm3hogrbz4.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749457744/xb5muwvibwlm3hogrbz4.png',
    NOW(),
    NOW()
  ),
  (
    15,
    3,
    5,
    '보드게임 카페',
    '보드게임 동아리',
    '학생회관 2층',
    '다양한 보드게임 체험',
    '2025-06-21 13:00',
    '2025-06-22 20:00',
    '2025-06-21 09:00',
    55,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749458368/qm3vkip13mhbmtadluew.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749458368/qm3vkip13mhbmtadluew.png',
    NOW(),
    NOW()
  ),
  (
    16,
    3,
    5,
    '칵테일 부스',
    'mixology 동아리',
    '중앙광장',
    '무알콜 칵테일 판매',
    '2025-06-22 15:00',
    '2025-06-22 22:00',
    '2025-06-21 10:00',
    70,
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749458391/bigwistmbbldpee4djn8.png',
    'https://res.cloudinary.com/dyn6rhsfe/image/upload/v1749458391/bigwistmbbldpee4djn8.png',
    NOW(),
    NOW()
  );

/* =============================================================
4. TIME SLOTS (2025년 6월 21-22일로 업데이트)
------------------------------------------------------------- */
INSERT INTO
  time_slot (
    id,
    booth_id,
    start_time,
    end_time,
    max_capacity,
    current_capacity,
    status,
    created_at,
    updated_at
  )
VALUES
  /* 떡볶이 부스 타임슬롯 */
  (
    1,
    1,
    '2025-06-21 11:00:00',
    '2025-06-21 12:00:00',
    10,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
  ),
  (
    2,
    1,
    '2025-06-21 12:00:00',
    '2025-06-21 13:00:00',
    10,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
  ),
  (
    3,
    1,
    '2025-06-21 13:00:00',
    '2025-06-21 14:00:00',
    10,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
  ),
  /* 포토존 타임슬롯 */
  (
    4,
    2,
    '2025-06-21 10:00:00',
    '2025-06-21 11:00:00',
    5,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
  ),
  (
    5,
    2,
    '2025-06-21 11:00:00',
    '2025-06-21 12:00:00',
    5,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
  ),
  /* 타로 부스 타임슬롯 */
  (
    6,
    3,
    '2025-06-22 12:00:00',
    '2025-06-22 13:00:00',
    3,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
  ),
  (
    7,
    3,
    '2025-06-22 13:00:00',
    '2025-06-22 14:00:00',
    3,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
  ),
  /* 버스킹 공연 타임슬롯 */
  (
    8,
    4,
    '2025-06-21 17:00:00',
    '2025-06-21 18:00:00',
    20,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
  ),
  (
    9,
    4,
    '2025-06-21 18:00:00',
    '2025-06-21 19:00:00',
    20,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
  ),
  /* 호떡 부스 타임슬롯 */
  (
    10,
    5,
    '2025-06-22 11:00:00',
    '2025-06-22 12:00:00',
    8,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
  ),
  (
    11,
    5,
    '2025-06-22 12:00:00',
    '2025-06-22 13:00:00',
    8,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
  ),
  /* 주점 타임슬롯 */
  (
    12,
    8,
    '2025-06-21 17:00:00',
    '2025-06-21 18:00:00',
    15,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
  ),
  (
    13,
    8,
    '2025-06-21 18:00:00',
    '2025-06-21 19:00:00',
    15,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
  ),
  (
    14,
    8,
    '2025-06-21 19:00:00',
    '2025-06-21 20:00:00',
    15,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
  );

/* =============================================================
5. RESERVATIONS (오늘 날짜에 맞춰 샘플 예약)
------------------------------------------------------------- */
INSERT INTO
  reservation (
    user_id,
    booth_id,
    festival_id,
    time_slot_id,
    number_of_people,
    payment_method,
    created_at,
    updated_at
  )
VALUES
  (
    1,
    1,
    1,
    2,
    2,
    'CARD',
    NOW(),
    NOW()
  ),
  (
    1,
    4,
    1,
    8,
    4,
    'BANK',
    NOW(),
    NOW()
  ),
  (
    1,
    8,
    3,
    14,
    5,
    'BANK',
    NOW(),
    NOW()
  ),
  (
    2,
    2,
    1,
    4,
    3,
    'CARD',
    NOW(),
    NOW()
  ),
  (
    2,
    5,
    2,
    10,
    2,
    'CARD',
    NOW(),
    NOW()
  ),
  (
    4,
    3,
    1,
    6,
    1,
    'CARD',
    NOW(),
    NOW()
  );

/* =============================================================
5.1. UPDATE TIME SLOT CAPACITY BASED ON RESERVATIONS
------------------------------------------------------------- */
-- 예약된 인원수만큼 타임슬롯의 current_capacity 업데이트
UPDATE time_slot ts
SET current_capacity = (
    SELECT COALESCE(SUM(r.number_of_people), 0)
    FROM reservation r
    WHERE r.time_slot_id = ts.id
)
WHERE ts.id IN (
    SELECT DISTINCT time_slot_id
    FROM reservation
    WHERE time_slot_id IS NOT NULL
);

-- 타임슬롯 상태 업데이트 (FULL 또는 AVAILABLE)
UPDATE time_slot
SET status = CASE
    WHEN current_capacity >= max_capacity THEN 'FULL'
    ELSE 'AVAILABLE'
END
WHERE id IN (
    SELECT DISTINCT time_slot_id
    FROM reservation
    WHERE time_slot_id IS NOT NULL
);

/* =============================================================
6. USER-FESTIVAL LIKES
------------------------------------------------------------- */
INSERT INTO
  user_festival_like (user_id, festival_id, created_at)
VALUES
  (1, 1, NOW()),
  (1, 3, NOW()),
  (2, 2, NOW()),
  (2, 3, NOW()),
  (4, 1, NOW()),
  (5, 2, NOW());

/* =============================================================
7. USER-BOOTH LIKES
------------------------------------------------------------- */
INSERT INTO
  user_booth_like (user_id, booth_id, created_at)
VALUES
  (1, 1, NOW()),
  (1, 4, NOW()),
  (1, 8, NOW()),
  (2, 2, NOW()),
  (2, 5, NOW()),
  (2, 9, NOW()),
  (4, 3, NOW()),
  (5, 6, NOW()),
  (5, 10, NOW());