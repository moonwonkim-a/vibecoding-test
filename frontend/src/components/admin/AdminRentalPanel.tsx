"use client";

import { useState, useEffect, useCallback } from "react";
import { AdminCurrentRental } from "@/lib/types";
import { getCurrentRentals } from "@/lib/api";

export default function AdminRentalPanel() {
  const [rentals, setRentals] = useState<AdminCurrentRental[]>([]);
  const [sort, setSort] = useState("rentDate");
  const [direction, setDirection] = useState("desc");
  const [loading, setLoading] = useState(false);

  const fetchRentals = useCallback(async () => {
    setLoading(true);
    const res = await getCurrentRentals({ sort, direction });
    if (res.success) setRentals(res.data);
    setLoading(false);
  }, [sort, direction]);

  useEffect(() => { fetchRentals(); }, [fetchRentals]);

  const toggleSort = (field: string) => {
    if (sort === field) setDirection((d) => (d === "asc" ? "desc" : "asc"));
    else { setSort(field); setDirection("asc"); }
  };

  const SortHeader = ({ field, label }: { field: string; label: string }) => (
    <th
      className="px-4 py-3 text-left cursor-pointer hover:bg-gray-100 select-none"
      onClick={() => toggleSort(field)}
    >
      {label} {sort === field ? (direction === "asc" ? "↑" : "↓") : ""}
    </th>
  );

  return (
    <div>
      <h2 className="text-lg font-bold text-gray-900 mb-4">대여 중인 도서 목록</h2>
      {loading ? (
        <div className="text-center py-8 text-gray-400">불러오는 중...</div>
      ) : (
        <div className="overflow-x-auto rounded-lg border border-gray-200">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-600">
              <tr>
                <th className="px-4 py-3 text-left">도서명</th>
                <th className="px-4 py-3 text-left">이용자</th>
                <SortHeader field="rentDate" label="대여일" />
                <SortHeader field="dueDate" label="반납 기한" />
                <th className="px-4 py-3 text-center">연체</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {rentals.length === 0 ? (
                <tr>
                  <td colSpan={5} className="text-center py-8 text-gray-400">대여 중인 도서가 없습니다.</td>
                </tr>
              ) : rentals.map((r) => (
                <tr key={r.rentId} className={`hover:bg-gray-50 ${r.overdue ? "bg-red-50" : ""}`}>
                  <td className="px-4 py-3 font-medium text-gray-900 max-w-xs truncate">{r.title}</td>
                  <td className="px-4 py-3">
                    <div className="text-xs text-gray-600">{r.userName}</div>
                    <div className="font-mono text-xs text-gray-400">{r.userCode7Masked}</div>
                  </td>
                  <td className="px-4 py-3 text-gray-600">{r.rentDate}</td>
                  <td className="px-4 py-3 text-gray-600">{r.dueDate}</td>
                  <td className="px-4 py-3 text-center">
                    {r.overdue ? (
                      <span className="text-red-500 text-xs font-medium bg-red-100 px-2 py-0.5 rounded-full">
                        {r.overdueDays}일 초과
                      </span>
                    ) : (
                      <span className="text-green-600 text-xs bg-green-50 px-2 py-0.5 rounded-full">정상</span>
                    )}
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
