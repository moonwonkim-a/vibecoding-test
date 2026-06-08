package com.example.library.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "library_book_info")
public class LibraryBookInfo {

    @Id
    @Column(name = "isbn", length = 13, nullable = false)
    private String isbn;

    @Column(name = "title", length = 300, nullable = false)
    private String title;

    @Column(name = "author", length = 200, nullable = false)
    private String author;

    @Column(name = "publisher", length = 200, nullable = false)
    private String publisher;

    @Column(name = "category", length = 100, nullable = false)
    private String category;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "rent_count", nullable = false)
    private Integer rentCount = 0;

    @Column(name = "del_yn", nullable = false, length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String delYn = "N";

    protected LibraryBookInfo() {
    }

    public LibraryBookInfo(String isbn, String title, String author, String publisher, String category, Integer price, Integer rentCount) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.category = category;
        this.price = price;
        this.rentCount = rentCount;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getCategory() {
        return category;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getRentCount() {
        return rentCount;
    }

    public void incrementRentCount() {
        this.rentCount++;
    }

    public boolean isDeleted() {
        return "Y".equals(this.delYn);
    }

    public void softDelete() {
        this.delYn = "Y";
    }

    public void restore(String title, String author, String publisher, String category, Integer price) {
        this.delYn = "N";
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.category = category;
        this.price = price;
    }
}
