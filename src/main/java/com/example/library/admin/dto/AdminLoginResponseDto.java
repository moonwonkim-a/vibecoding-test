package com.example.library.admin.dto;

public class AdminLoginResponseDto {
    private final String adminId;
    private final String adminName;

    public AdminLoginResponseDto(String adminId, String adminName) {
        this.adminId = adminId;
        this.adminName = adminName;
    }

    public String getAdminId() { return adminId; }
    public String getAdminName() { return adminName; }
}
