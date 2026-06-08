package com.example.library.user.repository;

import com.example.library.user.entity.LibraryUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibraryUserRepository extends JpaRepository<LibraryUser, String> {

    @Query("SELECT u FROM LibraryUser u LEFT JOIN FETCH u.blacklistReason WHERE u.blacklisted = true ORDER BY u.blacklistedAt DESC")
    List<LibraryUser> findAllBlacklisted();

    @Query("SELECT u FROM LibraryUser u LEFT JOIN FETCH u.blacklistReason WHERE u.blacklisted = true AND u.userName = :userName AND u.userCode7 LIKE CONCAT(:prefix, '%')")
    Optional<LibraryUser> findBlacklistedByUserNameAndCodePrefix(
            @Param("userName") String userName,
            @Param("prefix") String prefix
    );
}
