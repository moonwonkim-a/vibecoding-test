package com.example.library.user.repository;

import com.example.library.user.entity.LibraryUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibraryUserRepository extends JpaRepository<LibraryUser, String> {

    @Query("SELECT u FROM LibraryUser u LEFT JOIN FETCH u.blacklistReason WHERE u.blacklisted = true ORDER BY u.blacklistedAt DESC")
    List<LibraryUser> findAllBlacklisted();
}
