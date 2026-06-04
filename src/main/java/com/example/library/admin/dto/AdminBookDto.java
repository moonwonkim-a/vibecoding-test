package com.example.library.admin.dto;

public class AdminBookDto {
    private final String isbn;
    private final String title;
    private final String author;
    private final String category;
    private final String publisher;
    private final int price;
    private final long totalCount;
    private final long availableCount;
    private final int rentCount;

    public AdminBookDto(String isbn, String title, String author, String category, String publisher,
                        int price, long totalCount, long availableCount, int rentCount) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publisher = publisher;
        this.price = price;
        this.totalCount = totalCount;
        this.availableCount = availableCount;
        this.rentCount = rentCount;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getPublisher() { return publisher; }
    public int getPrice() { return price; }
    public long getTotalCount() { return totalCount; }
    public long getAvailableCount() { return availableCount; }
    public int getRentCount() { return rentCount; }
}
