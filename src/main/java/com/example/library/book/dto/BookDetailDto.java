package com.example.library.book.dto;

public record BookDetailDto(
        String isbn,
        String title,
        String author,
        String publisher,
        String category,
        Integer price,
        long totalCount,
        long availableCount,
        boolean rentAvailable
) {
}
