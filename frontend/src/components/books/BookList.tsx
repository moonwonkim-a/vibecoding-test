"use client";

import { useState, useEffect, useCallback } from "react";
import { BookListItem, BookListResponse } from "@/lib/types";
import { getBooks } from "@/lib/api";
import BookDetailModal from "./BookDetailModal";

const SORT_OPTIONS = [
  { value: "title", label: "제목" },
  { value: "author", label: "저자" },
  { value: "category", label: "카테고리" },
  { value: "price", label: "가격" },
  { value: "available", label: "대여 가능" },
];

const FILTER_OPTIONS = [
  { value: "", label: "전체" },
  { value: "title", label: "제목" },
  { value: "author", label: "저자" },
  { value: "category", label: "카테고리" },
  { value: "publisher", label: "출판사" },
];

export default function BookList() {
  const [data, setData] = useState<BookListResponse | null>(null);
  const [page, setPage] = useState(0);
  const [keyword, setKeyword] = useState("");
  const [inputKeyword, setInputKeyword] = useState("");
  const [filter, setFilter] = useState("");
  const [sort, setSort] = useState("title");
  const [direction, setDirection] = useState("asc");
  const [selectedIsbn, setSelectedIsbn] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchBooks = useCallback(async () => {
    setLoading(true);
    const res = await getBooks({ page, keyword, filter, sort, direction });
    if (res.success) setData(res.data);
    setLoading(false);
  }, [page, keyword, filter, sort, direction]);

  useEffect(() => {
    fetchBooks();
  }, [fetchBooks]);

  const handleSearch = () => {
    setKeyword(inputKeyword);
    setPage(0);
  };

  const handleSortChange = (newSort: string) => {
    if (sort === newSort) {
      setDirection((d) => (d === "asc" ? "desc" : "asc"));
    } else {
      setSort(newSort);
      setDirection("asc");
    }
    setPage(0);
  };

  return (
    <div>
      {/* 검색 영역 */}
      <div className="flex flex-col sm:flex-row gap-2 mb-4">
        <select
          value={filter}
          onChange={(e) => { setFilter(e.target.value); setPage(0); }}
          className="border border-gray-300 rounded-lg px-3 py-2 text-sm"
        >
          {FILTER_OPTIONS.map((o) => (
            <option key={o.value} value={o.value}>{o.label}</option>
          ))}
        </select>
        <div className="flex flex-1 gap-2">
          <input
            type="text"
            value={inputKeyword}
            onChange={(e) => setInputKeyword(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSearch()}
            placeholder="검색어를 입력하세요"
            className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm"
          />
          <button
            onClick={handleSearch}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-blue-700"
          >
            검색
          </button>
          {keyword && (
            <button
              onClick={() => { setKeyword(""); setInputKeyword(""); setPage(0); }}
              className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg text-sm hover:bg-gray-300"
            >
              초기화
            </button>
          )}
        </div>
      </div>

      {/* 정렬 */}
      <div className="flex flex-wrap gap-2 mb-4">
        <span className="text-sm text-gray-500 self-center">정렬:</span>
        {SORT_OPTIONS.map((o) => (
          <button
            key={o.value}
            onClick={() => handleSortChange(o.value)}
            className={`text-xs px-3 py-1 rounded-full border ${
              sort === o.value
                ? "bg-blue-600 text-white border-blue-600"
                : "bg-white text-gray-600 border-gray-300 hover:border-blue-400"
            }`}
          >
            {o.label} {sort === o.value ? (direction === "asc" ? "↑" : "↓") : ""}
          </button>
        ))}
      </div>

      {/* 테이블 */}
      {loading ? (
        <div className="text-center py-10 text-gray-400">불러오는 중...</div>
      ) : !data || data.content.length === 0 ? (
        <div className="text-center py-10 text-gray-400">검색 결과가 없습니다.</div>
      ) : (
        <>
          <div className="overflow-x-auto rounded-lg border border-gray-200">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 text-gray-600">
                <tr>
                  <th className="px-4 py-3 text-left">카테고리</th>
                  <th className="px-4 py-3 text-left">제목</th>
                  <th className="px-4 py-3 text-left">저자</th>
                  <th className="px-4 py-3 text-left">출판사</th>
                  <th className="px-4 py-3 text-right">가격</th>
                  <th className="px-4 py-3 text-center">대여 가능</th>
                  <th className="px-4 py-3 text-center">상세</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {data.content.map((book) => (
                  <BookRow
                    key={book.isbn}
                    book={book}
                    onDetail={() => setSelectedIsbn(book.isbn)}
                  />
                ))}
              </tbody>
            </table>
          </div>

          {/* 페이징 */}
          <div className="flex justify-center gap-2 mt-4">
            <button
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              disabled={page === 0}
              className="px-4 py-2 border border-gray-300 rounded-lg text-sm disabled:opacity-40 hover:bg-gray-50"
            >
              이전
            </button>
            <span className="px-4 py-2 text-sm text-gray-600">
              {page + 1} / {data.totalPages}
            </span>
            <button
              onClick={() => setPage((p) => Math.min(data.totalPages - 1, p + 1))}
              disabled={page >= data.totalPages - 1}
              className="px-4 py-2 border border-gray-300 rounded-lg text-sm disabled:opacity-40 hover:bg-gray-50"
            >
              다음
            </button>
          </div>
        </>
      )}

      {selectedIsbn && (
        <BookDetailModal
          isbn={selectedIsbn}
          onClose={() => setSelectedIsbn(null)}
          onRefresh={fetchBooks}
        />
      )}
    </div>
  );
}

function BookRow({ book, onDetail }: { book: BookListItem; onDetail: () => void }) {
  return (
    <tr className={`hover:bg-gray-50 ${!book.rentAvailable ? "opacity-60" : ""}`}>
      <td className="px-4 py-3">
        <span className="bg-gray-100 text-gray-600 text-xs px-2 py-1 rounded-full">
          {book.category}
        </span>
      </td>
      <td className="px-4 py-3 font-medium text-gray-900 max-w-xs truncate">{book.title}</td>
      <td className="px-4 py-3 text-gray-600">{book.author}</td>
      <td className="px-4 py-3 text-gray-500 text-xs">{book.publisher}</td>
      <td className="px-4 py-3 text-right text-gray-700">{book.price.toLocaleString()}원</td>
      <td className="px-4 py-3 text-center">
        {book.rentAvailable ? (
          <span className="text-green-600 text-xs font-medium bg-green-50 px-2 py-1 rounded-full">
            가능 ({book.availableCount}/{book.totalCount})
          </span>
        ) : (
          <span className="text-red-500 text-xs font-medium bg-red-50 px-2 py-1 rounded-full">
            대여 불가
          </span>
        )}
      </td>
      <td className="px-4 py-3 text-center">
        <button
          onClick={onDetail}
          className="text-blue-600 hover:underline text-xs font-medium"
        >
          상세
        </button>
      </td>
    </tr>
  );
}
