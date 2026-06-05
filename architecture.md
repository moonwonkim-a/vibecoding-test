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
### 레이어 책임 원칙

- **Controller**: 요청 수신 및 응답 반환만 담당한다. 비즈니스 로직을 포함하지 않는다.
- **Service**: 비즈니스 로직과 트랜잭션을 담당한다. DB 접근은 Repository를 통해서만 수행한다.
- **Repository**: DB 접근만 담당한다. 비즈니스 로직을 포함하지 않는다.
- **Entity**: 테이블 매핑과 필드 정의만 담당한다. 복잡한 비즈니스 메서드는 Service에 둔다.
- **DTO**: 요청/응답 데이터 전달만 담당한다. Entity를 Controller나 외부 레이어에 직접 노출하지 않는다.

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
### 레이어 책임 원칙

- **page.tsx**: 페이지 진입점 역할만 담당한다. 데이터 fetching과 비즈니스 로직은 포함하지 않는다.
- **components**: UI 렌더링과 사용자 이벤트 처리만 담당한다. API 호출을 직접 포함하지 않는다.
- **lib/api.ts**: 모든 API 호출을 담당한다. fetch 로직을 컴포넌트에 직접 작성하지 않는다.
- **lib/types.ts**: 요청/응답 타입 정의만 담당한다. `api-spec.md`의 필드명과 타입을 그대로 따른다.

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

목데이터는 `src/main/resources/data.sql`로 관리한다.

- `spring.sql.init.mode=always` 설정으로 앱 시작 시 자동 실행된다.
- `spring.jpa.defer-datasource-initialization=true` 설정으로 JPA 스키마 생성 후 SQL 스크립트가 실행된다.
- `data.sql`은 시작 시 테스트 상태(대여, 불량 지정, 소프트 삭제 등)를 초기 상태로 리셋한 뒤 초기 데이터를 삽입한다.
- `MockDataInitializer`(`CommandLineRunner`)는 관리자 계정(`admin`) 생성만 담당한다.

목데이터 기준은 `database-schema.md`와 `test-plan.md`를 따른다.

## 9. 트랜잭션 정책

대여 처리에서는 동시 요청에 의한 중복 재고 할당을 방지해야 한다.

필수 처리:

- 대여 가능한 재고 1건 조회
- 해당 재고 잠금 또는 트랜잭션 보호
- 재고 상태 변경
- 대여 기록 생성
- 도서 대여 횟수 증가

이 과정은 하나의 트랜잭션으로 처리한다.

## 10. CORS 정책

Next.js(기본 포트 3000)와 Spring Boot(포트 8080)는 별도 서버로 동작하므로 CORS 설정이 필요하다.

개발 환경 허용 설정:

- 허용 Origin: `http://localhost:3000`
- 허용 메서드: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS`
- 허용 헤더: `*`
- 자격증명 포함 여부: `true` (세션 쿠키 전달을 위해 필수)
- 설정 위치: `common/config/CorsConfig.java`

프론트엔드 `fetch` 호출 시 `credentials: 'include'` 옵션을 포함해야 세션 쿠키가 전달된다.

## 11. 글로벌 예외 처리 정책

모든 예외는 `common/exception/GlobalExceptionHandler.java`에서 중앙 처리한다.

규칙:

- `@RestControllerAdvice`로 구현한다.
- 모든 예외 응답은 `api-spec.md`의 공통 실패 응답 포맷을 따른다.
- 각 에러코드별 HTTP 상태코드는 `api-spec.md`의 에러코드 매핑 테이블을 따른다.
- `MethodArgumentNotValidException` (Bean Validation 실패)는 `400`으로 응답하며, 첫 번째 필드 오류 메시지를 `message`에 포함한다.
- 정의되지 않은 예외는 `500`으로 응답하고 `"서버 오류가 발생하였습니다."` 메시지를 반환한다.

## 12. 인증 세부 정책

세션 기반 인증을 기본으로 사용한다.

설정 기준:

- 세션 만료 시간: 비활동 30분 후 만료 (`server.servlet.session.timeout=30m`)
- 세션 쿠키명: 기본값 `JSESSIONID` 사용
- CSRF: 개발 편의를 위해 비활성화 (`csrf.disable()`)
- 로그아웃 엔드포인트: `POST /api/admin/logout` (서버 세션 무효화)
- 미인증 접근 시: `401` + `AUTH-001` 에러 응답 (JSON 형식, `api-spec.md` 참고)
- Spring Security 설정 위치: `common/config/SecurityConfig.java`

프론트엔드 관리자 로그인 상태 관리:

- 로그인 성공 응답 후 React Context 또는 전역 상태에 관리자 정보를 보관한다.
- 페이지 새로고침 시 `/admin/me` 엔드포인트 등으로 세션 유효성을 재확인하거나, 401 응답 수신 시 로그인 화면으로 리다이렉트한다.
- 세션 만료(401 응답) 시 로그인 모달 또는 로그인 페이지로 이동한다.

## 13. API 경로 규칙

모든 API는 `/api` prefix를 사용한다.

| 도메인 | 경로 prefix |
|---|---|
| 도서 조회 | `/api/books` |
| 대여 | `/api/rentals` |
| 반납 | `/api/returns` |
| 관리자 | `/api/admin` |

컨트롤러 클래스에 `@RequestMapping("/api/books")` 형태로 prefix를 명시한다.
