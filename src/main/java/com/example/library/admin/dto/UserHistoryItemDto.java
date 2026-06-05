package com.example.library.admin.dto;

import java.time.LocalDate;

public class UserHistoryItemDto {
    private final Long rentId;
    private final String isbn;
    private final String title;
    private final LocalDate rentDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate;
    private final String status;
    private final boolean overdue;
    private final int overdueDays;

    public UserHistoryItemDto(Long rentId, String isbn, String title, LocalDate rentDate,
                               LocalDate dueDate, LocalDate returnDate, String status,
                               boolean overdue, int overdueDays) {
        this.rentId = rentId;
        this.isbn = isbn;
        this.title = title;
        this.rentDate = rentDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.overdue = overdue;
        this.overdueDays = overdueDays;
    }

    public Long getRentId() { return rentId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public LocalDate getRentDate() { return rentDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getStatus() { return status; }
    public boolean isOverdue() { return overdue; }
    public int getOverdueDays() { return overdueDays; }
}
