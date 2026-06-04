package com.example.library.rental.dto;

import java.time.LocalDate;

public class RentalResponseDto {
    private final Long rentId;
    private final Long inventoryId;
    private final String isbn;
    private final String title;
    private final LocalDate rentDate;
    private final LocalDate dueDate;

    public RentalResponseDto(Long rentId, Long inventoryId, String isbn, String title,
                              LocalDate rentDate, LocalDate dueDate) {
        this.rentId = rentId;
        this.inventoryId = inventoryId;
        this.isbn = isbn;
        this.title = title;
        this.rentDate = rentDate;
        this.dueDate = dueDate;
    }

    public Long getRentId() { return rentId; }
    public Long getInventoryId() { return inventoryId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public LocalDate getRentDate() { return rentDate; }
    public LocalDate getDueDate() { return dueDate; }
}
