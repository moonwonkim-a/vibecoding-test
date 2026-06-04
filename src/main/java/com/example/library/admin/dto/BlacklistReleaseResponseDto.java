package com.example.library.admin.dto;

public class BlacklistReleaseResponseDto {
    private final String userName;
    private final String userCode7Masked;
    private final boolean blacklisted;

    public BlacklistReleaseResponseDto(String userName, String userCode7Masked, boolean blacklisted) {
        this.userName = userName;
        this.userCode7Masked = userCode7Masked;
        this.blacklisted = blacklisted;
    }

    public String getUserName() { return userName; }
    public String getUserCode7Masked() { return userCode7Masked; }
    public boolean isBlacklisted() { return blacklisted; }
}
