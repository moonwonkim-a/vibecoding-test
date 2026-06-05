package com.example.library.admin.dto;

import java.time.LocalDate;

public class ReturnHistoryItemDto {
    private final Long rentId;
    private final String isbn;
    private final String title;
    private final String userName;
    private final String userCode7;
    private final String userCode7Masked;
    private final LocalDate rentDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate;
    private final boolean overdue;
    private final int overdueDays;
    private final boolean blacklisted;
    private final boolean canBlacklist;

    public ReturnHistoryItemDto(Long rentId, String isbn, String title, String userName,
                                 String userCode7, String userCode7Masked, LocalDate rentDate,
                                 LocalDate dueDate, LocalDate returnDate, boolean overdue,
                                 int overdueDays, boolean blacklisted, boolean canBlacklist) {
        this.rentId = rentId;
        this.isbn = isbn;
        this.title = title;
        this.userName = userName;
        this.userCode7 = userCode7;
        this.userCode7Masked = userCode7Masked;
        this.rentDate = rentDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.overdue = overdue;
        this.overdueDays = overdueDays;
        this.blacklisted = blacklisted;
        this.canBlacklist = canBlacklist;
    }

    public Long getRentId() { return rentId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getUserName() { return userName; }
    public String getUserCode7() { return userCode7; }
    public String getUserCode7Masked() { return userCode7Masked; }
    public LocalDate getRentDate() { return rentDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isOverdue() { return overdue; }
    public int getOverdueDays() { return overdueDays; }
    public boolean isBlacklisted() { return blacklisted; }
    public boolean isCanBlacklist() { return canBlacklist; }
}
