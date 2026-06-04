# API Specification

## 1. 공통 규칙

Base URL:

```text
http://localhost:8080/api
```

요청/응답은 JSON을 사용한다.

날짜 형식:

```text
YYYY-MM-DD
```

날짜시간 형식:

```text
YYYY-MM-DDTHH:mm:ss
```

## 2. 공통 성공 응답

```json
{
  "success": true,
  "data": {},
  "message": "요청이 성공했습니다."
}
```

## 3. 공통 실패 응답

```json
{
  "success": false,
  "errorCode": "EX-001",
  "message": "현재 대여 한도(2권)를 초과하였습니다.",
  "requirementId": "EX-001"
}
```

## 4. 이용자 API

### GET /books

도서 목록을 조회한다.

관련 요구사항:

- FR-LIST-001
- FR-LIST-002
- FR-LIST-003
- FR-LIST-004
- FR-LIST-005

Query Parameters:

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| page | number | no | 페이지 번호, 기본값 0 |
| size | number | no | 페이지 크기, 기본값 30 |
| keyword | string | no | 검색어 |
| filter | string | no | title, author, category, publisher |
| sort | string | no | title, author, category, price, available |
| direction | string | no | asc, desc |

Response:

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "isbn": "9788966262281",
        "title": "자바의 정석",
        "author": "남궁성",
        "publisher": "도우출판",
        "category": "프로그래밍",
        "price": 30000,
        "totalCount": 3,
        "availableCount": 2,
        "rentCount": 12,
        "rentAvailable": true
      }
    ],
    "page": 0,
    "size": 30,
    "totalElements": 40,
    "totalPages": 2
  },
  "message": "도서 목록을 조회했습니다."
}
```

### GET /books/{isbn}

도서 상세 정보를 조회한다.

관련 요구사항:

- FR-DETAIL-001
- FR-DETAIL-002
- FR-DETAIL-003
- FR-DETAIL-004

Response:

```json
{
  "success": true,
  "data": {
    "isbn": "9788966262281",
    "title": "자바의 정석",
    "author": "남궁성",
    "publisher": "도우출판",
    "category": "프로그래밍",
    "price": 30000,
    "totalCount": 3,
    "availableCount": 2,
    "rentAvailable": true
  },
  "message": "도서 상세 정보를 조회했습니다."
}
```

### GET /books/rankings

대여 순위 TOP 10을 조회한다.

관련 요구사항:

- FR-RANKING-001
- FR-RANKING-002

Query Parameters:

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| category | string | no | 카테고리 |

Response:

```json
{
  "success": true,
  "data": [
    {
      "rank": 1,
      "isbn": "9788966262281",
      "title": "자바의 정석",
      "author": "남궁성",
      "category": "프로그래밍",
      "rentCount": 12
    }
  ],
  "message": "대여 순위를 조회했습니다."
}
```

### POST /rentals/check

대여 전 이용자 상태와 현재 대여 목록을 확인한다.

사전 회원가입은 없다. 입력한 `userCode7`이 기존 이용자 상태 테이블에 없으면 신규 이용자로 간주하고 현재 대여 0권, 불량 아님으로 응답한다. 실제 `library_users` 행 생성은 최종 대여 요청 시 수행한다.

관련 요구사항:

- FR-RENT-001
- FR-RENT-002
- FR-RENT-003
- FR-RENT-004
- FR-RENT-005

Request:

```json
{
  "userName": "홍길동",
  "userCode7": "9001151"
}
```

Response:

```json
{
  "success": true,
  "data": {
    "userName": "홍길동",
    "userCode7Masked": "9001***",
    "blacklisted": false,
    "currentRentCount": 1,
    "canRent": true,
    "currentRentals": [
      {
        "rentId": 1,
        "isbn": "9788966262281",
        "title": "자바의 정석",
        "rentDate": "2026-06-01",
        "dueDate": "2026-06-22"
      }
    ]
  },
  "message": "대여 가능 여부를 확인했습니다."
}
```

### POST /rentals

도서를 대여한다.

관련 요구사항:

- FR-RENT-006
- FR-RENT-007
- FR-RENT-008
- FR-RENT-009

Request:

```json
{
  "isbn": "9788966262281",
  "userName": "홍길동",
  "userCode7": "9001151"
}
```

Response:

```json
{
  "success": true,
  "data": {
    "rentId": 10,
    "inventoryId": 3,
    "isbn": "9788966262281",
    "title": "자바의 정석",
    "rentDate": "2026-06-02",
    "dueDate": "2026-06-23"
  },
  "message": "대여가 완료되었습니다."
}
```

Possible Errors:

- EX-001
- EX-002
- EX-003
- EX-009
- EX-014

### GET /returns/candidates

반납 가능한 미반납 도서를 조회한다.

관련 요구사항:

- FR-RETURN-001
- FR-RETURN-002

Query Parameters:

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| userName | string | yes | 이용자 이름 |
| userCode7 | string | yes | 테스트용 7자리 식별 코드 |

Response:

```json
{
  "success": true,
  "data": [
    {
      "rentId": 1,
      "inventoryId": 3,
      "isbn": "9788966262281",
      "title": "자바의 정석",
      "rentDate": "2026-06-01",
      "dueDate": "2026-06-22",
      "overdue": false,
      "overdueDays": 0
    }
  ],
  "message": "반납 가능한 도서를 조회했습니다."
}
```

### POST /returns

도서를 반납한다.

관련 요구사항:

- FR-RETURN-003
- FR-RETURN-004
- FR-RETURN-005
- FR-RETURN-006
- FR-RETURN-007

Request:

```json
{
  "rentId": 1,
  "userName": "홍길동",
  "userCode7": "9001151"
}
```

Response:

```json
{
  "success": true,
  "data": {
    "rentId": 1,
    "isbn": "9788966262281",
    "title": "자바의 정석",
    "returnDate": "2026-06-02",
    "overdue": false,
    "overdueDays": 0
  },
  "message": "반납이 완료되었습니다. 감사합니다."
}
```

## 5. 관리자 API

### POST /admin/login

관리자 로그인을 수행한다.

관련 요구사항:

- FR-ADMIN-001
- EX-008

Request:

```json
{
  "adminId": "admin",
  "adminPw": "admin1234"
}
```

Response:

```json
{
  "success": true,
  "data": {
    "adminId": "admin",
    "adminName": "관리자"
  },
  "message": "로그인되었습니다."
}
```

### GET /admin/books

관리자용 전체 도서 및 재고 현황을 조회한다.

관련 요구사항:

- FR-ADMIN-002

Response:

```json
{
  "success": true,
  "data": [
    {
      "isbn": "9788966262281",
      "title": "자바의 정석",
      "author": "남궁성",
      "category": "프로그래밍",
      "publisher": "도우출판",
      "price": 30000,
      "totalCount": 3,
      "availableCount": 2,
      "rentCount": 12
    }
  ],
  "message": "전체 도서 목록을 조회했습니다."
}
```

### POST /admin/books

도서 마스터 또는 실물 재고를 추가한다.

관련 요구사항:

- FR-ADMIN-003
- EX-010

Request:

```json
{
  "isbn": "9788966262281",
  "title": "자바의 정석",
  "author": "남궁성",
  "publisher": "도우출판",
  "category": "프로그래밍",
  "price": 30000,
  "quantity": 3
}
```

Response:

```json
{
  "success": true,
  "data": {
    "isbn": "9788966262281",
    "addedInventoryCount": 3
  },
  "message": "도서가 추가되었습니다."
}
```

### DELETE /admin/books/{isbn}

도서 마스터와 전체 재고를 삭제한다.

관련 요구사항:

- FR-ADMIN-004
- EX-007

Response:

```json
{
  "success": true,
  "data": {
    "isbn": "9788966262281"
  },
  "message": "도서가 삭제되었습니다."
}
```

### DELETE /admin/inventories/{inventoryId}

실물 재고 1건을 삭제한다.

관련 요구사항:

- FR-ADMIN-004
- EX-007

Response:

```json
{
  "success": true,
  "data": {
    "inventoryId": 3
  },
  "message": "재고가 삭제되었습니다."
}
```

### GET /admin/rentals/current

현재 대여 중인 도서 목록을 조회한다.

관련 요구사항:

- FR-ADMIN-005

Query Parameters:

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| sort | string | no | rentDate, dueDate |
| direction | string | no | asc, desc |

Response:

```json
{
  "success": true,
  "data": [
    {
      "rentId": 1,
      "inventoryId": 3,
      "isbn": "9788966262281",
      "title": "자바의 정석",
      "userName": "홍길동",
      "userCode7Masked": "9001***",
      "rentDate": "2026-06-01",
      "dueDate": "2026-06-22",
      "overdue": false,
      "overdueDays": 0
    }
  ],
  "message": "대여 중인 도서 목록을 조회했습니다."
}
```

### GET /admin/users/history

특정 이용자의 전체 대여/반납 이력을 조회한다.

관련 요구사항:

- FR-ADMIN-006
- FR-ADMIN-007
- FR-ADMIN-012
- EX-005
- EX-013
- EX-014

Query Parameters:

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| userName | string | yes | 이용자 이름 |
| userCode7 | string | yes | 테스트용 7자리 식별 코드 |

Response:

```json
{
  "success": true,
  "data": {
    "userName": "홍길동",
    "userCode7Masked": "9001***",
    "blacklisted": false,
    "blacklistReasonCode": null,
    "totalRentCount": 3,
    "overdueCount": 1,
    "histories": [
      {
        "rentId": 1,
        "isbn": "9788966262281",
        "title": "자바의 정석",
        "rentDate": "2026-05-01",
        "dueDate": "2026-05-22",
        "returnDate": "2026-05-25",
        "status": "RETURNED",
        "overdue": true,
        "overdueDays": 3
      }
    ],
    "canBlacklist": true
  },
  "message": "이용자 반납 이력을 조회했습니다."
}
```

### GET /admin/blacklist-reasons

불량 지정 사유 목록을 조회한다.

관련 요구사항:

- FR-ADMIN-009
- FR-ADMIN-010

Response:

```json
{
  "success": true,
  "data": [
    {
      "reasonCode": "OVERDUE_REPEAT",
      "reasonLabel": "반복 연체",
      "description": "3회 이상 반납 기한 초과 이력"
    }
  ],
  "message": "불량 지정 사유를 조회했습니다."
}
```

### POST /admin/users/blacklist

이용자를 불량 이용자로 지정한다.

관련 요구사항:

- FR-ADMIN-008
- FR-ADMIN-009
- FR-ADMIN-010
- FR-ADMIN-011
- EX-011
- EX-012

Request:

```json
{
  "userName": "홍길동",
  "userCode7": "9001151",
  "reasonCode": "OVERDUE_REPEAT",
  "memo": null
}
```

Response:

```json
{
  "success": true,
  "data": {
    "userName": "홍길동",
    "userCode7Masked": "9001***",
    "blacklisted": true,
    "reasonCode": "OVERDUE_REPEAT",
    "blacklistedAt": "2026-06-02T14:30:00"
  },
  "message": "불량 이용자로 지정되었습니다."
}
```

### GET /admin/users/blacklist

불량 이용자 목록을 조회한다.

관련 요구사항:

- FR-ADMIN-013

Response:

```json
{
  "success": true,
  "data": [
    {
      "userName": "이영희",
      "userCode7Masked": "9507***",
      "reasonCode": "OVERDUE_LONG",
      "reasonLabel": "장기 연체",
      "blacklistedAt": "2026-06-01T10:00:00"
    }
  ],
  "message": "불량 이용자 목록을 조회했습니다."
}
```

### PATCH /admin/users/blacklist/release

불량 이용자 상태를 해제한다.

관련 요구사항:

- FR-ADMIN-013

Request:

```json
{
  "userName": "이영희",
  "userCode7": "9507072"
}
```

Response:

```json
{
  "success": true,
  "data": {
    "userName": "이영희",
    "userCode7Masked": "9507***",
    "blacklisted": false
  },
  "message": "불량 이용자 상태가 해제되었습니다."
}
```
