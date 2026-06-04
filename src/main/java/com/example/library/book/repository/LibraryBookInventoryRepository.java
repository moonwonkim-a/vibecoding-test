package com.example.library.book.repository;

import com.example.library.book.entity.LibraryBookInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryBookInventoryRepository extends JpaRepository<LibraryBookInventory, Long> {
}
