-- GutType 테스트 데이터
INSERT INTO gut_types (id, name, code, description, image_url) VALUES
(1, '가스,복부팽만형', 'GUT-001', '평소에 가스가 자주 차고 장이 예민하게 반응하는 타입입니다.', 'http://example.com/images/gut-001.png'),
(2, '변비형', 'GUT-002', '화장실을 가는 주기가 길고, 변을 볼 때 어려움을 겪는 타입입니다.', 'http://example.com/images/gut-002.png'),
(3, '설사형', 'GUT-003', '장을 자극하는 음식을 먹으면 바로 신호가 오는 등 잦은 설사를 하는 타입입니다.', 'http://example.com/images/gut-003.png'),
(4, '건강형', 'GUT-004', '특별한 불편함 없이 장 건강을 잘 유지하고 있는 타입입니다.', 'http://example.com/images/gut-004.png');

-- 사용자 데이터 (비밀번호 암호화됨)
INSERT INTO users (id, username, password, is_lock, is_social, role_type, nickname, email, gender, age_range, gut_type_id) VALUES
(101, 'testuser', '$2a$10$fL9aT0DRr.r.bJd9w.g5A.Iu2.n2N8.g3.g3.g3.g3.g3.g3', false, false, 'USER', '테스트유저1', 'testuser@guthub.com', 'MALE', 20, 2),
(102, 'testuser2', '$2a$10$fL9aT0DRr.r.bJd9w.g5A.Iu2.n2N8.g3.g3.g3.g3.g3.g3', false, false, 'USER', '테스트유저2', 'testuser2@guthub.com', 'FEMALE', 30, 3);

-- 음식(Food) 테스트 데이터
INSERT INTO foods (id, name, calories, dietary_fiber, probiotics, saturated_fat, sugar, refined_carbs, is_flour_based, flour) VALUES
(201, '김치찌개', 250.0, 3.5, 0.1, 8.2, 2.1, 5.0, false, 0.0),
(202, '된장찌개', 220.0, 4.1, 0.2, 5.5, 1.8, 4.0, false, 0.0),
(203, '피자', 550.0, 2.0, 0.0, 15.0, 8.0, 40.0, true, 35.0),
(204, '치킨', 600.0, 1.5, 0.0, 18.0, 5.0, 30.0, true, 25.0),
(205, '샐러드', 150.0, 8.0, 0.0, 1.0, 3.0, 10.0, false, 0.0),
(206, '요거트', 100.0, 0.5, 20.0, 2.0, 15.0, 0.0, false, 0.0),
(207, '라면', 500.0, 2.5, 0.0, 9.0, 4.0, 70.0, true, 60.0);

-- 식단 기록(DietLog) 테스트 데이터
INSERT INTO diet_logs (id, user_id, food_id, log_date, amount, meal_type) VALUES
(301, 101, 201, CURRENT_DATE(), 1.0, 'BREAKFAST'),
(302, 101, 205, CURRENT_DATE(), 1.0, 'LUNCH'),
(303, 101, 202, CURRENT_DATE(), 1.0, 'DINNER'),
(304, 101, 206, CURRENT_DATE() - 1, 1.0, 'BREAKFAST'),
(305, 101, 205, CURRENT_DATE() - 1, 1.0, 'LUNCH'),
(306, 101, 207, CURRENT_DATE() - 2, 1.0, 'DINNER'),
(307, 101, 203, CURRENT_DATE() - 2, 2.0, 'DINNER'),
(308, 102, 205, CURRENT_DATE(), 1.0, 'BREAKFAST'),
(309, 102, 202, CURRENT_DATE(), 1.0, 'LUNCH');

-- GutNutrientStandard 테스트 데이터 (생략 - 이전과 동일)

-- DailyGutHealthScore 테스트 데이터
INSERT INTO daily_gut_health_scores (id, user_id, record_date, overall_status) VALUES
(501, 101, CURRENT_DATE(), 'GOOD'),
(502, 101, CURRENT_DATE() - 1, 'GOOD'),
(503, 101, CURRENT_DATE() - 2, 'BAD'),
(504, 101, CURRENT_DATE() - 3, 'NORMAL'),
(505, 101, CURRENT_DATE() - 4, 'GOOD'),
(506, 102, CURRENT_DATE(), 'GOOD'),
(507, 102, CURRENT_DATE() - 1, 'BAD');
