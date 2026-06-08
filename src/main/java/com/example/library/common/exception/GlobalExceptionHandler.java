package com.example.library.common.exception;

import com.example.library.common.response.ApiResponse;
import jakarta.persistence.PessimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        ErrorCode code = e.getErrorCode();
        ApiResponse<?> body = ApiResponse.fail(code.getCode(), code.getMessage(), code.getCode());
        return ResponseEntity.status(code.getHttpStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);

        if (fieldError != null && "userCode7".equals(fieldError.getField())) {
            ErrorCode code = ErrorCode.EX_014;
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(code.getCode(), code.getMessage(), code.getCode()));
        }

        if (fieldError != null && "isbn".equals(fieldError.getField())) {
            ErrorCode code = ErrorCode.EX_010;
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(code.getCode(), code.getMessage(), code.getCode()));
        }

        String message = fieldError != null
                ? fieldError.getDefaultMessage()
                : "입력값이 올바르지 않습니다.";
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail("VALIDATION", message, null));
    }

    @ExceptionHandler({
            PessimisticLockException.class,
            PessimisticLockingFailureException.class,
            CannotAcquireLockException.class
    })
    public ResponseEntity<ApiResponse<?>> handleLockException(Exception e) {
        log.warn("[EX-009] 동시 요청 충돌: {}", e.getMessage());
        ErrorCode code = ErrorCode.EX_009;
        ApiResponse<?> body = ApiResponse.fail(code.getCode(), code.getMessage(), code.getCode());
        return ResponseEntity.status(code.getHttpStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("[SERVER_ERROR] 처리되지 않은 예외 발생: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("SERVER_ERROR", "서버 오류가 발생하였습니다.", null));
    }
}
