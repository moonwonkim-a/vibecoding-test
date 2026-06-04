package com.example.library.rental.dto;

import java.time.LocalDate;

public class ReturnResponseDto {
    private final Long rentId;
    private final String isbn;
    private final String title;
    private final LocalDate returnDate;
    private final boolean overdue;
    private final int overdueDays;

    public ReturnResponseDto(Long rentId, String isbn, String title,
                              LocalDate returnDate, boolean overdue, int overdueDays) {
        this.rentId = rentId;
        this.isbn = isbn;
        this.title = title;
        this.returnDate = returnDate;
        this.overdue = overdue;
        this.overdueDays = overdueDays;
    }

    public Long getRentId() { return rentId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isOverdue() { return overdue; }
    public int getOverdueDays() { return overdueDays; }
}
