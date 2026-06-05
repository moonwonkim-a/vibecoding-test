package com.example.library.book.repository;

import com.example.library.book.entity.LibraryBookInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LibraryBookInfoRepository extends JpaRepository<LibraryBookInfo, String> {

    @Modifying
    @Query("UPDATE LibraryBookInfo b SET b.delYn = 'Y' WHERE b.isbn = :isbn")
    void softDeleteByIsbn(@Param("isbn") String isbn);
}
