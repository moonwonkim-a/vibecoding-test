"use client";

import { useState } from "react";
import { adminLogin } from "@/lib/api";

interface Props {
  onLogin: (adminId: string, adminName: string) => void;
}

export default function AdminLogin({ onLogin }: Props) {
  const [adminId, setAdminId] = useState("");
  const [adminPw, setAdminPw] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!adminId.trim() || !adminPw.trim()) { setError("아이디와 비밀번호를 입력해 주세요."); return; }
    setError("");
    setLoading(true);
    const res = await adminLogin({ adminId: adminId.trim(), adminPw });
    setLoading(false);
    if (res.success) {
      onLogin(res.data.adminId, res.data.adminName);
    } else {
      setError(res.message);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      <div className="bg-white rounded-xl shadow-lg w-full max-w-sm p-8">
        <div className="text-center mb-6">
          <span className="text-4xl">🔐</span>
          <h1 className="text-2xl font-bold text-gray-900 mt-2">관리자 로그인</h1>
          <p className="text-gray-500 text-sm mt-1">도서 대여 관리 시스템</p>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">아이디</label>
            <input
              type="text"
              value={adminId}
              onChange={(e) => setAdminId(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="관리자 아이디"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">비밀번호</label>
            <input
              type="password"
              value={adminPw}
              onChange={(e) => setAdminPw(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="비밀번호"
            />
          </div>
          {error && <p className="text-red-500 text-xs">{error}</p>}
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50"
          >
            {loading ? "로그인 중..." : "로그인"}
          </button>
        </form>
        <p className="text-center text-xs text-gray-400 mt-4">
          초기 계정: admin / admin1234
        </p>
      </div>
    </div>
  );
}
