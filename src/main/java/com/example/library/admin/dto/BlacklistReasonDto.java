package com.example.library.admin.dto;

public class BlacklistReasonDto {
    private final String reasonCode;
    private final String reasonLabel;
    private final String description;

    public BlacklistReasonDto(String reasonCode, String reasonLabel, String description) {
        this.reasonCode = reasonCode;
        this.reasonLabel = reasonLabel;
        this.description = description;
    }

    public String getReasonCode() { return reasonCode; }
    public String getReasonLabel() { return reasonLabel; }
    public String getDescription() { return description; }
}
