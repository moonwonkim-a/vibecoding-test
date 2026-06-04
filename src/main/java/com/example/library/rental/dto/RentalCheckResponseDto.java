package com.example.library.rental.dto;

import java.util.List;

public class RentalCheckResponseDto {
    private final String userName;
    private final String userCode7Masked;
    private final boolean blacklisted;
    private final int currentRentCount;
    private final boolean canRent;
    private final List<CurrentRentalItemDto> currentRentals;

    public RentalCheckResponseDto(String userName, String userCode7Masked, boolean blacklisted,
                                   int currentRentCount, boolean canRent, List<CurrentRentalItemDto> currentRentals) {
        this.userName = userName;
        this.userCode7Masked = userCode7Masked;
        this.blacklisted = blacklisted;
        this.currentRentCount = currentRentCount;
        this.canRent = canRent;
        this.currentRentals = currentRentals;
    }

    public String getUserName() { return userName; }
    public String getUserCode7Masked() { return userCode7Masked; }
    public boolean isBlacklisted() { return blacklisted; }
    public int getCurrentRentCount() { return currentRentCount; }
    public boolean isCanRent() { return canRent; }
    public List<CurrentRentalItemDto> getCurrentRentals() { return currentRentals; }
}
