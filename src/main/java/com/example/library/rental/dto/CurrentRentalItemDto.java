package com.example.library.rental.dto;

import java.time.LocalDate;

public class CurrentRentalItemDto {
    private final Long rentId;
    private final String isbn;
    private final String title;
    private final LocalDate rentDate;
    private final LocalDate dueDate;

    public CurrentRentalItemDto(Long rentId, String isbn, String title, LocalDate rentDate, LocalDate dueDate) {
        this.rentId = rentId;
        this.isbn = isbn;
        this.title = title;
        this.rentDate = rentDate;
        this.dueDate = dueDate;
    }

    public Long getRentId() { return rentId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public LocalDate getRentDate() { return rentDate; }
    public LocalDate getDueDate() { return dueDate; }
}
