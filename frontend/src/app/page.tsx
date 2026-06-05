"use client";

import { useState } from "react";
import BookList from "@/components/books/BookList";
import RankingSection from "@/components/books/RankingSection";
import ReturnModal from "@/components/rental/ReturnModal";

export default function UserPage() {
  const [showReturn, setShowReturn] = useState(false);
  const [activeTab, setActiveTab] = useState<"books" | "ranking">("books");
  const [refreshSignal, setRefreshSignal] = useState(0);

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 헤더 */}
      <header className="bg-white border-b border-gray-200 sticky top-0 z-40 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center gap-3">
              <span className="text-2xl">📚</span>
              <h1 className="text-xl font-bold text-gray-900">도서 대여 시스템</h1>
            </div>
            <div className="flex items-center gap-3">
              <button
                onClick={() => setShowReturn(true)}
                className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors"
              >
                도서 반납
              </button>
              <a
                href="/admin"
                className="text-gray-500 hover:text-gray-700 text-sm"
              >
                관리자
              </a>
            </div>
          </div>
        </div>
      </header>

      {/* 탭 */}
      <div className="bg-white border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex gap-6">
            {[
              { key: "books", label: "도서 목록" },
              { key: "ranking", label: "대여 순위" },
            ].map((tab) => (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key as "books" | "ranking")}
                className={`py-4 text-sm font-medium border-b-2 transition-colors ${
                  activeTab === tab.key
                    ? "border-blue-600 text-blue-600"
                    : "border-transparent text-gray-500 hover:text-gray-700"
                }`}
              >
                {tab.label}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* 본문 */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        {activeTab === "books" && <BookList refreshSignal={refreshSignal} />}
        {activeTab === "ranking" && <RankingSection />}
      </main>

      {/* 반납 모달 */}
      {showReturn && (
        <ReturnModal
          onClose={() => setShowReturn(false)}
          onSuccess={() => setRefreshSignal((s) => s + 1)}
        />
      )}
    </div>
  );
}
