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
(102, 'testuser2', '$2a$10$fL9aT0DRr.r.bJd9w.g5A.Iu2.n2N8.g3.g3.g3.g3.g3.g3', false, false, 'USER', '테스트유저2', 'testuser2@guthub.com', 'FEMALE', 30, 3); -- 설사형

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
(401, 1, 'dietaryFiber', 25.0, NULL),
(402, 1, 'probiotics', 10.0, NULL),
(403, 1, 'saturatedFat', NULL, 15.0),
(404, 1, 'sugar', NULL, 25.0),
(405, 1, 'refinedCarbs', NULL, 50.0),
(406, 1, 'flour', NULL, 30.0),

-- GUT-002: 변비형 (id 2)
(407, 2, 'dietaryFiber', 30.0, NULL),
(408, 2, 'probiotics', 15.0, NULL),
(409, 2, 'sugar', NULL, 20.0),
(410, 2, 'refinedCarbs', NULL, 60.0),

-- GUT-003: 설사형 (id 3)
(411, 3, 'dietaryFiber', 20.0, NULL),
(412, 3, 'saturatedFat', NULL, 10.0),
(413, 3, 'sugar', NULL, 30.0),
(414, 3, 'probiotics', 5.0, NULL),

-- GUT-004: 건강형 (id 4)
(415, 4, 'dietaryFiber', 25.0, 40.0),
(416, 4, 'probiotics', 10.0, 30.0),
(417, 4, 'saturatedFat', NULL, 20.0),
(418, 4, 'sugar', NULL, 35.0),
(419, 4, 'refinedCarbs', NULL, 70.0);

-- DailyGutHealthScore 테스트 데이터
-- testuser1 (id 101)
INSERT INTO daily_gut_health_scores (id, user_id, record_date, overall_status) VALUES
(501, 101, CURRENT_DATE(), 'GOOD'),
(502, 101, CURRENT_DATE() - 1, 'GOOD'),
(503, 101, CURRENT_DATE() - 2, 'BAD'),
(504, 101, CURRENT_DATE() - 3, 'NORMAL'),
(505, 101, CURRENT_DATE() - 4, 'GOOD'),
(506, 101, CURRENT_DATE() - 5, 'GOOD'),
(507, 101, CURRENT_DATE() - 6, 'NORMAL');

-- testuser2 (id 102)
INSERT INTO daily_gut_health_scores (id, user_id, record_date, overall_status) VALUES
(508, 102, CURRENT_DATE(), 'GOOD'),
(509, 102, CURRENT_DATE() - 1, 'BAD'),
(510, 102, CURRENT_DATE() - 2, 'NORMAL'),
(511, 102, CURRENT_DATE() - 3, 'GOOD');