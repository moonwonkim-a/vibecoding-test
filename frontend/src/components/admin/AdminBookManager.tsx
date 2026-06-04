"use client";

import { useState, useEffect, useCallback } from "react";
import { AdminBook, AdminBookAddRequest } from "@/lib/types";
import { getAdminBooks, addAdminBook, deleteAdminBook } from "@/lib/api";
import Modal from "@/components/shared/Modal";

export default function AdminBookManager() {
  const [books, setBooks] = useState<AdminBook[]>([]);
  const [loading, setLoading] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);
  const [error, setError] = useState("");

  const fetchBooks = useCallback(async () => {
    setLoading(true);
    const res = await getAdminBooks();
    if (res.success) setBooks(res.data);
    setLoading(false);
  }, []);

  useEffect(() => { fetchBooks(); }, [fetchBooks]);

  const handleDelete = async (isbn: string, title: string) => {
    if (!confirm(`"${title}" 도서를 삭제하시겠습니까? 관련 재고가 모두 삭제됩니다.`)) return;
    const res = await deleteAdminBook(isbn);
    if (res.success) {
      fetchBooks();
    } else {
      alert(res.message);
    }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-lg font-bold text-gray-900">도서 및 재고 현황</h2>
        <button
          onClick={() => setShowAddModal(true)}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700"
        >
          + 도서 추가
        </button>
      </div>

      {loading ? (
        <div className="text-center py-8 text-gray-400">불러오는 중...</div>
      ) : (
        <div className="overflow-x-auto rounded-lg border border-gray-200">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-600">
              <tr>
                <th className="px-4 py-3 text-left">ISBN</th>
                <th className="px-4 py-3 text-left">제목</th>
                <th className="px-4 py-3 text-left">저자</th>
                <th className="px-4 py-3 text-left">카테고리</th>
                <th className="px-4 py-3 text-right">총</th>
                <th className="px-4 py-3 text-right">가능</th>
                <th className="px-4 py-3 text-right">대여수</th>
                <th className="px-4 py-3 text-center">삭제</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {books.map((book) => (
                <tr key={book.isbn} className="hover:bg-gray-50">
                  <td className="px-4 py-3 font-mono text-xs text-gray-500">{book.isbn}</td>
                  <td className="px-4 py-3 font-medium text-gray-900 max-w-xs truncate">{book.title}</td>
                  <td className="px-4 py-3 text-gray-600">{book.author}</td>
                  <td className="px-4 py-3">
                    <span className="bg-gray-100 text-gray-600 text-xs px-2 py-0.5 rounded-full">{book.category}</span>
                  </td>
                  <td className="px-4 py-3 text-right">{book.totalCount}</td>
                  <td className="px-4 py-3 text-right">
                    <span className={book.availableCount > 0 ? "text-green-600" : "text-red-400"}>
                      {book.availableCount}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-right text-gray-500">{book.rentCount}</td>
                  <td className="px-4 py-3 text-center">
                    <button
                      onClick={() => handleDelete(book.isbn, book.title)}
                      className="text-red-500 hover:underline text-xs"
                    >
                      삭제
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showAddModal && (
        <AddBookModal
          onClose={() => setShowAddModal(false)}
          onSuccess={() => { setShowAddModal(false); fetchBooks(); }}
        />
      )}
    </div>
  );
}

function AddBookModal({ onClose, onSuccess }: { onClose: () => void; onSuccess: () => void }) {
  const [form, setForm] = useState<AdminBookAddRequest>({
    isbn: "", title: "", author: "", publisher: "", category: "", price: 0, quantity: 1,
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!/^\d{13}$/.test(form.isbn)) { setError("ISBN은 숫자 13자리여야 합니다."); return; }
    setError("");
    setLoading(true);
    const res = await addAdminBook(form);
    setLoading(false);
    if (res.success) {
      onSuccess();
    } else {
      setError(res.message);
    }
  };

  const update = (key: keyof AdminBookAddRequest, value: string | number) =>
    setForm((f) => ({ ...f, [key]: value }));

  return (
    <Modal isOpen title="도서 추가" onClose={onClose}>
      <form onSubmit={handleSubmit} className="space-y-3">
        {([
          ["isbn", "ISBN (13자리)"],
          ["title", "제목"],
          ["author", "저자"],
          ["publisher", "출판사"],
          ["category", "카테고리"],
        ] as [keyof AdminBookAddRequest, string][]).map(([key, label]) => (
          <div key={key}>
            <label className="block text-xs font-medium text-gray-600 mb-0.5">{label}</label>
            <input
              type="text"
              value={form[key] as string}
              onChange={(e) => update(key, e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        ))}
        <div className="flex gap-3">
          <div className="flex-1">
            <label className="block text-xs font-medium text-gray-600 mb-0.5">가격</label>
            <input
              type="number"
              value={form.price}
              onChange={(e) => update("price", Number(e.target.value))}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
              min={0}
            />
          </div>
          <div className="flex-1">
            <label className="block text-xs font-medium text-gray-600 mb-0.5">수량</label>
            <input
              type="number"
              value={form.quantity}
              onChange={(e) => update("quantity", Number(e.target.value))}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
              min={1}
            />
          </div>
        </div>
        {error && <p className="text-red-500 text-xs">{error}</p>}
        <button
          type="submit"
          disabled={loading}
          className="w-full bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50"
        >
          {loading ? "추가 중..." : "도서 추가"}
        </button>
      </form>
    </Modal>
  );
}
