package com.example.library.admin.dto;

import java.time.LocalDateTime;

public class BlacklistResponseDto {
    private final String userName;
    private final String userCode7Masked;
    private final boolean blacklisted;
    private final String reasonCode;
    private final LocalDateTime blacklistedAt;

    public BlacklistResponseDto(String userName, String userCode7Masked, boolean blacklisted,
                                 String reasonCode, LocalDateTime blacklistedAt) {
        this.userName = userName;
        this.userCode7Masked = userCode7Masked;
        this.blacklisted = blacklisted;
        this.reasonCode = reasonCode;
        this.blacklistedAt = blacklistedAt;
    }

    public String getUserName() { return userName; }
    public String getUserCode7Masked() { return userCode7Masked; }
    public boolean isBlacklisted() { return blacklisted; }
    public String getReasonCode() { return reasonCode; }
    public LocalDateTime getBlacklistedAt() { return blacklistedAt; }
}
