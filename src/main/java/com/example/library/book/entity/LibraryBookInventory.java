package com.example.library.book.entity;

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
@Table(name = "library_book_inventory")
public class LibraryBookInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long inventoryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "isbn", nullable = false)
    private LibraryBookInfo bookInfo;

    @Column(name = "available", nullable = false)
    private boolean available = true;

    @Column(name = "rent_date")
    private LocalDate rentDate;

    protected LibraryBookInventory() {
    }

    public LibraryBookInventory(LibraryBookInfo bookInfo, boolean available, LocalDate rentDate) {
        this.bookInfo = bookInfo;
        this.available = available;
        this.rentDate = rentDate;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public LibraryBookInfo getBookInfo() {
        return bookInfo;
    }

    public boolean isAvailable() {
        return available;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public void markRented(LocalDate rentDate) {
        this.available = false;
        this.rentDate = rentDate;
    }

    public void markReturned() {
        this.available = true;
        this.rentDate = null;
    }
}
