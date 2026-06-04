package com.example.library.admin.dto;

import jakarta.validation.constraints.NotBlank;

public class BlacklistReleaseRequestDto {

    @NotBlank(message = "이름을 입력해 주세요.")
    private String userName;

    @NotBlank(message = "이용자 식별 코드를 입력해 주세요.")
    private String userCode7Masked;

    public String getUserName() { return userName; }
    public String getUserCode7Masked() { return userCode7Masked; }
}
