package com.example.library.book.repository;

import com.example.library.book.entity.LibraryBookInfo;
import com.example.library.book.entity.LibraryBookInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibraryBookInventoryRepository extends JpaRepository<LibraryBookInventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM LibraryBookInventory i WHERE i.bookInfo = :bookInfo AND i.available = true AND i.delYn = 'N' ORDER BY i.inventoryId ASC LIMIT 1")
    Optional<LibraryBookInventory> findFirstAvailableWithLock(@Param("bookInfo") LibraryBookInfo bookInfo);

    @Query("SELECT COUNT(i) FROM LibraryBookInventory i WHERE i.bookInfo = :bookInfo AND i.available = false AND i.delYn = 'N'")
    long countRentedByBook(@Param("bookInfo") LibraryBookInfo bookInfo);

    @Query("SELECT i FROM LibraryBookInventory i WHERE i.bookInfo.isbn = :isbn AND (i.delYn IS NULL OR i.delYn = 'N') ORDER BY i.inventoryId ASC")
    List<LibraryBookInventory> findAllActiveByIsbn(@Param("isbn") String isbn);

    @Modifying
    @Query("UPDATE LibraryBookInventory i SET i.delYn = 'Y' WHERE i.inventoryId = :id")
    void softDeleteById(@Param("id") Long inventoryId);

    @Modifying
    @Query("UPDATE LibraryBookInventory i SET i.delYn = 'Y' WHERE i.bookInfo.isbn = :isbn AND (i.delYn IS NULL OR i.delYn = 'N')")
    void softDeleteAllByIsbn(@Param("isbn") String isbn);

    List<LibraryBookInventory> findAllByBookInfo(LibraryBookInfo bookInfo);
}
