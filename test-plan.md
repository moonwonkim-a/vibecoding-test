# Test Plan

## 1. 테스트 목적

이 문서는 요구사항 ID 기준으로 API와 주요 화면 동작을 검증하기 위한 테스트 계획이다.

테스트는 API 테스트 프로그램을 이용한 블랙박스 테스트로 수행한다. 실행 중인 Spring Boot 서버에 실제 HTTP 요청을 보내 응답을 검증한다.

테스트 도구:

- Bruno (테스트 정의는 `bruno/` 컬렉션의 `.bru` 파일로 작성, TC ID와 1:1 매칭)
- 실행: Bruno MCP 또는 Bruno CLI (`bru run`) — 자동화 절차는 `AGENT-TEST-WORKFLOW.md` 참조
- 테스트 결과는 `test-results/`에 실행 회차별로 기록한다 (`INDEX.md` 요약 + 월별 상세)

## 2. 테스트 원칙

- 모든 테스트는 요구사항 ID와 연결한다.
- 성공 케이스와 예외 케이스를 모두 검증한다.
- 목데이터는 `database-schema.md`의 초기 목데이터 정책을 따른다. 테스트 실행 전 서버를 재시작하면 `data.sql`이 자동으로 재실행되어 초기 상태로 리셋된다.
- 실제 개인정보를 테스트 데이터로 사용하지 않는다.
- `user_code7`은 테스트용 7자리 식별 코드만 사용한다.
- 검증은 API 응답을 기준으로 한다. 기대 결과가 DB 저장 값인 TC(예: `rent_date` 기록, `return_date` 저장, BCrypt 해시)는 후속 조회 API 또는 test 프로파일 한정 test-support 엔드포인트로 확인한다.
- 동시성 검증(TC-RENT-013)은 병렬 요청 스크립트로 수행한다 (`AGENT-TEST-WORKFLOW.md` 6.3절).

## 3. 도서 목록 테스트

| TC ID | 요구사항 ID | 테스트 내용 | 기대 결과 |
|---|---|---|---|
| TC-LIST-001 | FR-LIST-001 | 도서 목록 기본 조회 | 30건 이하 페이징 응답 |
| TC-LIST-002 | FR-LIST-002 | 목록 필드 확인 | ISBN, 제목, 저자, 출판사, 카테고리, 가격, 수량 정보 포함 |
| TC-LIST-003 | FR-LIST-003 | 제목 오름차순 정렬 | 제목 기준 asc 정렬 |
| TC-LIST-004 | FR-LIST-003 | 가격 내림차순 정렬 | 가격 기준 desc 정렬 |
| TC-LIST-005 | FR-LIST-004 | 제목 검색 | 검색어에 맞는 도서만 반환 |
| TC-LIST-006 | FR-LIST-004 | 카테고리 필터 | 해당 카테고리 도서만 반환 |
| TC-LIST-007 | EX-006 | 검색 결과 없음 | 빈 목록과 검색 결과 없음 메시지 |
| TC-LIST-008 | FR-LIST-005 | 대여 불가 도서 표시 | `availableCount=0`, `rentAvailable=false` |

## 4. 도서 상세 테스트

| TC ID | 요구사항 ID | 테스트 내용 | 기대 결과 |
|---|---|---|---|
| TC-DETAIL-001 | FR-DETAIL-002 | 도서 상세 조회 | 도서 메타데이터와 재고 수량 반환 |
| TC-DETAIL-002 | FR-DETAIL-003 | 대여 가능 도서 상세 | `rentAvailable=true` |
| TC-DETAIL-003 | FR-DETAIL-004 | 재고 0권 도서 상세 | `rentAvailable=false` |

## 5. 대여 테스트

| TC ID | 요구사항 ID | 테스트 내용 | 기대 결과 |
|---|---|---|---|
| TC-RENT-001 | FR-RENT-001 | 정상 이용자 대여 가능 여부 조회 | 이용자 정보와 현재 대여 목록 반환 |
| TC-RENT-002 | FR-RENT-002 | 현재 대여 도서 목록 조회 | 미반납 도서 목록 반환 |
| TC-RENT-003 | FR-RENT-003, EX-001 | 현재 2권 대여 이용자 추가 대여 시도 | 대여 차단 |
| TC-RENT-004 | FR-RENT-004, EX-002 | 불량 이용자 대여 시도 | 대여 차단 |
| TC-RENT-005 | FR-RENT-006 | 정상 대여 요청 | 대여 가능한 재고 자동 할당 |
| TC-RENT-006 | FR-RENT-007 | 대여 후 재고 상태 확인 | `available=false`, `rent_date` 기록 |
| TC-RENT-007 | FR-RENT-008 | 대여 기록 생성 확인 | `due_date=rent_date+21` |
| TC-RENT-008 | FR-RENT-009 | 대여 완료 응답 확인 | 반납 기한 포함 |
| TC-RENT-009 | EX-003 | 재고 0권 도서 대여 시도 | 대여 차단 |
| TC-RENT-010 | FR-RENT-006 | 신규 이용자 첫 대여 | `library_users` 자동 생성 후 대여 성공 |
| TC-RENT-011 | EX-014 | `user_code7` 형식 오류 | 요청 실패 |
| TC-RENT-012 | EX-015 | 동일 `user_code7`에 다른 이름 입력 | 409 응답, 대여 차단 |
| TC-RENT-013 | EX-009, NFR-010 | 동일 재고에 동시 대여 요청 2건 | 1건만 성공, 나머지 409 응답 |

## 6. 반납 테스트

| TC ID | 요구사항 ID | 테스트 내용 | 기대 결과 |
|---|---|---|---|
| TC-RETURN-001 | FR-RETURN-001 | 반납 후보 조회 | 미반납 도서 목록 반환 |
| TC-RETURN-002 | FR-RETURN-002 | 대여 중 도서가 없는 이용자 조회 | 빈 목록 반환 |
| TC-RETURN-003 | FR-RETURN-005 | 정상 반납 | 재고 `available=true`, `rent_date=null` |
| TC-RETURN-004 | FR-RETURN-006 | 반납 기록 확인 | `return_date` 저장 |
| TC-RETURN-005 | FR-RETURN-004, EX-004 | 연체 도서 반납 | 반납 허용, 연체 여부 true |
| TC-RETURN-006 | EX-005 | 이용자 정보 불일치 | 조회 실패 |
| TC-RETURN-007 | EX-015 | 반납 시 동일 `user_code7`에 다른 이름 입력 | 409 응답, 반납 차단 |

## 7. 대여 순위 테스트

| TC ID | 요구사항 ID | 테스트 내용 | 기대 결과 |
|---|---|---|---|
| TC-RANK-001 | FR-RANKING-001 | 전체 TOP 10 조회 | rent_count 기준 상위 10건 |
| TC-RANK-002 | FR-RANKING-002 | 카테고리 TOP 10 조회 | 해당 카테고리 내 상위 10건 |

## 8. 관리자 로그인 테스트

| TC ID | 요구사항 ID | 테스트 내용 | 기대 결과 |
|---|---|---|---|
| TC-ADMIN-LOGIN-001 | FR-ADMIN-001 | 정상 로그인 | 로그인 성공 |
| TC-ADMIN-LOGIN-002 | EX-008 | 잘못된 비밀번호 로그인 | 로그인 실패 |
| TC-ADMIN-LOGIN-003 | NFR-004 | 관리자 비밀번호 저장 확인 | BCrypt 해시 저장 |
| TC-ADMIN-LOGIN-004 | FR-ADMIN-001 | 정상 로그아웃 | 세션 무효화, 200 응답 |
| TC-ADMIN-LOGIN-005 | NFR-006 | 로그아웃 후 관리자 API 접근 | 401 응답 |
| TC-ADMIN-LOGIN-006 | FR-ADMIN-014 | 미인증 상태로 관리자 API 접근 | 401 + AUTH-001 응답 |

## 9. 관리자 도서 관리 테스트

| TC ID | 요구사항 ID | 테스트 내용 | 기대 결과 |
|---|---|---|---|
| TC-ADMIN-BOOK-001 | FR-ADMIN-002 | 전체 도서/재고 현황 조회 | 수량 정보 포함 |
| TC-ADMIN-BOOK-002 | FR-ADMIN-003 | 신규 ISBN 도서 추가 | book_info와 inventory 생성 |
| TC-ADMIN-BOOK-003 | FR-ADMIN-003 | 기존 ISBN 재고 추가 | inventory만 추가 |
| TC-ADMIN-BOOK-004 | EX-010 | 잘못된 ISBN 추가 | 저장 차단 |
| TC-ADMIN-BOOK-005 | FR-ADMIN-004 | 대여 중이 아닌 재고 삭제 | 삭제 성공 |
| TC-ADMIN-BOOK-006 | EX-007 | 대여 중 재고 삭제 | 삭제 차단 |
| TC-ADMIN-BOOK-007 | EX-007 | 대여 중 재고가 있는 도서 마스터 삭제 | 삭제 차단 |

## 10. 관리자 대여 현황 테스트

| TC ID | 요구사항 ID | 테스트 내용 | 기대 결과 |
|---|---|---|---|
| TC-ADMIN-RENT-001 | FR-ADMIN-005 | 대여 중 도서 목록 조회 | 미반납 도서 목록 반환 |
| TC-ADMIN-RENT-002 | FR-ADMIN-005 | 반납 기한 기준 정렬 | dueDate 기준 정렬 |
| TC-ADMIN-RENT-003 | FR-ADMIN-005 | 연체 도서 확인 | overdue true, overdueDays 표시 |

## 11. 반납 이력 및 불량 이용자 테스트

| TC ID | 요구사항 ID | 테스트 내용 | 기대 결과 |
|---|---|---|---|
| TC-HISTORY-001 | FR-ADMIN-006 | 전체 반납 이력 조회 | 모든 도서의 반납 완료 기록 반환 (반납일 최신순) |
| TC-HISTORY-002 | FR-ADMIN-007 | 이력 필드 확인 | 도서명, 이용자명, 마스킹 코드, 대여일, 반납 기한, 반납일, 연체 정보 포함 |
| TC-HISTORY-003 | FR-ADMIN-007 | 연체 반납 항목 표시 | 연체 반납 건의 overdue=true, overdueDays > 0 |
| TC-HISTORY-004 | FR-ADMIN-008 | 행 단위 불량 지정 가능 여부 | 미불량 이용자 항목은 canBlacklist=true |
| TC-BLACKLIST-001 | FR-ADMIN-009, EX-012 | 사유 미선택 불량 지정 | 저장 차단 |
| TC-BLACKLIST-002 | FR-ADMIN-010 | ETC 선택 시 메모 저장 | memo 저장 |
| TC-BLACKLIST-003 | FR-ADMIN-011 | 정상 불량 지정 | is_blacklisted true |
| TC-BLACKLIST-004 | FR-ADMIN-012, EX-011 | 이미 불량 이용자 지정 시도 | 지정 차단 |
| TC-BLACKLIST-005 | FR-ADMIN-013 | 불량 이용자 목록 조회 | 불량 이용자 목록 반환 |
| TC-BLACKLIST-006 | FR-ADMIN-013 | 불량 해제 | is_blacklisted false |

## 12. 프론트엔드 확인 항목

프론트엔드는 API 테스트가 완료된 뒤 확인한다.

| TC ID | 페이지 | 확인 내용 |
|---|---|---|
| TC-UI-USER-001 | 이용자 페이지 | 도서 목록, 검색, 필터, 정렬이 동작한다. |
| TC-UI-USER-002 | 이용자 페이지 | 상세 모달에서 재고 수량과 대여 버튼 상태가 맞다. |
| TC-UI-USER-003 | 이용자 페이지 | 대여 성공/실패 메시지가 요구사항과 맞다. |
| TC-UI-USER-004 | 이용자 페이지 | 반납 후보 조회와 반납이 동작한다. |
| TC-UI-USER-005 | 이용자 페이지 | 대여 순위 TOP 10이 표시된다. |
| TC-UI-ADMIN-001 | 관리자 페이지 | 관리자 로그인이 동작한다. |
| TC-UI-ADMIN-002 | 관리자 페이지 | 도서/재고 현황이 표시된다. |
| TC-UI-ADMIN-003 | 관리자 페이지 | 도서 추가/삭제가 동작한다. |
| TC-UI-ADMIN-004 | 관리자 페이지 | 반납 이력 조회가 동작한다. |
| TC-UI-ADMIN-005 | 관리자 페이지 | 불량 지정/해제가 동작한다. |
