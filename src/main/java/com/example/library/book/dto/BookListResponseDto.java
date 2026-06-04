package com.example.library.book.dto;

import java.util.List;

public record BookListResponseDto(
        List<BookListItemDto> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
