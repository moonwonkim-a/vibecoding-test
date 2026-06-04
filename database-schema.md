# Database Schema

## 1. DB 환경

- DBMS: MariaDB
- DB 확인 도구: DBeaver
- DB 접속 URL: `jdbc:mariadb://192.100.50.100:33307/test`
- Host: `192.100.50.100`
- Port: `33307`
- Database: `test`
- 테이블 생성 방식: Spring Boot JPA Entity 기반 자동 생성
- 개발 설정: `spring.jpa.hibernate.ddl-auto=update`

제공된 `test` 데이터베이스 안에 프로젝트용 테이블을 생성한다.

DBeaver에서는 DB 연결 확인과 테이블/목데이터 확인을 수행한다.

테이블을 DBeaver에서 직접 하나씩 만들 필요는 없다. Spring Boot JPA Entity를 기준으로 테이블을 생성한다.

Spring Boot 설정 예시:

```properties
spring.datasource.url=jdbc:mariadb://192.100.50.100:33307/test
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect
spring.jpa.show-sql=true
```

## 2. 명명 규칙

- 테이블명은 snake_case를 사용한다.
- 테이블명은 `library_` 접두사를 사용한다.
- 컬럼명은 snake_case를 사용한다.
- Java Entity 필드는 camelCase를 사용한다.
- 실제 주민번호는 사용하지 않는다.
- 요구사항 원문의 주민번호 앞 7자리는 구현에서 `user_code7`로 대체한다.

## 3. 테이블 목록

- `library_book_info`
- `library_book_inventory`
- `library_users`
- `library_rent_records`
- `library_blacklist_reasons`
- `library_admins`

## 4. library_book_info

도서 마스터 테이블이다. ISBN 기준 도서 메타데이터를 저장한다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| isbn | VARCHAR(13) | PK | ISBN 13자리 |
| title | VARCHAR(300) | NOT NULL | 도서 제목 |
| author | VARCHAR(200) | NOT NULL | 저자 |
| publisher | VARCHAR(200) | NOT NULL | 출판사 |
| category | VARCHAR(100) | NOT NULL | 카테고리 |
| price | INT | NOT NULL | 가격 |
| rent_count | INT | DEFAULT 0 | 누적 대여 횟수 |

규칙:

- ISBN은 13자리 숫자 문자열이어야 한다.
- 도서 목록은 `library_book_info` 기준 1 ISBN = 1행으로 표시한다.

## 5. library_book_inventory

실물 재고 테이블이다. 실제 책 1권을 1행으로 저장한다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| inventory_id | BIGINT | PK, AUTO_INCREMENT | 실물 재고 ID |
| isbn | VARCHAR(13) | FK -> library_book_info.isbn | 도서 마스터 ISBN |
| available | BOOLEAN | DEFAULT TRUE | 대여 가능 여부 |
| rent_date | DATE | NULL | 현재 대여 시작일 |

규칙:

- 하나의 ISBN은 여러 실물 재고를 가질 수 있다.
- 이용자는 실물 재고를 직접 선택하지 않는다.
- 대여 시 서버가 `available=true`인 재고 1건을 자동 할당한다.

## 6. library_users

이용자 상태 테이블이다.

이 테이블은 사전 회원가입용 테이블이 아니다.

일반 이용자는 별도 회원가입 없이 이름과 `user_code7`을 입력하여 대여할 수 있다. 첫 대여 시 해당 `user_code7`이 없으면 서버가 `library_users` 행을 자동 생성한다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| user_code7 | CHAR(7) | PK | 테스트용 7자리 이용자 식별 코드 |
| user_name | VARCHAR(100) | NOT NULL | 이용자 이름 |
| is_blacklisted | BOOLEAN | DEFAULT FALSE | 불량 이용자 여부 |
| blacklist_reason_code | VARCHAR(20) | FK -> library_blacklist_reasons.reason_code, NULL | 불량 지정 사유 코드 |
| blacklist_memo | VARCHAR(500) | NULL | 기타 사유 또는 관리자 메모 |
| blacklisted_at | DATETIME | NULL | 불량 지정 일시 |

규칙:

- `user_code7`은 숫자 7자리 문자열이다.
- 실제 주민번호를 저장하지 않는다.
- 사전 목데이터 이용자가 없어도 신규 대여 요청 시 자동 생성할 수 있다.
- 동일 `user_code7`이 이미 있으면 기존 이용자 상태를 사용한다.
- 이미 존재하는 `user_code7`에 다른 이름이 입력되면 서버 정책에 따라 기존 이름을 유지하거나 검증 오류로 처리한다. 기본 권장안은 검증 오류 처리이다.
- 관리자 화면에서는 앞 4자리 + `***`로 마스킹한다.
- 현재 대여 권수는 저장하지 않고 미반납 `rent_records` 개수로 계산한다.
- 불량 이용자 지정/해제 상태는 이 테이블에 저장한다.

## 7. library_rent_records

대여 기록 테이블이다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| rent_id | BIGINT | PK, AUTO_INCREMENT | 대여 기록 ID |
| inventory_id | BIGINT | FK -> library_book_inventory.inventory_id | 실물 재고 ID |
| isbn | VARCHAR(13) | FK -> library_book_info.isbn | 도서 ISBN |
| user_code7 | CHAR(7) | FK -> library_users.user_code7 | 이용자 식별 코드 |
| user_name | VARCHAR(100) | NOT NULL | 대여 당시 이용자 이름 |
| rent_date | DATE | NOT NULL | 대여일 |
| due_date | DATE | NOT NULL | 반납 기한 |
| return_date | DATE | NULL | 실제 반납일 |
| is_overdue | BOOLEAN | DEFAULT FALSE | 연체 여부 |

규칙:

- 반납 전 기록은 `return_date=null`이다.
- 반납 기한은 `rent_date + 21일`이다.
- 대여 기록은 삭제하지 않는다.
- 연체 여부는 반납 시 갱신한다.
- 현재 대여 중 도서는 `return_date is null` 조건으로 조회한다.

## 8. library_blacklist_reasons

불량 지정 사유 코드 마스터 테이블이다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| reason_code | VARCHAR(20) | PK | 사유 코드 |
| reason_label | VARCHAR(100) | NOT NULL | 화면 표시명 |
| description | VARCHAR(300) | NULL | 설명 |

필수 데이터:

| reason_code | reason_label | description |
|---|---|---|
| OVERDUE_REPEAT | 반복 연체 | 3회 이상 반납 기한 초과 이력 |
| OVERDUE_LONG | 장기 연체 | 반납 기한 30일 이상 초과 |
| LOST | 도서 분실 | 대여 도서 분실 처리 |
| DAMAGE | 도서 훼손 | 도서 심각 훼손 |
| FRAUD | 부정 대여 | 타인 명의 도용 등 부정 대여 시도 |
| ETC | 기타 | 기타 사유 |

## 9. library_admins

관리자 계정 테이블이다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| admin_id | VARCHAR(50) | PK | 관리자 로그인 ID |
| admin_pw | VARCHAR(256) | NOT NULL | BCrypt 해시 비밀번호 |
| admin_name | VARCHAR(100) | NOT NULL | 관리자 이름 |
| created_at | DATETIME | NOT NULL | 계정 생성 일시 |

규칙:

- 비밀번호는 평문 저장 금지.
- 개발 목데이터 계정 1개를 생성한다.

## 10. 관계

| 관계 | 설명 |
|---|---|
| library_book_info 1:N library_book_inventory | 하나의 도서 마스터는 여러 실물 재고를 가진다. |
| library_book_info 1:N library_rent_records | 하나의 도서는 여러 대여 기록을 가진다. |
| library_book_inventory 1:N library_rent_records | 하나의 실물 재고는 여러 대여 기록을 가진다. |
| library_users 1:N library_rent_records | 한 이용자는 여러 대여 기록을 가진다. |
| library_blacklist_reasons 1:N library_users | 하나의 불량 사유는 여러 이용자에게 적용될 수 있다. |

## 11. 초기 목데이터 정책

외부 도서 데이터는 사용하지 않는다.

개발 환경에서 애플리케이션 시작 시 목데이터를 자동 생성한다.

필수 목데이터:

- `library_book_info`: 최소 40권
- `library_book_inventory`: 도서별 1~5권
- `library_users`: 최소 6명
- `library_admins`: 최소 1명
- `library_blacklist_reasons`: 필수 코드 6개
- `library_rent_records`: 최소 15건

목데이터는 다음 검증이 가능해야 한다.

- 검색 결과 있음
- 검색 결과 없음
- 카테고리 필터
- 제목/저자/가격/대여 가능 여부 정렬
- 대여 가능 도서
- 대여 불가 도서
- 현재 대여 0권 이용자
- 현재 대여 1권 이용자
- 현재 대여 2권 이용자
- 불량 이용자
- 연체 이력 있는 이용자
- 대여 이력 없는 이용자
- 첫 대여 시 자동 생성되는 신규 이용자
- 대여 순위 TOP 10

## 12. 권장 목데이터 예시

관리자:

| admin_id | admin_pw | admin_name |
|---|---|---|
| admin | BCrypt 해시값 | 관리자 |

이용자:

| user_name | user_code7 | 상태 |
|---|---|---|
| 홍길동 | 9001151 | 정상, 현재 대여 0권 |
| 김민수 | 0102033 | 정상, 현재 대여 2권 |
| 이영희 | 9507072 | 불량 이용자 |
| 박서준 | 8812121 | 연체 이력 보유 |
| 최유진 | 0203044 | 현재 대여 1권 |
| 정하늘 | 9901012 | 대여 이력 없음 |

신규 이용자 자동 생성 테스트:

| user_name | user_code7 | 기대 동작 |
|---|---|---|
| 신규대여자 | 0405053 | 첫 대여 요청 시 `library_users`에 자동 생성 |

## 13. 삭제 정책

- 대여 중인 재고는 삭제할 수 없다.
- 도서 마스터 삭제는 해당 ISBN의 모든 재고가 대여 가능 상태일 때만 허용한다.
- 대여 기록은 삭제하지 않는다.
