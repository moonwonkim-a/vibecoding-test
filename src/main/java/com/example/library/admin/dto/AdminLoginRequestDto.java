package com.example.library.admin.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminLoginRequestDto {

    @NotBlank(message = "아이디를 입력해 주세요.")
    private String adminId;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String adminPw;

    public String getAdminId() { return adminId; }
    public String getAdminPw() { return adminPw; }
}
