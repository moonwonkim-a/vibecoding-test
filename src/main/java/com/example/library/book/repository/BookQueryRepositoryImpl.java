package com.example.library.book.repository;

import com.example.library.book.dto.BookDetailDto;
import com.example.library.book.dto.BookListItemDto;
import com.example.library.book.dto.BookListResponseDto;
import com.example.library.book.dto.RankingItemDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
public class BookQueryRepositoryImpl implements BookQueryRepository {

    private static final String ACTIVE_BOOK_FILTER = "(b.del_yn IS NULL OR b.del_yn = 'N')";
    private static final String ACTIVE_INVENTORY_JOIN = "(i.del_yn IS NULL OR i.del_yn = 'N')";
    private static final String INVENTORY_JOIN =
            " LEFT JOIN library_book_inventory i ON b.isbn = i.isbn AND " + ACTIVE_INVENTORY_JOIN;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BookListResponseDto findBookList(int page, int size, String keyword, String filter, String sort, String direction) {
        String whereClause = buildWhereClause(keyword, filter);
        String orderByClause = buildOrderByClause(sort, direction);

        String baseSql = " FROM library_book_info b"
                + INVENTORY_JOIN
                + whereClause
                + " GROUP BY b.isbn, b.title, b.author, b.publisher, b.category, b.price, b.rent_count";

        String listSql = "SELECT b.isbn, b.title, b.author, b.publisher, b.category, b.price,"
                + " COUNT(i.inventory_id) AS total_count,"
                + " SUM(CASE WHEN i.available = true THEN 1 ELSE 0 END) AS available_count,"
                + " b.rent_count"
                + baseSql
                + orderByClause;

        String countSql = "SELECT COUNT(*) FROM (SELECT b.isbn" + baseSql + ") t";

        int safePage = Math.max(0, page);
        Query listQuery = entityManager.createNativeQuery(listSql);
        Query countQuery = entityManager.createNativeQuery(countSql);
        applyKeywordParam(listQuery, keyword, filter);
        applyKeywordParam(countQuery, keyword, filter);
        listQuery.setFirstResult(safePage * size);
        listQuery.setMaxResults(size);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = listQuery.getResultList();
        List<BookListItemDto> content = rows.stream()
                .map(this::toBookListItem)
                .toList();

        Number total = (Number) countQuery.getSingleResult();
        long totalElements = total.longValue();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new BookListResponseDto(content, page, size, totalElements, totalPages);
    }

    @Override
    public BookDetailDto findBookDetail(String isbn) {
        String detailSql = "SELECT b.isbn, b.title, b.author, b.publisher, b.category, b.price,"
                + " COUNT(i.inventory_id) AS total_count,"
                + " SUM(CASE WHEN i.available = true THEN 1 ELSE 0 END) AS available_count"
                + " FROM library_book_info b"
                + INVENTORY_JOIN
                + " WHERE b.isbn = ? AND " + ACTIVE_BOOK_FILTER
                + " GROUP BY b.isbn, b.title, b.author, b.publisher, b.category, b.price";

        Query query = entityManager.createNativeQuery(detailSql);
        query.setParameter(1, isbn);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        return rows.stream()
                .findFirst()
                .map(this::toBookDetail)
                .orElse(null);
    }

    private String buildWhereClause(String keyword, String filter) {
        StringBuilder clause = new StringBuilder(" WHERE ").append(ACTIVE_BOOK_FILTER);

        if (keyword == null || keyword.isBlank()) {
            return clause.toString();
        }

        String normalizedFilter = Optional.ofNullable(filter).orElse("").trim().toLowerCase(Locale.ROOT);
        String keywordCondition = switch (normalizedFilter) {
            case "title" -> "LOWER(b.title) LIKE LOWER(?)";
            case "author" -> "LOWER(b.author) LIKE LOWER(?)";
            case "category" -> "LOWER(b.category) LIKE LOWER(?)";
            case "publisher" -> "LOWER(b.publisher) LIKE LOWER(?)";
            default -> buildAllFieldsKeywordCondition();
        };

        return clause.append(" AND ").append(keywordCondition).toString();
    }

    private String buildAllFieldsKeywordCondition() {
        return "(LOWER(b.title) LIKE LOWER(?)"
                + " OR LOWER(b.author) LIKE LOWER(?)"
                + " OR LOWER(b.category) LIKE LOWER(?)"
                + " OR LOWER(b.publisher) LIKE LOWER(?))";
    }

    private String buildOrderByClause(String sort, String direction) {
        String normalizedSort = Optional.ofNullable(sort).orElse("").toLowerCase(Locale.ROOT);
        String normalizedDirection = "desc".equalsIgnoreCase(direction) ? "DESC" : "ASC";

        String sortColumn = switch (normalizedSort) {
            case "title" -> "b.title";
            case "author" -> "b.author";
            case "category" -> "b.category";
            case "price" -> "b.price";
            case "available" -> "available_count";
            default -> "b.title";
        };

        return " ORDER BY " + sortColumn + " " + normalizedDirection + ", b.isbn ASC";
    }

    private void applyKeywordParam(Query query, String keyword, String filter) {
        if (keyword == null || keyword.isBlank()) {
            return;
        }
        String pattern = "%" + keyword.trim() + "%";
        String normalizedFilter = Optional.ofNullable(filter).orElse("").trim().toLowerCase(Locale.ROOT);

        if ("title".equals(normalizedFilter)
                || "author".equals(normalizedFilter)
                || "category".equals(normalizedFilter)
                || "publisher".equals(normalizedFilter)) {
            query.setParameter(1, pattern);
            return;
        }

        query.setParameter(1, pattern);
        query.setParameter(2, pattern);
        query.setParameter(3, pattern);
        query.setParameter(4, pattern);
    }

    private long toLongOrZero(Object value) {
        return value == null ? 0L : ((Number) value).longValue();
    }

    private BookListItemDto toBookListItem(Object[] row) {
        long totalCount = toLongOrZero(row[6]);
        long availableCount = toLongOrZero(row[7]);
        return new BookListItemDto(
                (String) row[0],
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (String) row[4],
                ((Number) row[5]).intValue(),
                totalCount,
                availableCount,
                ((Number) row[8]).intValue(),
                availableCount > 0
        );
    }

    @Override
    public List<RankingItemDto> findRankings(String category) {
        boolean hasCategory = category != null && !category.isBlank();
        String whereClause = " WHERE " + ACTIVE_BOOK_FILTER + (hasCategory ? " AND b.category = ?" : "");

        String sql = "SELECT b.isbn, b.title, b.author, b.category, b.rent_count"
                + " FROM library_book_info b"
                + whereClause
                + " ORDER BY b.rent_count DESC LIMIT 10";

        Query query = entityManager.createNativeQuery(sql);
        if (hasCategory) {
            query.setParameter(1, category);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();
        List<RankingItemDto> result = new java.util.ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Object[] row = rows.get(i);
            result.add(new RankingItemDto(
                    i + 1,
                    (String) row[0],
                    (String) row[1],
                    (String) row[2],
                    (String) row[3],
                    ((Number) row[4]).intValue()
            ));
        }
        return result;
    }

    private BookDetailDto toBookDetail(Object[] row) {
        long totalCount = toLongOrZero(row[6]);
        long availableCount = toLongOrZero(row[7]);
        return new BookDetailDto(
                (String) row[0],
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (String) row[4],
                ((Number) row[5]).intValue(),
                totalCount,
                availableCount,
                availableCount > 0
        );
    }
}
