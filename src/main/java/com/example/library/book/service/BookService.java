package com.example.library.book.service;

import com.example.library.book.dto.BookDetailDto;
import com.example.library.book.dto.BookListResponseDto;
import com.example.library.book.repository.BookQueryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "도서를 찾을 수 없습니다.");
        }
        return detail;
    }
}
