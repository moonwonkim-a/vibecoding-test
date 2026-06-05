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

    @Query(value = "SELECT r.* FROM library_rent_records r WHERE r.user_code7 = :userCode7 ORDER BY r.rent_date DESC", nativeQuery = true)
    List<LibraryRentRecord> findAllByUserCode7(@Param("userCode7") String userCode7);

    @Query("SELECT r FROM LibraryRentRecord r JOIN FETCH r.bookInfo JOIN FETCH r.user WHERE r.returnDate IS NOT NULL ORDER BY r.returnDate DESC, r.rentId DESC")
    List<LibraryRentRecord> findAllReturnedRecords();
}
