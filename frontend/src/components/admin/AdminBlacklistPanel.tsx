"use client";

import { useState, useEffect, useCallback } from "react";
import { BlacklistUser } from "@/lib/types";
import { getBlacklistUsers, releaseBlacklist } from "@/lib/api";

export default function AdminBlacklistPanel() {
  const [users, setUsers] = useState<BlacklistUser[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchUsers = useCallback(async () => {
    setLoading(true);
    const res = await getBlacklistUsers();
    if (res.success) setUsers(res.data);
    setLoading(false);
  }, []);

  useEffect(() => { fetchUsers(); }, [fetchUsers]);

  const handleRelease = async (user: BlacklistUser, userCode7Full: string) => {
    if (!confirm(`${user.userName} 이용자의 불량 상태를 해제하시겠습니까?`)) return;

    const code = prompt("이용자 식별 코드를 입력하세요 (7자리):", "");
    if (!code || !/^\d{7}$/.test(code)) {
      alert("올바른 식별 코드를 입력해 주세요.");
      return;
    }

    const res = await releaseBlacklist({ userName: user.userName, userCode7: code });
    if (res.success) {
      fetchUsers();
    } else {
      alert(res.message);
    }
  };

  return (
    <div>
      <h2 className="text-lg font-bold text-gray-900 mb-4">불량 이용자 목록</h2>
      {loading ? (
        <div className="text-center py-8 text-gray-400">불러오는 중...</div>
      ) : users.length === 0 ? (
        <div className="text-center py-8 text-gray-400">불량 이용자가 없습니다.</div>
      ) : (
        <div className="overflow-x-auto rounded-lg border border-gray-200">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-600">
              <tr>
                <th className="px-4 py-3 text-left">이름</th>
                <th className="px-4 py-3 text-left">식별 코드</th>
                <th className="px-4 py-3 text-left">사유</th>
                <th className="px-4 py-3 text-left">지정 일시</th>
                <th className="px-4 py-3 text-center">해제</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {users.map((u) => (
                <tr key={u.userCode7Masked} className="hover:bg-gray-50">
                  <td className="px-4 py-3 font-medium text-gray-900">{u.userName}</td>
                  <td className="px-4 py-3 font-mono text-xs text-gray-500">{u.userCode7Masked}</td>
                  <td className="px-4 py-3">
                    <span className="bg-red-100 text-red-600 text-xs px-2 py-0.5 rounded-full">
                      {u.reasonLabel}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-xs text-gray-500">
                    {u.blacklistedAt ? u.blacklistedAt.substring(0, 16).replace("T", " ") : "-"}
                  </td>
                  <td className="px-4 py-3 text-center">
                    <button
                      onClick={() => handleRelease(u, u.userCode7Masked)}
                      className="text-blue-600 hover:underline text-xs font-medium"
                    >
                      해제
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
