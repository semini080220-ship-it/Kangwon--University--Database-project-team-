-- 삼척 관광지 시드 데이터 (분산 관광 데모용)
-- 붐비는 대표 명소(HIGH) vs 한적한 로컬 명소(LOW, local_gem=true)
INSERT INTO attraction (name, description, category, latitude, longitude, address, congestion_level, local_gem, created_at) VALUES
('쏠비치 삼척',            '대표 해안 리조트',          'COAST',        37.4600, 129.1700, '강원 삼척시 수로부인길', 'HIGH',   FALSE, CURRENT_TIMESTAMP),
('장호항',                '한국의 나폴리, 투명카약',     'COAST',        37.3000, 129.3100, '강원 삼척시 근덕면',    'HIGH',   FALSE, CURRENT_TIMESTAMP),
('초곡용굴촛대바위길',      '한적한 해안 비경 산책로',     'COAST',        37.3600, 129.2600, '강원 삼척시 근덕면',    'LOW',    TRUE,  CURRENT_TIMESTAMP),
('덕봉산 해안생태탐방로',   '숨은 해안 생태길',          'COAST',        37.3300, 129.2800, '강원 삼척시 근덕면',    'LOW',    TRUE,  CURRENT_TIMESTAMP),
('무건리 이끼폭포',        '신비로운 산간 이끼폭포',      'MOUNTAIN',     37.2000, 129.0500, '강원 삼척시 도계읍',    'LOW',    TRUE,  CURRENT_TIMESTAMP),
('환선굴',                '국내 최대 석회동굴',         'CAVE',         37.2700, 129.0500, '강원 삼척시 신기면',    'MEDIUM', FALSE, CURRENT_TIMESTAMP),
('죽서루',                '관동팔경, 보물',            'HISTORY',      37.4400, 129.1600, '강원 삼척시 죽서루길',  'MEDIUM', FALSE, CURRENT_TIMESTAMP),
('도계 폐광촌 문화공간',    '폐광지 재생 문화공간',       'MINE_CULTURE', 37.2300, 129.0600, '강원 삼척시 도계읍',    'LOW',    TRUE,  CURRENT_TIMESTAMP);

-- 방문자 댓글 시드 (로그인 없이 닉네임으로 남기는 가벼운 후기)
-- attraction_id 는 위 INSERT 순서대로 1=쏠비치 … 3=초곡 … 4=덕봉산 … 5=무건리
INSERT INTO comment (attraction_id, author, content, created_at) VALUES
(3, '바다러버',   '사람이 적어서 파도 소리만 들렸어요. 노을이 정말 예술입니다.', CURRENT_TIMESTAMP),
(3, '주말산책',   '촛대바위까지 천천히 걷기 좋아요. 한적해서 강추!',           CURRENT_TIMESTAMP),
(4, '생태탐방러', '새가 사람보다 많아요. 조용히 걷기 딱 좋습니다.',             CURRENT_TIMESTAMP),
(5, '숲멍전문가', '이끼폭포 공기부터 달라요. 깊은 숲의 정적이 좋았어요.',       CURRENT_TIMESTAMP),
(1, '여름휴가',   '리조트는 좋은데 성수기엔 너무 붐벼요. 대안 추천 기능 굿.',   CURRENT_TIMESTAMP);
