"use client";

import React, { useState, useEffect, useCallback } from "react";
import { AdminBook, AdminBookAddRequest, AdminInventory } from "@/lib/types";
import {
  getAdminBooks,
  addAdminBook,
  deleteAdminBook,
  getAdminInventories,
  deleteAdminInventory,
} from "@/lib/api";
import Modal from "@/components/shared/Modal";

export default function AdminBookManager() {
  const [books, setBooks] = useState<AdminBook[]>([]);
  const [loading, setLoading] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);
  const [expandedIsbn, setExpandedIsbn] = useState<string | null>(null);
  const [inventories, setInventories] = useState<AdminInventory[]>([]);
  const [inventoryLoading, setInventoryLoading] = useState(false);
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
      if (expandedIsbn === isbn) setExpandedIsbn(null);
      fetchBooks();
    } else {
      alert(res.message);
    }
  };

  const toggleInventory = async (isbn: string) => {
    if (expandedIsbn === isbn) {
      setExpandedIsbn(null);
      setInventories([]);
      return;
    }
    setExpandedIsbn(isbn);
    setInventoryLoading(true);
    const res = await getAdminInventories(isbn);
    if (res.success) setInventories(res.data);
    setInventoryLoading(false);
  };

  const handleDeleteInventory = async (inventoryId: number) => {
    if (!confirm(`재고 #${inventoryId}를 삭제하시겠습니까?`)) return;
    const res = await deleteAdminInventory(inventoryId);
    if (res.success) {
      if (expandedIsbn) {
        const updated = await getAdminInventories(expandedIsbn);
        if (updated.success) setInventories(updated.data);
      }
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

      {error && <p className="text-red-500 text-sm mb-3">{error}</p>}

      {loading ? (
        <div className="text-center py-8 text-gray-400">불러오는 중...</div>
      ) : (
        <div className="overflow-x-auto rounded-lg border border-gray-200">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-600">
              <tr>
                <th className="px-4 py-3 text-left w-6"></th>
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
                <React.Fragment key={book.isbn}>
                  <tr className="hover:bg-gray-50">
                    <td className="px-4 py-3 text-center">
                      <button
                        onClick={() => toggleInventory(book.isbn)}
                        className="text-gray-400 hover:text-blue-500 text-xs font-bold"
                        title="재고 상세 보기"
                      >
                        {expandedIsbn === book.isbn ? "▼" : "▶"}
                      </button>
                    </td>
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
                        전체삭제
                      </button>
                    </td>
                  </tr>

                  {expandedIsbn === book.isbn && (
                    <tr className="bg-blue-50">
                      <td colSpan={9} className="px-8 py-3">
                        {inventoryLoading ? (
                          <span className="text-xs text-gray-400">재고 불러오는 중...</span>
                        ) : inventories.length === 0 ? (
                          <span className="text-xs text-gray-400">재고 없음</span>
                        ) : (
                          <div className="flex flex-wrap gap-2">
                            <span className="text-xs text-gray-500 font-medium self-center mr-1">재고 단건 삭제:</span>
                            {inventories.map((inv) => (
                              <div key={inv.inventoryId} className="flex items-center gap-1.5 bg-white border border-gray-200 rounded-lg px-2.5 py-1.5 text-xs">
                                <span className="font-mono text-gray-600">#{inv.inventoryId}</span>
                                <span className={`px-1.5 py-0.5 rounded text-xs font-medium ${inv.available ? "bg-green-100 text-green-700" : "bg-red-100 text-red-500"}`}>
                                  {inv.available ? "가능" : "대여중"}
                                </span>
                                {inv.available && (
                                  <button
                                    onClick={() => handleDeleteInventory(inv.inventoryId)}
                                    className="text-red-400 hover:text-red-600 ml-1 font-bold"
                                    title="이 재고 삭제"
                                  >
                                    ✕
                                  </button>
                                )}
                              </div>
                            ))}
                          </div>
                        )}
                      </td>
                    </tr>
                  )}
                </React.Fragment>
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
