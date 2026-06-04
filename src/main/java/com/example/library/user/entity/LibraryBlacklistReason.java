package com.example.library.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "library_blacklist_reasons")
public class LibraryBlacklistReason {

    @Id
    @Column(name = "reason_code", length = 20, nullable = false)
    private String reasonCode;

    @Column(name = "reason_label", length = 100, nullable = false)
    private String reasonLabel;

    @Column(name = "description", length = 300)
    private String description;

    protected LibraryBlacklistReason() {
    }

    public LibraryBlacklistReason(String reasonCode, String reasonLabel, String description) {
        this.reasonCode = reasonCode;
        this.reasonLabel = reasonLabel;
        this.description = description;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public String getReasonLabel() {
        return reasonLabel;
    }

    public String getDescription() {
        return description;
    }
}
