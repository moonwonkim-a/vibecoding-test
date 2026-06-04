package com.example.library.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class BlacklistRequestDto {

    @NotBlank(message = "이름을 입력해 주세요.")
    private String userName;

    @NotBlank(message = "이용자 식별 코드를 입력해 주세요.")
    @Pattern(regexp = "\\d{7}", message = "이용자 식별 코드는 숫자 7자리여야 합니다.")
    private String userCode7;

    @NotBlank(message = "사유를 선택해 주세요.")
    private String reasonCode;

    private String memo;

    public String getUserName() { return userName; }
    public String getUserCode7() { return userCode7; }
    public String getReasonCode() { return reasonCode; }
    public String getMemo() { return memo; }
}
