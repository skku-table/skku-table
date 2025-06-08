-- Development Environment Seed Data
-- Note: Version 1000+ is used for seed data to avoid conflicts with schema migrations
-- Insert test users
-- password: password123
INSERT INTO
  user (
    name,
    email,
    password,
    role,
    created_at,
    updated_at
  )
VALUES
  (
    '김철수',
    'user1@skku.edu',
    '$2a$10$fihxE1rSIi64vEXARNikYu7vvrFt6OS.PyLqMp4B31bJzQM8Z4chO',
    'USER',
    NOW(),
    NOW()
  ), -- password: password123
  (
    '이영희',
    'user2@skku.edu',
    '$2a$10$fihxE1rSIi64vEXARNikYu7vvrFt6OS.PyLqMp4B31bJzQM8Z4chO',
    'USER',
    NOW(),
    NOW()
  ),
  (
    '박관리',
    'admin@skku.edu',
    '$2a$10$fihxE1rSIi64vEXARNikYu7vvrFt6OS.PyLqMp4B31bJzQM8Z4chO',
    'ADMIN',
    NOW(),
    NOW()
  ),
  (
    '정부스',
    'host1@skku.edu',
    '$2a$10$fihxE1rSIi64vEXARNikYu7vvrFt6OS.PyLqMp4B31bJzQM8Z4chO',
    'HOST',
    NOW(),
    NOW()
  ),
  (
    '최부스',
    'host2@skku.edu',
    '$2a$10$fihxE1rSIi64vEXARNikYu7vvrFt6OS.PyLqMp4B31bJzQM8Z4chO',
    'HOST',
    NOW(),
    NOW()
  );

-- Insert festivals
INSERT INTO
  festival (
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
    '2024 성균관대 봄 축제',
    '성균관대학교 봄 축제입니다! 다양한 공연과 부스가 준비되어 있습니다.',
    '성균관대학교 자연과학캠퍼스',
    '2024-05-15',
    '2024-05-17',
    'https://example.com/spring-festival-poster.jpg',
    'https://example.com/spring-festival-map.jpg',
    150,
    NOW(),
    NOW()
  ),
  (
    '2024 성균관대 가을 축제',
    '성균관대학교 가을 축제! 풍성한 먹거리와 볼거리가 가득합니다.',
    '성균관대학교 인문사회과학캠퍼스',
    '2024-10-20',
    '2024-10-22',
    'https://example.com/fall-festival-poster.jpg',
    'https://example.com/fall-festival-map.jpg',
    200,
    NOW(),
    NOW()
  ),
  (
    '2024 대동제',
    '학생회 주최 대동제! 모든 성균인이 하나되는 축제입니다.',
    '성균관대학교 자연과학캠퍼스',
    '2024-11-10',
    '2024-11-12',
    'https://example.com/daedong-poster.jpg',
    'https://example.com/daedong-map.jpg',
    300,
    NOW(),
    NOW()
  );

-- Insert booths
INSERT INTO
  booth (
    festival_id,
    name,
    host,
    location,
    description,
    start_date_time,
    end_date_time,
    like_count,
    poster_image_url,
    event_image_url,
    created_at,
    updated_at
  )
VALUES
  -- 봄 축제 부스들
  (
    1,
    '떡볶이 부스',
    '요리 동아리',
    '학생회관 앞',
    '매콤달콤한 떡볶이를 판매합니다!',
    '2024-05-15 11:00:00',
    '2024-05-15 18:00:00',
    50,
    'https://example.com/tteokbokki-poster.jpg',
    'https://example.com/tteokbokki-event.jpg',
    NOW(),
    NOW()
  ),
  (
    1,
    '포토존',
    '사진 동아리',
    '중앙광장',
    '예쁜 사진을 찍어드립니다!',
    '2024-05-15 10:00:00',
    '2024-05-17 20:00:00',
    80,
    'https://example.com/photo-poster.jpg',
    'https://example.com/photo-event.jpg',
    NOW(),
    NOW()
  ),
  (
    1,
    '타로 부스',
    '심리학과',
    '도서관 앞',
    '타로로 당신의 운세를 봐드립니다',
    '2024-05-16 12:00:00',
    '2024-05-16 17:00:00',
    30,
    'https://example.com/tarot-poster.jpg',
    'https://example.com/tarot-event.jpg',
    NOW(),
    NOW()
  ),
  (
    1,
    '버스킹 공연',
    '음악 동아리',
    '야외 무대',
    '다양한 장르의 음악 공연',
    '2024-05-15 17:00:00',
    '2024-05-17 21:00:00',
    100,
    'https://example.com/busking-poster.jpg',
    'https://example.com/busking-event.jpg',
    NOW(),
    NOW()
  ),
  -- 가을 축제 부스들
  (
    2,
    '호떡 부스',
    '봉사 동아리',
    '정문 앞',
    '따뜻한 호떡을 판매합니다',
    '2024-10-20 11:00:00',
    '2024-10-22 18:00:00',
    60,
    'https://example.com/hotteok-poster.jpg',
    'https://example.com/hotteok-event.jpg',
    NOW(),
    NOW()
  ),
  (
    2,
    '플리마켓',
    '경영학과',
    '중앙도서관 앞',
    '다양한 수공예품 판매',
    '2024-10-20 10:00:00',
    '2024-10-22 19:00:00',
    90,
    'https://example.com/flea-poster.jpg',
    'https://example.com/flea-event.jpg',
    NOW(),
    NOW()
  ),
  (
    2,
    '미니게임',
    '컴퓨터학과',
    '공학관 앞',
    '재미있는 미니게임과 상품!',
    '2024-10-21 12:00:00',
    '2024-10-21 18:00:00',
    40,
    'https://example.com/game-poster.jpg',
    'https://example.com/game-event.jpg',
    NOW(),
    NOW()
  ),
  -- 대동제 부스들
  (
    3,
    '주점',
    '경제학과',
    '학생회관 3층',
    '다양한 안주와 음료 판매',
    '2024-11-10 17:00:00',
    '2024-11-12 23:00:00',
    150,
    'https://example.com/bar-poster.jpg',
    'https://example.com/bar-event.jpg',
    NOW(),
    NOW()
  ),
  (
    3,
    '귀신의 집',
    '연극 동아리',
    '체육관',
    '무서운 귀신의 집 체험',
    '2024-11-10 15:00:00',
    '2024-11-12 22:00:00',
    120,
    'https://example.com/ghost-poster.jpg',
    'https://example.com/ghost-event.jpg',
    NOW(),
    NOW()
  ),
  (
    3,
    '푸드트럭',
    '학생회',
    '중앙광장',
    '다양한 세계 음식',
    '2024-11-10 11:00:00',
    '2024-11-12 21:00:00',
    200,
    'https://example.com/foodtruck-poster.jpg',
    'https://example.com/foodtruck-event.jpg',
    NOW(),
    NOW()
  );

-- Insert reservations
INSERT INTO
  reservation (
    user_id,
    booth_id,
    festival_id,
    reservation_time,
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
    '2024-05-15 12:30:00',
    2,
    'CARD',
    NOW(),
    NOW()
  ),
  (
    1,
    4,
    1,
    '2024-05-15 18:00:00',
    4,
    'BANK',
    NOW(),
    NOW()
  ),
  (
    2,
    2,
    1,
    '2024-05-16 14:00:00',
    3,
    'CARD',
    NOW(),
    NOW()
  ),
  (
    2,
    5,
    2,
    '2024-10-20 13:00:00',
    2,
    'CARD',
    NOW(),
    NOW()
  ),
  (
    1,
    8,
    3,
    '2024-11-10 19:00:00',
    5,
    'BANK',
    NOW(),
    NOW()
  ),
  (
    4,
    3,
    1,
    '2024-05-16 15:00:00',
    1,
    'CARD',
    NOW(),
    NOW()
  );

-- Insert user festival likes
INSERT INTO
  user_festival_like (user_id, festival_id, created_at)
VALUES
  (1, 1, NOW()),
  (1, 3, NOW()),
  (2, 2, NOW()),
  (2, 3, NOW()),
  (4, 1, NOW()),
  (5, 2, NOW());

-- Insert user booth likes
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

-- Add some sample data for testing pagination
INSERT INTO
  booth (
    festival_id,
    name,
    host,
    location,
    description,
    start_date_time,
    end_date_time,
    like_count,
    poster_image_url,
    event_image_url,
    created_at,
    updated_at
  )
VALUES
  (
    1,
    '솜사탕 부스',
    '화학과',
    '과학관 앞',
    '달콤한 솜사탕 판매',
    '2024-05-16 11:00:00',
    '2024-05-16 17:00:00',
    25,
    NULL,
    NULL,
    NOW(),
    NOW()
  ),
  (
    1,
    '네일아트',
    '미술학과',
    '예술관 로비',
    '예쁜 네일아트 서비스',
    '2024-05-15 12:00:00',
    '2024-05-17 18:00:00',
    35,
    NULL,
    NULL,
    NOW(),
    NOW()
  ),
  (
    2,
    '커피 부스',
    '바리스타 동아리',
    '경영관 앞',
    '직접 내린 커피 판매',
    '2024-10-20 09:00:00',
    '2024-10-22 18:00:00',
    45,
    NULL,
    NULL,
    NOW(),
    NOW()
  ),
  (
    2,
    '페이스페인팅',
    '디자인학과',
    '미술관 앞',
    '다양한 페이스페인팅',
    '2024-10-21 11:00:00',
    '2024-10-21 17:00:00',
    20,
    NULL,
    NULL,
    NOW(),
    NOW()
  ),
  (
    3,
    '보드게임 카페',
    '보드게임 동아리',
    '학생회관 2층',
    '다양한 보드게임 체험',
    '2024-11-10 13:00:00',
    '2024-11-12 20:00:00',
    55,
    NULL,
    NULL,
    NOW(),
    NOW()
  ),
  (
    3,
    '칵테일 부스',
    'mixology 동아리',
    '중앙광장',
    '무알콜 칵테일 판매',
    '2024-11-11 15:00:00',
    '2024-11-11 22:00:00',
    70,
    NULL,
    NULL,
    NOW(),
    NOW()
  );