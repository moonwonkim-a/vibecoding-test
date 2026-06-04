package com.example.library.admin.repository;

import com.example.library.admin.entity.LibraryAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryAdminRepository extends JpaRepository<LibraryAdmin, String> {
}
