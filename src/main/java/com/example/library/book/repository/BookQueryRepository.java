package com.example.library.book.repository;

import com.example.library.book.dto.BookDetailDto;
import com.example.library.book.dto.BookListResponseDto;
import com.example.library.book.dto.RankingItemDto;

import java.util.List;

public interface BookQueryRepository {
    BookListResponseDto findBookList(int page, int size, String keyword, String filter, String sort, String direction);

    BookDetailDto findBookDetail(String isbn);

    List<RankingItemDto> findRankings(String category);
}
