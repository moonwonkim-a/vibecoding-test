package com.example.library.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;
    private final String errorCode;
    private final String requirementId;

    private ApiResponse(boolean success, T data, String message, String errorCode, String requirementId) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.errorCode = errorCode;
        this.requirementId = requirementId;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null, null);
    }

    public static <T> ApiResponse<T> fail(String errorCode, String message, String requirementId) {
        return new ApiResponse<>(false, null, message, errorCode, requirementId);
    }

    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getMessage() { return message; }
    public String getErrorCode() { return errorCode; }
    public String getRequirementId() { return requirementId; }
}
