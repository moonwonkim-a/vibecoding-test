package com.example.library.book.dto;

public class RankingItemDto {
    private final int rank;
    private final String isbn;
    private final String title;
    private final String author;
    private final String category;
    private final int rentCount;

    public RankingItemDto(int rank, String isbn, String title, String author, String category, int rentCount) {
        this.rank = rank;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.rentCount = rentCount;
    }

    public int getRank() { return rank; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public int getRentCount() { return rentCount; }
}
