package com.example.library.rental.repository;

import com.example.library.rental.entity.LibraryRentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRentRecordRepository extends JpaRepository<LibraryRentRecord, Long> {
}
