package com.example.library.book.controller;

import com.example.library.book.dto.BookDetailDto;
import com.example.library.book.dto.BookListResponseDto;
import com.example.library.book.service.BookService;
import com.example.library.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ApiResponse<BookListResponseDto> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction
    ) {
        BookListResponseDto data = bookService.getBooks(page, size, keyword, filter, sort, direction);
        return ApiResponse.success(data, "도서 목록을 조회했습니다.");
    }

    @GetMapping("/{isbn}")
    public ApiResponse<BookDetailDto> getBookDetail(@PathVariable String isbn) {
        BookDetailDto data = bookService.getBookDetail(isbn);
        return ApiResponse.success(data, "도서 상세 정보를 조회했습니다.");
    }
}
