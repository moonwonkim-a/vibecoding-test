-- ============================================================
-- Library Mock Data — src/main/resources/data.sql
-- 앱 시작 시 자동 실행 (INSERT IGNORE — 멱등성 보장)
-- ============================================================
-- DB 초기화가 필요한 경우 아래 순서로 실행 후 앱 재시작:
--   SET FOREIGN_KEY_CHECKS=0;
--   TRUNCATE library_rent_records;
--   TRUNCATE library_book_inventory;
--   DELETE FROM library_book_info;
--   DELETE FROM library_users;
--   DELETE FROM library_admins;
--   SET FOREIGN_KEY_CHECKS=1;
-- ============================================================

-- ① Charset 보정 (멱등)
ALTER TABLE library_blacklist_reasons
  MODIFY COLUMN reason_label  VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  MODIFY COLUMN description    VARCHAR(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE library_book_info
  MODIFY COLUMN title      VARCHAR(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  MODIFY COLUMN author     VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  MODIFY COLUMN publisher  VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  MODIFY COLUMN category   VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL;
ALTER TABLE library_users
  MODIFY COLUMN user_name      VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  MODIFY COLUMN blacklist_memo VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE library_rent_records
  MODIFY COLUMN user_name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL;
ALTER TABLE library_admins
  MODIFY COLUMN admin_name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL;

-- ② 테스트 상태 완전 리셋 (서버 재시작 시 항상 깨끗한 상태로 초기화)
SET FOREIGN_KEY_CHECKS = 0;

-- 모든 대여 기록 삭제 (초기 16건 포함 — 아래 ⑦에서 재삽입)
DELETE FROM library_rent_records;

-- 이용자 전체 삭제 후 재삽입 — 이름 인코딩 불일치 방지
DELETE FROM library_users;

-- 테스트 추가 재고 삭제 (inventory_id > 120)
DELETE FROM library_book_inventory WHERE inventory_id > 120;

-- 테스트 추가 도서 삭제 (목데이터 40권 외)
DELETE FROM library_book_info WHERE isbn NOT IN (
  '9780000000000','9780000000001','9780000000002','9780000000003','9780000000004',
  '9780000000005','9780000000006','9780000000007','9780000000008','9780000000009',
  '9780000000010','9780000000011','9780000000012','9780000000013','9780000000014',
  '9780000000015','9780000000016','9780000000017','9780000000018','9780000000019',
  '9780000000020','9780000000021','9780000000022','9780000000023','9780000000024',
  '9780000000025','9780000000026','9780000000027','9780000000028','9780000000029',
  '9780000000030','9780000000031','9780000000032','9780000000033','9780000000034',
  '9780000000035','9780000000036','9780000000037','9780000000038','9780000000039'
);

-- 재고(1~120) 상태 완전 초기화 — 대여 중인 4건(inv 1,2,3,6)만 available=0, 나머지 available=1
UPDATE library_book_inventory SET available = 1, rent_date = NULL, del_yn = 'N'
WHERE inventory_id BETWEEN 1 AND 120;
UPDATE library_book_inventory SET available = 0, rent_date = CURDATE() - INTERVAL 4  DAY WHERE inventory_id = 1;
UPDATE library_book_inventory SET available = 0, rent_date = CURDATE() - INTERVAL 2  DAY WHERE inventory_id = 2;
UPDATE library_book_inventory SET available = 0, rent_date = CURDATE() - INTERVAL 7  DAY WHERE inventory_id = 3;
UPDATE library_book_inventory SET available = 0, rent_date = CURDATE() - INTERVAL 30 DAY WHERE inventory_id = 6;

-- 도서 del_yn 초기화 (소프트 삭제된 도서 복구)
UPDATE library_book_info SET del_yn = 'N';

SET FOREIGN_KEY_CHECKS = 1;

-- ③ 불량 지정 사유 (6건)
INSERT IGNORE INTO library_blacklist_reasons (reason_code, reason_label, description) VALUES
('OVERDUE_REPEAT', '반복 연체', '3회 이상 반납 기한 초과 이력'),
('OVERDUE_LONG',   '장기 연체', '반납 기한 30일 이상 초과'),
('LOST',           '도서 분실', '대여 도서 분실 처리'),
('DAMAGE',         '도서 훼손', '도서 심각 훼손'),
('FRAUD',          '부정 대여', '타인 명의 도용 등 부정 대여 시도'),
('ETC',            '기타',      '기타 사유');

-- ③ 도서 마스터 (40권)
-- 카테고리 순환: {프로그래밍, 데이터, 소설, 역사, 자기계발, 디자인, 과학, 경제}
-- 저자: 저자(i%10+1), 출판사: 출판사(i%5+1), 가격: 10000+i*500, rent_count: (i%18)+1
INSERT IGNORE INTO library_book_info (isbn, title, author, publisher, category, price, rent_count) VALUES
('9780000000000', '샘플 도서 1',  '저자 1',  '출판사 1', '프로그래밍', 10000, 1),
('9780000000001', '샘플 도서 2',  '저자 2',  '출판사 2', '데이터',     10500, 2),
('9780000000002', '샘플 도서 3',  '저자 3',  '출판사 3', '소설',       11000, 3),
('9780000000003', '샘플 도서 4',  '저자 4',  '출판사 4', '역사',       11500, 4),
('9780000000004', '샘플 도서 5',  '저자 5',  '출판사 5', '자기계발',   12000, 5),
('9780000000005', '샘플 도서 6',  '저자 6',  '출판사 1', '디자인',     12500, 6),
('9780000000006', '샘플 도서 7',  '저자 7',  '출판사 2', '과학',       13000, 7),
('9780000000007', '샘플 도서 8',  '저자 8',  '출판사 3', '경제',       13500, 8),
('9780000000008', '샘플 도서 9',  '저자 9',  '출판사 4', '프로그래밍', 14000, 9),
('9780000000009', '샘플 도서 10', '저자 10', '출판사 5', '데이터',     14500, 10),
('9780000000010', '샘플 도서 11', '저자 1',  '출판사 1', '소설',       15000, 11),
('9780000000011', '샘플 도서 12', '저자 2',  '출판사 2', '역사',       15500, 12),
('9780000000012', '샘플 도서 13', '저자 3',  '출판사 3', '자기계발',   16000, 13),
('9780000000013', '샘플 도서 14', '저자 4',  '출판사 4', '디자인',     16500, 14),
('9780000000014', '샘플 도서 15', '저자 5',  '출판사 5', '과학',       17000, 15),
('9780000000015', '샘플 도서 16', '저자 6',  '출판사 1', '경제',       17500, 16),
('9780000000016', '샘플 도서 17', '저자 7',  '출판사 2', '프로그래밍', 18000, 17),
('9780000000017', '샘플 도서 18', '저자 8',  '출판사 3', '데이터',     18500, 18),
('9780000000018', '샘플 도서 19', '저자 9',  '출판사 4', '소설',       19000, 1),
('9780000000019', '샘플 도서 20', '저자 10', '출판사 5', '역사',       19500, 2),
('9780000000020', '샘플 도서 21', '저자 1',  '출판사 1', '자기계발',   20000, 3),
('9780000000021', '샘플 도서 22', '저자 2',  '출판사 2', '디자인',     20500, 4),
('9780000000022', '샘플 도서 23', '저자 3',  '출판사 3', '과학',       21000, 5),
('9780000000023', '샘플 도서 24', '저자 4',  '출판사 4', '경제',       21500, 6),
('9780000000024', '샘플 도서 25', '저자 5',  '출판사 5', '프로그래밍', 22000, 7),
('9780000000025', '샘플 도서 26', '저자 6',  '출판사 1', '데이터',     22500, 8),
('9780000000026', '샘플 도서 27', '저자 7',  '출판사 2', '소설',       23000, 9),
('9780000000027', '샘플 도서 28', '저자 8',  '출판사 3', '역사',       23500, 10),
('9780000000028', '샘플 도서 29', '저자 9',  '출판사 4', '자기계발',   24000, 11),
('9780000000029', '샘플 도서 30', '저자 10', '출판사 5', '디자인',     24500, 12),
('9780000000030', '샘플 도서 31', '저자 1',  '출판사 1', '과학',       25000, 13),
('9780000000031', '샘플 도서 32', '저자 2',  '출판사 2', '경제',       25500, 14),
('9780000000032', '샘플 도서 33', '저자 3',  '출판사 3', '프로그래밍', 26000, 15),
('9780000000033', '샘플 도서 34', '저자 4',  '출판사 4', '데이터',     26500, 16),
('9780000000034', '샘플 도서 35', '저자 5',  '출판사 5', '소설',       27000, 17),
('9780000000035', '샘플 도서 36', '저자 6',  '출판사 1', '역사',       27500, 18),
('9780000000036', '샘플 도서 37', '저자 7',  '출판사 2', '자기계발',   28000, 1),
('9780000000037', '샘플 도서 38', '저자 8',  '출판사 3', '디자인',     28500, 2),
('9780000000038', '샘플 도서 39', '저자 9',  '출판사 4', '과학',       29000, 3),
('9780000000039', '샘플 도서 40', '저자 10', '출판사 5', '경제',       29500, 4);

-- ④ 재고 (120건) — 각 도서 i번째: (i%5)+1개
-- available: 0=대여중, 1=대여가능
-- 대여중 재고: inv_id=1(김민수4일전), 2(김민수2일전), 3(최유진7일전), 6(박서준30일전연체)
INSERT IGNORE INTO library_book_inventory (inventory_id, isbn, available, rent_date) VALUES
-- book 0 (1개)
(1,   '9780000000000', 0, CURDATE() - INTERVAL 4  DAY),
-- book 1 (2개)
(2,   '9780000000001', 0, CURDATE() - INTERVAL 2  DAY),
(3,   '9780000000001', 0, CURDATE() - INTERVAL 7  DAY),
-- book 2 (3개)
(4,   '9780000000002', 1, NULL),
(5,   '9780000000002', 1, NULL),
(6,   '9780000000002', 0, CURDATE() - INTERVAL 30 DAY),
-- book 3 (4개)
(7,   '9780000000003', 1, NULL),
(8,   '9780000000003', 1, NULL),
(9,   '9780000000003', 1, NULL),
(10,  '9780000000003', 1, NULL),
-- book 4 (5개)
(11,  '9780000000004', 1, NULL),
(12,  '9780000000004', 1, NULL),
(13,  '9780000000004', 1, NULL),
(14,  '9780000000004', 1, NULL),
(15,  '9780000000004', 1, NULL),
-- book 5 (1개)
(16,  '9780000000005', 1, NULL),
-- book 6 (2개)
(17,  '9780000000006', 1, NULL),
(18,  '9780000000006', 1, NULL),
-- book 7 (3개)
(19,  '9780000000007', 1, NULL),
(20,  '9780000000007', 1, NULL),
(21,  '9780000000007', 1, NULL),
-- book 8 (4개)
(22,  '9780000000008', 1, NULL),
(23,  '9780000000008', 1, NULL),
(24,  '9780000000008', 1, NULL),
(25,  '9780000000008', 1, NULL),
-- book 9 (5개)
(26,  '9780000000009', 1, NULL),
(27,  '9780000000009', 1, NULL),
(28,  '9780000000009', 1, NULL),
(29,  '9780000000009', 1, NULL),
(30,  '9780000000009', 1, NULL),
-- book 10 (1개)
(31,  '9780000000010', 1, NULL),
-- book 11 (2개)
(32,  '9780000000011', 1, NULL),
(33,  '9780000000011', 1, NULL),
-- book 12 (3개)
(34,  '9780000000012', 1, NULL),
(35,  '9780000000012', 1, NULL),
(36,  '9780000000012', 1, NULL),
-- book 13 (4개)
(37,  '9780000000013', 1, NULL),
(38,  '9780000000013', 1, NULL),
(39,  '9780000000013', 1, NULL),
(40,  '9780000000013', 1, NULL),
-- book 14 (5개)
(41,  '9780000000014', 1, NULL),
(42,  '9780000000014', 1, NULL),
(43,  '9780000000014', 1, NULL),
(44,  '9780000000014', 1, NULL),
(45,  '9780000000014', 1, NULL),
-- book 15 (1개)
(46,  '9780000000015', 1, NULL),
-- book 16 (2개)
(47,  '9780000000016', 1, NULL),
(48,  '9780000000016', 1, NULL),
-- book 17 (3개)
(49,  '9780000000017', 1, NULL),
(50,  '9780000000017', 1, NULL),
(51,  '9780000000017', 1, NULL),
-- book 18 (4개)
(52,  '9780000000018', 1, NULL),
(53,  '9780000000018', 1, NULL),
(54,  '9780000000018', 1, NULL),
(55,  '9780000000018', 1, NULL),
-- book 19 (5개)
(56,  '9780000000019', 1, NULL),
(57,  '9780000000019', 1, NULL),
(58,  '9780000000019', 1, NULL),
(59,  '9780000000019', 1, NULL),
(60,  '9780000000019', 1, NULL),
-- book 20 (1개) ← availableIsbn 테스트 대상 (9780000000020)
(61,  '9780000000020', 1, NULL),
-- book 21 (2개)
(62,  '9780000000021', 1, NULL),
(63,  '9780000000021', 1, NULL),
-- book 22 (3개)
(64,  '9780000000022', 1, NULL),
(65,  '9780000000022', 1, NULL),
(66,  '9780000000022', 1, NULL),
-- book 23 (4개)
(67,  '9780000000023', 1, NULL),
(68,  '9780000000023', 1, NULL),
(69,  '9780000000023', 1, NULL),
(70,  '9780000000023', 1, NULL),
-- book 24 (5개)
(71,  '9780000000024', 1, NULL),
(72,  '9780000000024', 1, NULL),
(73,  '9780000000024', 1, NULL),
(74,  '9780000000024', 1, NULL),
(75,  '9780000000024', 1, NULL),
-- book 25 (1개)
(76,  '9780000000025', 1, NULL),
-- book 26 (2개)
(77,  '9780000000026', 1, NULL),
(78,  '9780000000026', 1, NULL),
-- book 27 (3개)
(79,  '9780000000027', 1, NULL),
(80,  '9780000000027', 1, NULL),
(81,  '9780000000027', 1, NULL),
-- book 28 (4개)
(82,  '9780000000028', 1, NULL),
(83,  '9780000000028', 1, NULL),
(84,  '9780000000028', 1, NULL),
(85,  '9780000000028', 1, NULL),
-- book 29 (5개)
(86,  '9780000000029', 1, NULL),
(87,  '9780000000029', 1, NULL),
(88,  '9780000000029', 1, NULL),
(89,  '9780000000029', 1, NULL),
(90,  '9780000000029', 1, NULL),
-- book 30 (1개)
(91,  '9780000000030', 1, NULL),
-- book 31 (2개)
(92,  '9780000000031', 1, NULL),
(93,  '9780000000031', 1, NULL),
-- book 32 (3개)
(94,  '9780000000032', 1, NULL),
(95,  '9780000000032', 1, NULL),
(96,  '9780000000032', 1, NULL),
-- book 33 (4개)
(97,  '9780000000033', 1, NULL),
(98,  '9780000000033', 1, NULL),
(99,  '9780000000033', 1, NULL),
(100, '9780000000033', 1, NULL),
-- book 34 (5개)
(101, '9780000000034', 1, NULL),
(102, '9780000000034', 1, NULL),
(103, '9780000000034', 1, NULL),
(104, '9780000000034', 1, NULL),
(105, '9780000000034', 1, NULL),
-- book 35 (1개)
(106, '9780000000035', 1, NULL),
-- book 36 (2개)
(107, '9780000000036', 1, NULL),
(108, '9780000000036', 1, NULL),
-- book 37 (3개)
(109, '9780000000037', 1, NULL),
(110, '9780000000037', 1, NULL),
(111, '9780000000037', 1, NULL),
-- book 38 (4개)
(112, '9780000000038', 1, NULL),
(113, '9780000000038', 1, NULL),
(114, '9780000000038', 1, NULL),
(115, '9780000000038', 1, NULL),
-- book 39 (5개)
(116, '9780000000039', 1, NULL),
(117, '9780000000039', 1, NULL),
(118, '9780000000039', 1, NULL),
(119, '9780000000039', 1, NULL),
(120, '9780000000039', 1, NULL);

ALTER TABLE library_book_inventory AUTO_INCREMENT = 121;

-- ⑤ 이용자 (6명)
-- 이영희(9507072): 불량 이용자 (5일 전 지정, 사유: OVERDUE_LONG)
INSERT IGNORE INTO library_users (user_code7, user_name, is_blacklisted, blacklist_reason_code, blacklist_memo, blacklisted_at) VALUES
('9001151', '홍길동', 0, NULL,          NULL,             NULL),
('0102033', '김민수', 0, NULL,          NULL,             NULL),
('9507072', '이영희', 1, 'OVERDUE_LONG','장기 연체 이력', NOW() - INTERVAL 5 DAY),
('8812121', '박서준', 0, NULL,          NULL,             NULL),
('0203044', '최유진', 0, NULL,          NULL,             NULL),
('9901012', '정하늘', 0, NULL,          NULL,             NULL);

-- ⑥ 대여 이력 (16건)
-- 현재 대여(4건): inv_id=1,2 김민수 / inv_id=3 최유진 / inv_id=6 박서준(연체)
-- 반납 완료(12건): is_overdue/overdue_days 정확히 계산
INSERT IGNORE INTO library_rent_records
  (rent_id, inventory_id, isbn, user_code7, user_name, rent_date, due_date, return_date, is_overdue, overdue_days)
VALUES
-- 현재 대여 — 김민수 2권 (연체 없음)
(1,  1,  '9780000000000', '0102033', '김민수',
    CURDATE() - INTERVAL 4 DAY,  CURDATE() + INTERVAL 17 DAY, NULL,                        0, 0),
(2,  2,  '9780000000001', '0102033', '김민수',
    CURDATE() - INTERVAL 2 DAY,  CURDATE() + INTERVAL 19 DAY, NULL,                        0, 0),
-- 현재 대여 — 최유진 1권 (연체 없음)
(3,  3,  '9780000000001', '0203044', '최유진',
    CURDATE() - INTERVAL 7 DAY,  CURDATE() + INTERVAL 14 DAY, NULL,                        0, 0),
-- 반납 완료 — 홍길동 2건 (연체 없음)
(4,  4,  '9780000000002', '9001151', '홍길동',
    CURDATE() - INTERVAL 35 DAY, CURDATE() - INTERVAL 14 DAY, CURDATE() - INTERVAL 25 DAY, 0, 0),
(5,  5,  '9780000000002', '9001151', '홍길동',
    CURDATE() - INTERVAL 60 DAY, CURDATE() - INTERVAL 39 DAY, CURDATE() - INTERVAL 41 DAY, 0, 0),
-- 현재 대여 — 박서준 1권 (연체: 30일 전 대여, 만기 9일 전 초과)
(6,  6,  '9780000000002', '8812121', '박서준',
    CURDATE() - INTERVAL 30 DAY, CURDATE() - INTERVAL 9 DAY,  NULL,                        0, 0),
-- 반납 완료 — 박서준 2건 (연체 있음)
(7,  7,  '9780000000003', '8812121', '박서준',
    CURDATE() - INTERVAL 50 DAY, CURDATE() - INTERVAL 29 DAY, CURDATE() - INTERVAL 15 DAY, 1, 14),
(8,  8,  '9780000000003', '8812121', '박서준',
    CURDATE() - INTERVAL 90 DAY, CURDATE() - INTERVAL 69 DAY, CURDATE() - INTERVAL 65 DAY, 1, 4),
-- 추가 이력 8건 (홍길동/김민수/이영희/박서준/최유진/정하늘 균등 배분)
(9,  9,  '9780000000003', '9001151', '홍길동',
    CURDATE() - INTERVAL 20 DAY, CURDATE() + INTERVAL 1 DAY,  CURDATE() - INTERVAL 15 DAY, 0, 0),
(10, 10, '9780000000003', '0102033', '김민수',
    CURDATE() - INTERVAL 31 DAY, CURDATE() - INTERVAL 10 DAY, CURDATE() - INTERVAL 16 DAY, 0, 0),
(11, 11, '9780000000004', '9507072', '이영희',
    CURDATE() - INTERVAL 22 DAY, CURDATE() - INTERVAL 1 DAY,  CURDATE() - INTERVAL 17 DAY, 0, 0),
(12, 12, '9780000000004', '8812121', '박서준',
    CURDATE() - INTERVAL 33 DAY, CURDATE() - INTERVAL 12 DAY, CURDATE() - INTERVAL 18 DAY, 0, 0),
(13, 13, '9780000000004', '0203044', '최유진',
    CURDATE() - INTERVAL 24 DAY, CURDATE() - INTERVAL 3 DAY,  CURDATE() - INTERVAL 19 DAY, 0, 0),
(14, 14, '9780000000004', '9901012', '정하늘',
    CURDATE() - INTERVAL 35 DAY, CURDATE() - INTERVAL 14 DAY, CURDATE() - INTERVAL 20 DAY, 0, 0),
(15, 15, '9780000000004', '9001151', '홍길동',
    CURDATE() - INTERVAL 26 DAY, CURDATE() - INTERVAL 5 DAY,  CURDATE() - INTERVAL 21 DAY, 0, 0),
(16, 16, '9780000000005', '0102033', '김민수',
    CURDATE() - INTERVAL 37 DAY, CURDATE() - INTERVAL 16 DAY, CURDATE() - INTERVAL 22 DAY, 0, 0);

ALTER TABLE library_rent_records AUTO_INCREMENT = 17;
