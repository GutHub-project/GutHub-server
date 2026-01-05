-- GutType 테스트 데이터
INSERT INTO gut_types (id, name, code, description, image_url) VALUES
(1, '가스,복부팽만형', 'GUT-001', '평소에 가스가 자주 차고 장이 예민하게 반응하는 타입입니다.', 'http://example.com/images/gut-001.png'),
(2, '변비형', 'GUT-002', '화장실을 가는 주기가 길고, 변을 볼 때 어려움을 겪는 타입입니다.', 'http://example.com/images/gut-002.png'),
(3, '설사형', 'GUT-003', '장을 자극하는 음식을 먹으면 바로 신호가 오는 등 잦은 설사를 하는 타입입니다.', 'http://example.com/images/gut-003.png'),
(4, '건강형', 'GUT-004', '특별한 불편함 없이 장 건강을 잘 유지하고 있는 타입입니다.', 'http://example.com/images/gut-004.png');

-- 사용자 데이터 (비밀번호 암호화됨)
-- 비밀번호는 모두 "password"를 BCrypt로 암호화한 값입니다.
INSERT INTO users (id, username, password, is_lock, is_social, role_type, nickname, email, gender, age_range, gut_type_id) VALUES
(101, 'testuser1', '$2a$10$fL9aT0DRr.r.bJd9w.g5A.Iu2.n2N8.g3.g3.g3.g3.g3.g3', false, false, 'USER', '테스트유저1', 'testuser1@guthub.com', 'MALE', 20, 2), -- 변비형
(102, 'testuser2', '$2a$10$fL9aT0DRr.r.bJd9w.g5A.Iu2.n2N8.g3.g3.g3.g3.g3.g3', false, false, 'USER', '테스트유저2', 'testuser2@guthub.com', 'FEMALE', 30, 3), -- 설사형
(103, 'testuser3', '$2a$10$fL9aT13Rr.r.bJd9w.g5A.Iu2.n2N8.g3.g3.g3.g3.g3.g3', false, false, 'TEMP', '테스트유저3', 'testuser3@guthub.com', 'FEMALE', 30, 3); -- 설사형

-- 음식(Food) 테스트 데이터
INSERT INTO foods (id, name, calories, dietary_fiber, probiotics, saturated_fat, sugar, refined_carbs, is_flour_based, flour) VALUES
(201, '김치찌개', 250.0, 3.5, 0.1, 8.2, 2.1, 5.0, false, 0.0),
(202, '된장찌개', 220.0, 4.1, 0.2, 5.5, 1.8, 4.0, false, 0.0),
(203, '피자', 550.0, 2.0, 0.0, 15.0, 8.0, 40.0, true, 35.0),
(204, '치킨', 600.0, 1.5, 0.0, 18.0, 5.0, 30.0, true, 25.0),
(205, '샐러드', 150.0, 8.0, 0.0, 1.0, 3.0, 10.0, false, 0.0),
(206, '요거트', 100.0, 0.5, 20.0, 2.0, 15.0, 0.0, false, 0.0),
(207, '라면', 500.0, 2.5, 0.0, 9.0, 4.0, 70.0, true, 60.0),
(208, '현미밥', 200.0, 5.0, 0.0, 0.5, 0.5, 2.0, false, 0.0),
(209, '고구마', 120.0, 4.0, 0.0, 0.2, 6.0, 1.0, false, 0.0),
(210, '사과', 80.0, 3.0, 0.0, 0.1, 10.0, 0.0, false, 0.0),
(211, '바나나', 100.0, 3.0, 0.0, 0.3, 12.0, 0.0, false, 0.0),
(212, '두부', 80.0, 1.0, 0.0, 1.0, 1.0, 0.0, false, 0.0),
(213, '계란후라이', 90.0, 0.0, 0.0, 3.0, 0.5, 0.0, false, 0.0);

-- 식단 기록(DietLog) 테스트 데이터
-- testuser1 (id 101)
INSERT INTO diet_logs (id, user_id, food_id, log_date, amount, meal_type) VALUES
(301, 101, 201, CURRENT_DATE(), 1.0, 'BREAKFAST'), -- 김치찌개
(302, 101, 208, CURRENT_DATE(), 1.0, 'BREAKFAST'), -- 현미밥
(303, 101, 205, CURRENT_DATE(), 1.0, 'LUNCH'),     -- 샐러드
(304, 101, 202, CURRENT_DATE(), 1.0, 'DINNER'),    -- 된장찌개
(305, 101, 208, CURRENT_DATE(), 1.0, 'DINNER'),    -- 현미밥
(306, 101, 206, CURRENT_DATE() - 1, 1.0, 'BREAKFAST'), -- 요거트
(307, 101, 209, CURRENT_DATE() - 1, 2.0, 'LUNCH'),     -- 고구마
(308, 101, 204, CURRENT_DATE() - 1, 0.5, 'DINNER'),    -- 치킨 (반마리)
(309, 101, 207, CURRENT_DATE() - 2, 1.0, 'DINNER'),    -- 라면
(310, 101, 210, CURRENT_DATE() - 2, 1.0, 'SNACK'),     -- 사과
(311, 101, 211, CURRENT_DATE() - 3, 1.0, 'BREAKFAST'), -- 바나나
(312, 101, 212, CURRENT_DATE() - 3, 1.0, 'LUNCH');     -- 두부

-- testuser2 (id 102)
INSERT INTO diet_logs (id, user_id, food_id, log_date, amount, meal_type) VALUES
(313, 102, 205, CURRENT_DATE(), 1.0, 'BREAKFAST'), -- 샐러드
(314, 102, 206, CURRENT_DATE(), 1.0, 'BREAKFAST'), -- 요거트
(315, 102, 202, CURRENT_DATE(), 1.0, 'LUNCH'),     -- 된장찌개
(316, 102, 208, CURRENT_DATE(), 1.0, 'LUNCH'),     -- 현미밥
(317, 102, 201, CURRENT_DATE() - 1, 1.0, 'DINNER'),    -- 김치찌개
(318, 102, 204, CURRENT_DATE() - 1, 1.0, 'DINNER'),    -- 치킨
(319, 102, 213, CURRENT_DATE() - 2, 2.0, 'BREAKFAST'), -- 계란후라이
(320, 102, 209, CURRENT_DATE() - 2, 1.0, 'LUNCH');     -- 고구마

-- GutNutrientStandard 테스트 데이터
INSERT INTO gut_nutrient_standards (id, gut_type_id, nutrient_name, min_limit, max_limit) VALUES
-- GUT-001: 가스,복부팽만형 (id 1)
(401, 1, 'DIETARY_FIBER', 25.0, NULL),
(402, 1, 'PROBIOTICS', 10.0, NULL),
(403, 1, 'SATURATED_FAT', NULL, 15.0),
(404, 1, 'SUGAR', NULL, 25.0),
(405, 1, 'REFINED_CARBS', NULL, 50.0),
(406, 1, 'FLOUR', NULL, 30.0),

-- GUT-002: 변비형 (id 2)
(407, 2, 'DIETARY_FIBER', 30.0, NULL),
(408, 2, 'PROBIOTICS', 15.0, NULL),
(409, 2, 'SUGAR', NULL, 20.0),
(410, 2, 'REFINED_CARBS', NULL, 60.0),

-- GUT-003: 설사형 (id 3)
(411, 3, 'DIETARY_FIBER', 20.0, NULL),
(412, 3, 'SATURATED_FAT', NULL, 10.0),
(413, 3, 'SUGAR', NULL, 30.0),
(414, 3, 'PROBIOTICS', 5.0, NULL),

-- GUT-004: 건강형 (id 4)
(415, 4, 'DIETARY_FIBER', 25.0, 40.0),
(416, 4, 'PROBIOTICS', 10.0, 30.0),
(417, 4, 'SATURATED_FAT', NULL, 20.0),
(418, 4, 'SUGAR', NULL, 35.0),
(419, 4, 'REFINED_CARBS', NULL, 70.0);

-- DailyGutHealthScore 테스트 데이터
-- testuser1 (id 101)
INSERT INTO daily_gut_health_scores (id, user_id, record_date, overall_status, bad_count, violation_reason, updated_at) VALUES
(501, 101, CURRENT_DATE(), 'GOOD', 0, NULL, CURRENT_TIMESTAMP()),
(502, 101, CURRENT_DATE() - 1, 'GOOD', 0, NULL, CURRENT_TIMESTAMP()),
(503, 101, CURRENT_DATE() - 2, 'BAD', 5, '포화지방, 정제탄수화물, 설탕, 밀가루, 식이섬유 기준 초과', CURRENT_TIMESTAMP()),
(504, 101, CURRENT_DATE() - 3, 'NORMAL', 2, '식이섬유 부족, 설탕 초과', CURRENT_TIMESTAMP()),
(505, 101, CURRENT_DATE() - 4, 'GOOD', 0, NULL, CURRENT_TIMESTAMP()),
(506, 101, CURRENT_DATE() - 5, 'GOOD', 0, NULL, CURRENT_TIMESTAMP()),
(507, 101, CURRENT_DATE() - 6, 'NORMAL', 1, '프로바이오틱스 부족', CURRENT_TIMESTAMP());

-- testuser2 (id 102)
INSERT INTO daily_gut_health_scores (id, user_id, record_date, overall_status, bad_count, violation_reason, updated_at) VALUES
(508, 102, CURRENT_DATE(), 'GOOD', 0, NULL, CURRENT_TIMESTAMP()),
(509, 102, CURRENT_DATE() - 1, 'BAD', 4, '포화지방, 정제탄수화물, 밀가루, 식이섬유 기준 초과', CURRENT_TIMESTAMP()),
(510, 102, CURRENT_DATE() - 2, 'NORMAL', 1, '설탕 초과', CURRENT_TIMESTAMP()),
(511, 102, CURRENT_DATE() - 3, 'GOOD', 0, NULL, CURRENT_TIMESTAMP());

-- Ingredient 테스트 데이터
INSERT INTO ingredients (id, name) VALUES
(601, '비타민C'),
(602, '비타민D'),
(603, '프로바이오틱스'),
(604, '아연'),
(605, '마그네슘'),
(606, '오메가3'),
(607, '밀크씨슬'),
(608, '루테인');

-- Supplement 테스트 데이터
INSERT INTO supplements (id, name, brand, price, image_url, description, purchase_url, capacity, review_count, rating_average) VALUES
(701, '슈퍼 유산균 골드', '건강나라', 35000, 'http://example.com/supplements/701.png', '장 건강에 도움을 줄 수 있는 100억 유산균', 'http://shop.com/701', 60, 2, 4.5),
(702, '데일리 비타민C 1000', '비타민월드', 15000, 'http://example.com/supplements/702.png', '활력 넘치는 하루를 위한 고함량 비타민C', 'http://shop.com/702', 120, 1, 5.0),
(703, '눈 건강 루테인 지아잔틴', '아이케어', 28000, 'http://example.com/supplements/703.png', '침침한 눈을 밝게, 황반색소 밀도 유지', 'http://shop.com/703', 30, 0, 0.0),
(704, '간편한 밀크씨슬', '리버가드', 22000, 'http://example.com/supplements/704.png', '지친 간을 위한 하루 한 알', 'http://shop.com/704', 60, 0, 0.0),
(705, '프리미엄 오메가3', '오션라이프', 45000, 'http://example.com/supplements/705.png', '혈행 개선과 건조한 눈 개선에 도움', 'http://shop.com/705', 90, 0, 0.0);

-- SupplementIngredient 연결 데이터
INSERT INTO supplement_ingredients (id, supplement_id, ingredient_id) VALUES
(801, 701, 603), -- 슈퍼 유산균 골드 - 프로바이오틱스
(802, 701, 604), -- 슈퍼 유산균 골드 - 아연
(803, 702, 601), -- 데일리 비타민C - 비타민C
(804, 703, 608), -- 눈 건강 루테인 - 루테인
(805, 703, 601), -- 눈 건강 루테인 - 비타민C (부원료)
(806, 704, 607), -- 간편한 밀크씨슬 - 밀크씨슬
(807, 704, 602), -- 간편한 밀크씨슬 - 비타민D
(808, 705, 606), -- 프리미엄 오메가3 - 오메가3
(809, 705, 602); -- 프리미엄 오메가3 - 비타민D

-- Review 테스트 데이터
INSERT INTO reviews (id, user_id, supplement_id, rating, delivery_rating, content, created_at, updated_at) VALUES
(901, 101, 701, 5, 5, '배송도 빠르고 효과도 좋은 것 같아요. 화장실 가기 편해졌습니다.', CURRENT_TIMESTAMP() - INTERVAL '2' DAY, CURRENT_TIMESTAMP()),
(902, 102, 701, 4, 4, '가격 대비 괜찮은 제품입니다. 꾸준히 먹어보려구요.', CURRENT_TIMESTAMP() - INTERVAL '1' DAY, CURRENT_TIMESTAMP()),
(903, 101, 702, 5, 5, '피로가 좀 덜한 느낌이에요. 알약 크기도 적당합니다.', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
