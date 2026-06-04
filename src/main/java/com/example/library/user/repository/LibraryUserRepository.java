package com.example.library.user.repository;

import com.example.library.user.entity.LibraryUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryUserRepository extends JpaRepository<LibraryUser, String> {
}
