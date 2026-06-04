package com.example.library.rental.repository;

import com.example.library.rental.entity.LibraryRentRecord;
import com.example.library.user.entity.LibraryUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LibraryRentRecordRepository extends JpaRepository<LibraryRentRecord, Long> {

    @Query("SELECT r FROM LibraryRentRecord r JOIN FETCH r.bookInfo JOIN FETCH r.inventory WHERE r.user = :user AND r.returnDate IS NULL")
    List<LibraryRentRecord> findCurrentRentalsByUser(@Param("user") LibraryUser user);

    @Query("SELECT COUNT(r) FROM LibraryRentRecord r WHERE r.user = :user AND r.returnDate IS NULL")
    long countCurrentRentalsByUser(@Param("user") LibraryUser user);

    @Query("SELECT r FROM LibraryRentRecord r JOIN FETCH r.bookInfo JOIN FETCH r.inventory WHERE r.returnDate IS NULL ORDER BY r.rentDate DESC")
    List<LibraryRentRecord> findAllCurrentRentals();

    @Query("SELECT r FROM LibraryRentRecord r JOIN FETCH r.bookInfo JOIN FETCH r.inventory WHERE r.user = :user ORDER BY r.rentDate DESC")
    List<LibraryRentRecord> findAllByUser(@Param("user") LibraryUser user);
}
