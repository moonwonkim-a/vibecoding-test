# Architecture

## 1. 기술 스택

### Backend

- Java: JDK 25
- Framework: Spring Boot
- Build Tool: Gradle
- Persistence: Spring Data JPA
- Validation: Jakarta Bean Validation
- Security: Spring Security
- Password Hashing: BCrypt
- Database: MariaDB
- Test: JUnit 5, MockMvc

JDK 25 사용을 기준으로 하되, 빌드 실패 시 Gradle 및 Spring Boot 버전의 JDK 25 호환성을 먼저 확인한다.

### Frontend

- Framework: Next.js
- Language: TypeScript
- Routing: App Router
- API Call: `fetch`
- Styling: CSS Module, Tailwind CSS, 또는 프로젝트 생성 시 선택한 단일 방식

프론트엔드는 2페이지로 구성한다.

- `/`: 이용자 페이지
- `/admin`: 관리자 대시보드 페이지

## 2. Backend 구조

권장 패키지 구조:

```text
com.example.library
├── admin
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
├── book
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
├── rental
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
├── user
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
├── common
│   ├── config
│   ├── exception
│   └── response
└── LibraryApplication.java
```

## 3. Frontend 구조

권장 구조:

```text
src
├── app
│   ├── page.tsx
│   ├── admin
│   │   └── page.tsx
│   └── layout.tsx
├── components
│   ├── admin
│   ├── books
│   ├── rental
│   └── shared
├── lib
│   ├── api.ts
│   └── types.ts
└── styles
```

## 4. 페이지 책임

### 이용자 페이지

`/` 페이지는 일반 이용자 기능만 포함한다.

- 도서 목록
- 검색/필터/정렬
- 상세 모달
- 대여 모달
- 반납 영역 또는 반납 모달
- 대여 순위

### 관리자 대시보드

`/admin` 페이지는 관리자 기능만 포함한다.

- 로그인 상태 관리
- 전체 도서/재고 현황
- 도서 추가/삭제
- 대여 중 도서 목록
- 반납 이력 조회
- 불량 이용자 지정/해제

## 5. API 설계 원칙

- 모든 API는 `api-spec.md`를 따른다.
- 요청/응답 필드명은 임의로 변경하지 않는다.
- 성공 응답은 일관된 JSON 구조로 반환한다.
- 실패 응답은 `errorCode`, `message`, `requirementId`를 포함한다.
- 프론트엔드는 백엔드 응답 구조를 직접 추정하지 않고 `api-spec.md` 기준으로 구현한다.

## 6. 인증 정책

관리자 기능에는 인증이 필요하다.

미니 프로젝트에서는 다음 중 하나를 사용할 수 있다.

- 세션 기반 인증
- JWT 기반 인증

기본 권장안은 세션 기반 인증이다.

규칙:

- 관리자 비밀번호는 BCrypt로 저장한다.
- 관리자 API는 인증되지 않은 요청을 거부한다.
- 이용자 페이지는 별도 로그인 없이 이름과 `user_code7`로 대여/반납 조회를 수행한다.

## 7. DB 정책

- DBMS는 MariaDB를 사용한다.
- DBeaver는 데이터베이스, 테이블, 목데이터 확인 용도로 사용한다.
- 테이블은 Spring Boot JPA Entity와 `ddl-auto` 설정으로 생성한다.
- DB 상세 설계는 `database-schema.md`를 따른다.
- DB 접속 URL은 `jdbc:mariadb://192.100.50.100:33307/test`를 사용한다.
- 제공된 `test` 데이터베이스 안에 프로젝트용 테이블을 생성한다.
- 다른 사용자/프로젝트와 테이블명이 겹치지 않도록 테이블명은 `library_` 접두사를 사용한다.

권장 개발 설정:

```properties
spring.datasource.url=jdbc:mariadb://192.100.50.100:33307/test
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect
spring.jpa.show-sql=true
```

## 8. 목데이터 정책

외부 도서 데이터는 사용하지 않는다.

개발 환경에서 Spring Boot 앱 시작 시 목데이터를 자동 생성한다.

목데이터 생성 기준은 `database-schema.md`와 `test-plan.md`를 따른다.

## 9. 트랜잭션 정책

대여 처리에서는 동시 요청에 의한 중복 재고 할당을 방지해야 한다.

필수 처리:

- 대여 가능한 재고 1건 조회
- 해당 재고 잠금 또는 트랜잭션 보호
- 재고 상태 변경
- 대여 기록 생성
- 도서 대여 횟수 증가

이 과정은 하나의 트랜잭션으로 처리한다.
