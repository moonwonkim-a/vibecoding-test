package com.example.library.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "library_admins")
public class LibraryAdmin {

    @Id
    @Column(name = "admin_id", length = 50, nullable = false)
    private String adminId;

    @Column(name = "admin_pw", length = 256, nullable = false)
    private String adminPw;

    @Column(name = "admin_name", length = 100, nullable = false)
    private String adminName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected LibraryAdmin() {
    }

    public LibraryAdmin(String adminId, String adminPw, String adminName, LocalDateTime createdAt) {
        this.adminId = adminId;
        this.adminPw = adminPw;
        this.adminName = adminName;
        this.createdAt = createdAt;
    }
}
