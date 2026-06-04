package com.example.library.book.repository;

import com.example.library.book.entity.LibraryBookInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryBookInfoRepository extends JpaRepository<LibraryBookInfo, String> {
}
