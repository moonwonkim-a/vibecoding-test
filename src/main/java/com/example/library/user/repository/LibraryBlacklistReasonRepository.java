package com.example.library.user.repository;

import com.example.library.user.entity.LibraryBlacklistReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryBlacklistReasonRepository extends JpaRepository<LibraryBlacklistReason, String> {
}
