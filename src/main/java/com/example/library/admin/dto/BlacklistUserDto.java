package com.example.library.admin.dto;

import java.time.LocalDateTime;

public class BlacklistUserDto {
    private final String userName;
    private final String userCode7Masked;
    private final String reasonCode;
    private final String reasonLabel;
    private final LocalDateTime blacklistedAt;

    public BlacklistUserDto(String userName, String userCode7Masked, String reasonCode,
                             String reasonLabel, LocalDateTime blacklistedAt) {
        this.userName = userName;
        this.userCode7Masked = userCode7Masked;
        this.reasonCode = reasonCode;
        this.reasonLabel = reasonLabel;
        this.blacklistedAt = blacklistedAt;
    }

    public String getUserName() { return userName; }
    public String getUserCode7Masked() { return userCode7Masked; }
    public String getReasonCode() { return reasonCode; }
    public String getReasonLabel() { return reasonLabel; }
    public LocalDateTime getBlacklistedAt() { return blacklistedAt; }
}
