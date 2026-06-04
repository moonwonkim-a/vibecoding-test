package com.example.library.rental.entity;

import com.example.library.book.entity.LibraryBookInfo;
import com.example.library.book.entity.LibraryBookInventory;
import com.example.library.user.entity.LibraryUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "library_rent_records")
public class LibraryRentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rent_id")
    private Long rentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_id", nullable = false)
    private LibraryBookInventory inventory;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "isbn", nullable = false)
    private LibraryBookInfo bookInfo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_code7", nullable = false)
    private LibraryUser user;

    @Column(name = "user_name", length = 100, nullable = false)
    private String userName;

    @Column(name = "rent_date", nullable = false)
    private LocalDate rentDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "is_overdue", nullable = false)
    private boolean overdue = false;

    protected LibraryRentRecord() {
    }

    public LibraryRentRecord(
            LibraryBookInventory inventory,
            LibraryBookInfo bookInfo,
            LibraryUser user,
            String userName,
            LocalDate rentDate,
            LocalDate dueDate,
            LocalDate returnDate,
            boolean overdue
    ) {
        this.inventory = inventory;
        this.bookInfo = bookInfo;
        this.user = user;
        this.userName = userName;
        this.rentDate = rentDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.overdue = overdue;
    }
}
