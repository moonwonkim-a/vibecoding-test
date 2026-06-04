package com.example.library.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    EX_001("EX-001", "현재 대여 한도(2권)를 초과하였습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    EX_002("EX-002", "불량 이용자로 대여가 불가합니다.", HttpStatus.FORBIDDEN),
    EX_003("EX-003", "현재 모든 재고가 대여 중입니다.", HttpStatus.CONFLICT),
    EX_004("EX-004", "반납 기한이 초과되었습니다.", HttpStatus.OK),
    EX_005("EX-005", "입력하신 정보와 일치하는 이용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    EX_006("EX-006", "검색 결과가 없습니다.", HttpStatus.OK),
    EX_007("EX-007", "대여 중인 재고가 있어 삭제할 수 없습니다.", HttpStatus.CONFLICT),
    EX_008("EX-008", "아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    EX_009("EX-009", "요청 처리 중 오류가 발생하였습니다. 다시 시도해 주세요.", HttpStatus.CONFLICT),
    EX_010("EX-010", "올바른 ISBN 형식이 아닙니다.", HttpStatus.BAD_REQUEST),
    EX_011("EX-011", "이미 불량 이용자로 지정되어 있습니다.", HttpStatus.CONFLICT),
    EX_012("EX-012", "사유를 선택해 주세요.", HttpStatus.BAD_REQUEST),
    EX_013("EX-013", "해당 이용자의 대여 이력이 없습니다.", HttpStatus.OK),
    EX_014("EX-014", "이용자 식별 코드는 숫자 7자리여야 합니다.", HttpStatus.BAD_REQUEST),
    EX_015("EX-015", "입력하신 이름이 기존 이용자 정보와 일치하지 않습니다.", HttpStatus.CONFLICT),
    AUTH_001("AUTH-001", "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public HttpStatus getHttpStatus() { return httpStatus; }
}
