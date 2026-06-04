package com.example.library.book.service;

import com.example.library.book.dto.BookDetailDto;
import com.example.library.book.dto.BookListResponseDto;
import com.example.library.book.dto.RankingItemDto;
import com.example.library.book.repository.BookQueryRepository;
import com.example.library.common.exception.BusinessException;
import com.example.library.common.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookQueryRepository bookQueryRepository;

    public BookService(BookQueryRepository bookQueryRepository) {
        this.bookQueryRepository = bookQueryRepository;
    }

    public BookListResponseDto getBooks(int page, int size, String keyword, String filter, String sort, String direction) {
        return bookQueryRepository.findBookList(page, size, keyword, filter, sort, direction);
    }

    public BookDetailDto getBookDetail(String isbn) {
        BookDetailDto detail = bookQueryRepository.findBookDetail(isbn);
        if (detail == null) {
            throw BusinessException.of(ErrorCode.EX_005);
        }
        return detail;
    }

    public List<RankingItemDto> getRankings(String category) {
        return bookQueryRepository.findRankings(category);
    }
}
