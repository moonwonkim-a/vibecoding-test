package com.example.library.book.repository;

import com.example.library.book.entity.LibraryBookInfo;
import com.example.library.book.entity.LibraryBookInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibraryBookInventoryRepository extends JpaRepository<LibraryBookInventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM LibraryBookInventory i WHERE i.bookInfo = :bookInfo AND i.available = true ORDER BY i.inventoryId ASC LIMIT 1")
    Optional<LibraryBookInventory> findFirstAvailableWithLock(@Param("bookInfo") LibraryBookInfo bookInfo);

    @Query("SELECT COUNT(i) FROM LibraryBookInventory i WHERE i.bookInfo = :bookInfo AND i.available = false")
    long countRentedByBook(@Param("bookInfo") LibraryBookInfo bookInfo);

    List<LibraryBookInventory> findAllByBookInfo(LibraryBookInfo bookInfo);
}
