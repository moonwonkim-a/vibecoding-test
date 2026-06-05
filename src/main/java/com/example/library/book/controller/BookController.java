package com.example.library.book.controller;

import com.example.library.book.dto.BookDetailDto;
import com.example.library.book.dto.BookListResponseDto;
import com.example.library.book.dto.RankingItemDto;
import com.example.library.book.service.BookService;
import com.example.library.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // 도서 목록 조회 기능 (키워드 검색, 카테고리/제목 필터, 정렬 지원)
    @GetMapping
    public ApiResponse<BookListResponseDto> getBooks(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "30") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "direction", required = false) String direction
    ) {
        BookListResponseDto data = bookService.getBooks(page, size, keyword, filter, sort, direction);
        return ApiResponse.success(data, "도서 목록을 조회했습니다.");
    }

    // 대여 순위 조회 기능 (카테고리별 필터 지원, 상위 10위)
    @GetMapping("/rankings")
    public ApiResponse<List<RankingItemDto>> getRankings(
            @RequestParam(name = "category", required = false) String category
    ) {
        List<RankingItemDto> data = bookService.getRankings(category);
        return ApiResponse.success(data, "대여 순위를 조회했습니다.");
    }

    // 도서 상세 조회 기능 (ISBN으로 단건 조회, 재고·대여 가능 여부 포함)
    @GetMapping("/{isbn}")
    public ApiResponse<BookDetailDto> getBookDetail(@PathVariable String isbn) {
        BookDetailDto data = bookService.getBookDetail(isbn);
        return ApiResponse.success(data, "도서 상세 정보를 조회했습니다.");
    }
}
