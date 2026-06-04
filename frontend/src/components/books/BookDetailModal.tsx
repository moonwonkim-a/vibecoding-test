"use client";

import { useState, useEffect } from "react";
import { BookDetail } from "@/lib/types";
import { getBookDetail } from "@/lib/api";
import Modal from "@/components/shared/Modal";
import RentalModal from "@/components/rental/RentalModal";

interface Props {
  isbn: string;
  onClose: () => void;
  onRefresh: () => void;
}

export default function BookDetailModal({ isbn, onClose, onRefresh }: Props) {
  const [book, setBook] = useState<BookDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [showRental, setShowRental] = useState(false);

  useEffect(() => {
    (async () => {
      const res = await getBookDetail(isbn);
      if (res.success) setBook(res.data);
      setLoading(false);
    })();
  }, [isbn]);

  const handleRentalSuccess = () => {
    setShowRental(false);
    onClose();
    onRefresh();
  };

  if (loading) {
    return (
      <Modal isOpen title="도서 상세" onClose={onClose}>
        <div className="text-center py-8 text-gray-400">불러오는 중...</div>
      </Modal>
    );
  }

  if (!book) {
    return (
      <Modal isOpen title="도서 상세" onClose={onClose}>
        <div className="text-center py-8 text-red-400">도서 정보를 불러올 수 없습니다.</div>
      </Modal>
    );
  }

  return (
    <>
      <Modal isOpen title="도서 상세" onClose={onClose}>
        <div className="space-y-3">
          <div className="flex justify-between">
            <span className="text-gray-500 text-sm">카테고리</span>
            <span className="font-medium text-sm bg-gray-100 px-2 py-1 rounded">{book.category}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500 text-sm">제목</span>
            <span className="font-bold text-gray-900 text-sm text-right max-w-[60%]">{book.title}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500 text-sm">저자</span>
            <span className="text-sm">{book.author}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500 text-sm">출판사</span>
            <span className="text-sm">{book.publisher}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500 text-sm">가격</span>
            <span className="text-sm font-medium">{book.price.toLocaleString()}원</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500 text-sm">총 보유</span>
            <span className="text-sm">{book.totalCount}권</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500 text-sm">대여 가능</span>
            <span className={`text-sm font-medium ${book.rentAvailable ? "text-green-600" : "text-red-500"}`}>
              {book.availableCount}권
            </span>
          </div>

          <div className="pt-3 border-t border-gray-200">
            {book.rentAvailable ? (
              <button
                onClick={() => setShowRental(true)}
                className="w-full bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 transition-colors"
              >
                대여하기
              </button>
            ) : (
              <div className="text-center">
                <button
                  disabled
                  className="w-full bg-gray-200 text-gray-400 py-2.5 rounded-lg font-medium cursor-not-allowed mb-2"
                >
                  대여 불가
                </button>
                <p className="text-xs text-red-400">현재 모든 재고가 대여 중입니다.</p>
              </div>
            )}
          </div>
        </div>
      </Modal>

      {showRental && (
        <RentalModal
          isbn={isbn}
          title={book.title}
          onClose={() => setShowRental(false)}
          onSuccess={handleRentalSuccess}
        />
      )}
    </>
  );
}
