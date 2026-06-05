"use client";

import { useState, useEffect } from "react";
import AdminLogin from "@/components/admin/AdminLogin";
import AdminBookManager from "@/components/admin/AdminBookManager";
import AdminRentalPanel from "@/components/admin/AdminRentalPanel";
import AdminUserHistory from "@/components/admin/AdminUserHistory";
import AdminBlacklistPanel from "@/components/admin/AdminBlacklistPanel";
import { adminLogout } from "@/lib/api";

type Tab = "books" | "rentals" | "history" | "blacklist";

interface AdminInfo {
  adminId: string;
  adminName: string;
}

export default function AdminPage() {
  const [admin, setAdmin] = useState<AdminInfo | null>(null);
  const [activeTab, setActiveTab] = useState<Tab>("books");
  const [checked, setChecked] = useState(false);

  useEffect(() => {
    const saved = sessionStorage.getItem("adminInfo");
    if (saved) {
      try { setAdmin(JSON.parse(saved)); } catch {}
    }
    setChecked(true);
  }, []);

  const handleLogin = (adminId: string, adminName: string) => {
    const info = { adminId, adminName };
    setAdmin(info);
    sessionStorage.setItem("adminInfo", JSON.stringify(info));
  };

  const handleLogout = async () => {
    await adminLogout();
    setAdmin(null);
    sessionStorage.removeItem("adminInfo");
  };

  if (!checked) return null;
  if (!admin) return <AdminLogin onLogin={handleLogin} />;

  const TABS = [
    { key: "books", label: "도서 관리" },
    { key: "rentals", label: "대여 현황" },
    { key: "history", label: "이용자 이력" },
    { key: "blacklist", label: "불량 이용자" },
  ] as { key: Tab; label: string }[];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 헤더 */}
      <header className="bg-white border-b border-gray-200 sticky top-0 z-40 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center gap-3">
              <span className="text-2xl">🔐</span>
              <div>
                <h1 className="text-lg font-bold text-gray-900">관리자 대시보드</h1>
                <p className="text-xs text-gray-500">{admin.adminName} 님</p>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <a href="/" className="text-gray-500 hover:text-gray-700 text-sm">이용자 페이지</a>
              <button
                onClick={handleLogout}
                className="text-red-500 hover:text-red-700 text-sm font-medium border border-red-300 px-3 py-1.5 rounded-lg hover:bg-red-50"
              >
                로그아웃
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* 탭 */}
      <div className="bg-white border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex gap-6">
            {TABS.map((tab) => (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key)}
                className={`py-4 text-sm font-medium border-b-2 transition-colors whitespace-nowrap ${
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
        {activeTab === "books" && <AdminBookManager />}
        {activeTab === "rentals" && <AdminRentalPanel />}
        {activeTab === "history" && <AdminUserHistory />}
        {activeTab === "blacklist" && <AdminBlacklistPanel />}
      </main>
    </div>
  );
}
