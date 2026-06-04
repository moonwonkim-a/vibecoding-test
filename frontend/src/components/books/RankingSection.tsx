"use client";

import { useState, useEffect } from "react";
import { RankingItem } from "@/lib/types";
import { getRankings } from "@/lib/api";

const CATEGORIES = ["", "프로그래밍", "데이터", "소설", "역사", "자기계발", "디자인", "과학", "경제"];

export default function RankingSection() {
  const [rankings, setRankings] = useState<RankingItem[]>([]);
  const [category, setCategory] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    (async () => {
      setLoading(true);
      const res = await getRankings(category || undefined);
      if (res.success) setRankings(res.data);
      setLoading(false);
    })();
  }, [category]);

  return (
    <section>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-bold text-gray-900">대여 순위 TOP 10</h2>
        <select
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm"
        >
          {CATEGORIES.map((c) => (
            <option key={c} value={c}>{c || "전체 카테고리"}</option>
          ))}
        </select>
      </div>

      {loading ? (
        <div className="text-center py-6 text-gray-400">불러오는 중...</div>
      ) : rankings.length === 0 ? (
        <div className="text-center py-6 text-gray-400">순위 데이터가 없습니다.</div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
          {rankings.map((item) => (
            <div
              key={item.isbn}
              className="flex items-center gap-3 p-3 border border-gray-200 rounded-lg bg-white hover:shadow-sm transition-shadow"
            >
              <span
                className={`w-7 h-7 rounded-full flex items-center justify-center text-sm font-bold flex-shrink-0 ${
                  item.rank <= 3 ? "bg-yellow-400 text-white" : "bg-gray-100 text-gray-600"
                }`}
              >
                {item.rank}
              </span>
              <div className="min-w-0">
                <p className="font-medium text-sm text-gray-900 truncate">{item.title}</p>
                <p className="text-xs text-gray-500">
                  {item.author} · {item.category} · {item.rentCount}회
                </p>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
