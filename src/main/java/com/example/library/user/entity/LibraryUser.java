package com.example.library.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "library_users")
public class LibraryUser {

    @Id
    @Column(name = "user_code7", length = 7, nullable = false)
    private String userCode7;

    @Column(name = "user_name", length = 100, nullable = false)
    private String userName;

    @Column(name = "is_blacklisted", nullable = false)
    private boolean blacklisted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blacklist_reason_code")
    private LibraryBlacklistReason blacklistReason;

    @Column(name = "blacklist_memo", length = 500)
    private String blacklistMemo;

    @Column(name = "blacklisted_at")
    private LocalDateTime blacklistedAt;

    protected LibraryUser() {
    }

    public LibraryUser(String userCode7, String userName) {
        this.userCode7 = userCode7;
        this.userName = userName;
    }

    public void markBlacklisted(LibraryBlacklistReason reason, String memo, LocalDateTime at) {
        this.blacklisted = true;
        this.blacklistReason = reason;
        this.blacklistMemo = memo;
        this.blacklistedAt = at;
    }

    public String getUserCode7() {
        return userCode7;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }

    public LibraryBlacklistReason getBlacklistReason() {
        return blacklistReason;
    }

    public String getBlacklistMemo() {
        return blacklistMemo;
    }

    public LocalDateTime getBlacklistedAt() {
        return blacklistedAt;
    }
}
