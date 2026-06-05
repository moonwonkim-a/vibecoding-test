package com.example.library.admin.dto;

import java.util.List;

public class UserHistoryResponseDto {
    private final String userName;
    private final String userCode7Masked;
    private final boolean blacklisted;
    private final String blacklistReasonCode;
    private final int totalRentCount;
    private final int overdueCount;
    private final List<UserHistoryItemDto> histories;
    private final boolean canBlacklist;

    public UserHistoryResponseDto(String userName, String userCode7Masked, boolean blacklisted,
                                   String blacklistReasonCode, int totalRentCount, int overdueCount,
                                   List<UserHistoryItemDto> histories, boolean canBlacklist) {
        this.userName = userName;
        this.userCode7Masked = userCode7Masked;
        this.blacklisted = blacklisted;
        this.blacklistReasonCode = blacklistReasonCode;
        this.totalRentCount = totalRentCount;
        this.overdueCount = overdueCount;
        this.histories = histories;
        this.canBlacklist = canBlacklist;
    }

    public String getUserName() { return userName; }
    public String getUserCode7Masked() { return userCode7Masked; }
    public boolean isBlacklisted() { return blacklisted; }
    public String getBlacklistReasonCode() { return blacklistReasonCode; }
    public int getTotalRentCount() { return totalRentCount; }
    public int getOverdueCount() { return overdueCount; }
    public List<UserHistoryItemDto> getHistories() { return histories; }
    public boolean isCanBlacklist() { return canBlacklist; }
}
