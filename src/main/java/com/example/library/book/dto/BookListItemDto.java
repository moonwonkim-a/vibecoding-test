package com.example.library.book.dto;

public record BookListItemDto(
        String isbn,
        String title,
        String author,
        String publisher,
        String category,
        Integer price,
        long totalCount,
        long availableCount,
        Integer rentCount,
        boolean rentAvailable
) {
}
