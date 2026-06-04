package com.example.library.admin.dto;

import java.time.LocalDate;

public class AdminCurrentRentalDto {
    private final Long rentId;
    private final Long inventoryId;
    private final String isbn;
    private final String title;
    private final String userName;
    private final String userCode7Masked;
    private final LocalDate rentDate;
    private final LocalDate dueDate;
    private final boolean overdue;
    private final int overdueDays;

    public AdminCurrentRentalDto(Long rentId, Long inventoryId, String isbn, String title,
                                  String userName, String userCode7Masked, LocalDate rentDate,
                                  LocalDate dueDate, boolean overdue, int overdueDays) {
        this.rentId = rentId;
        this.inventoryId = inventoryId;
        this.isbn = isbn;
        this.title = title;
        this.userName = userName;
        this.userCode7Masked = userCode7Masked;
        this.rentDate = rentDate;
        this.dueDate = dueDate;
        this.overdue = overdue;
        this.overdueDays = overdueDays;
    }

    public Long getRentId() { return rentId; }
    public Long getInventoryId() { return inventoryId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getUserName() { return userName; }
    public String getUserCode7Masked() { return userCode7Masked; }
    public LocalDate getRentDate() { return rentDate; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isOverdue() { return overdue; }
    public int getOverdueDays() { return overdueDays; }
}
