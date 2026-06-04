package com.example.library.rental.dto;

import java.time.LocalDate;

public class ReturnCandidateDto {
    private final Long rentId;
    private final Long inventoryId;
    private final String isbn;
    private final String title;
    private final LocalDate rentDate;
    private final LocalDate dueDate;
    private final boolean overdue;
    private final int overdueDays;

    public ReturnCandidateDto(Long rentId, Long inventoryId, String isbn, String title,
                               LocalDate rentDate, LocalDate dueDate, boolean overdue, int overdueDays) {
        this.rentId = rentId;
        this.inventoryId = inventoryId;
        this.isbn = isbn;
        this.title = title;
        this.rentDate = rentDate;
        this.dueDate = dueDate;
        this.overdue = overdue;
        this.overdueDays = overdueDays;
    }

    public Long getRentId() { return rentId; }
    public Long getInventoryId() { return inventoryId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public LocalDate getRentDate() { return rentDate; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isOverdue() { return overdue; }
    public int getOverdueDays() { return overdueDays; }
}
