"use client";

import { useState, useEffect, useCallback } from "react";
import { ReturnHistoryItem, BlacklistReason } from "@/lib/types";
import { getReturnHistory, getBlacklistReasons, blacklistUser } from "@/lib/api";
import Modal from "@/components/shared/Modal";

export default function AdminReturnHistory() {
  const [items, setItems] = useState<ReturnHistoryItem[]>([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [target, setTarget] = useState<ReturnHistoryItem | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    const res = await getReturnHistory();
    setLoading(false);
    if (res.success) {
      setItems(res.data);
      setError("");
    } else {
      setError(res.message);
      setItems([]);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleBlacklistSuccess = async () => {
    setTarget(null);
    await load();
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-bold text-gray-900">전체 반납 이력</h2>
        <button
          onClick={load}
          disabled={loading}
          className="text-sm text-blue-600 hover:text-blue-700 disabled:opacity-50"
        >
          {loading ? "불러오는 중..." : "새로고침"}
        </button>
      </div>

      {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

      {items.length === 0 && !loading ? (
        <div className="text-center py-10 text-gray-400">반납 이력이 없습니다.</div>
      ) : (
        <div className="overflow-x-auto rounded-lg border border-gray-200">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-600">
              <tr>
                <th className="px-4 py-3 text-left">도서명</th>
                <th className="px-4 py-3 text-left">이용자</th>
                <th className="px-4 py-3 text-left">코드</th>
                <th className="px-4 py-3 text-left">대여일</th>
                <th className="px-4 py-3 text-left">반납 기한</th>
                <th className="px-4 py-3 text-left">반납일</th>
                <th className="px-4 py-3 text-center">연체</th>
                <th className="px-4 py-3 text-center">작업</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {items.map((item) => (
                <tr key={item.rentId} className={`hover:bg-gray-50 ${item.overdue ? "bg-red-50" : ""}`}>
                  <td className="px-4 py-3 font-medium text-gray-900 max-w-xs truncate">{item.title}</td>
                  <td className="px-4 py-3 text-gray-900">
                    {item.userName}
                    {item.blacklisted && (
                      <span className="ml-2 text-xs bg-red-100 text-red-600 px-2 py-0.5 rounded-full font-medium">불량</span>
                    )}
                  </td>
                  <td className="px-4 py-3 text-gray-500 font-mono">{item.userCode7Masked}</td>
                  <td className="px-4 py-3 text-gray-600">{item.rentDate}</td>
                  <td className="px-4 py-3 text-gray-600">{item.dueDate}</td>
                  <td className="px-4 py-3 text-gray-600">{item.returnDate || "-"}</td>
                  <td className="px-4 py-3 text-center">
                    {item.overdue ? (
                      <span className="text-red-500 text-xs">{item.overdueDays}일</span>
                    ) : (
                      <span className="text-gray-300 text-xs">-</span>
                    )}
                  </td>
                  <td className="px-4 py-3 text-center">
                    {item.canBlacklist ? (
                      <button
                        onClick={() => setTarget(item)}
                        className="text-xs bg-red-600 text-white px-3 py-1 rounded-lg font-medium hover:bg-red-700"
                      >
                        불량 지정
                      </button>
                    ) : (
                      <span className="text-gray-300 text-xs">-</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {target && (
        <BlacklistModal
          userName={target.userName}
          userCode7={target.userCode7}
          onClose={() => setTarget(null)}
          onSuccess={handleBlacklistSuccess}
        />
      )}
    </div>
  );
}

function BlacklistModal({
  userName, userCode7, onClose, onSuccess,
}: { userName: string; userCode7: string; onClose: () => void; onSuccess: () => void }) {
  const [reasons, setReasons] = useState<BlacklistReason[]>([]);
  const [reasonCode, setReasonCode] = useState("");
  const [memo, setMemo] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    (async () => {
      const res = await getBlacklistReasons();
      if (res.success) setReasons(res.data);
    })();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!reasonCode) { setError("사유를 선택해 주세요."); return; }
    setError("");
    setLoading(true);
    const res = await blacklistUser({ userName, userCode7, reasonCode, memo: memo || null });
    setLoading(false);
    if (res.success) {
      onSuccess();
    } else {
      setError(res.message);
    }
  };

  return (
    <Modal isOpen title="불량 이용자 지정" onClose={onClose} maxWidth="max-w-sm">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="bg-gray-50 rounded-lg p-3 text-sm">
          <span className="text-gray-500">대상: </span>
          <span className="font-medium">{userName}</span>
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">사유 선택 *</label>
          <select
            value={reasonCode}
            onChange={(e) => setReasonCode(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
          >
            <option value="">-- 사유 선택 --</option>
            {reasons.map((r) => (
              <option key={r.reasonCode} value={r.reasonCode}>{r.reasonLabel}</option>
            ))}
          </select>
        </div>
        {reasonCode === "ETC" && (
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">관리자 메모</label>
            <textarea
              value={memo}
              onChange={(e) => setMemo(e.target.value)}
              rows={3}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
              placeholder="메모를 입력하세요"
            />
          </div>
        )}
        {error && <p className="text-red-500 text-xs">{error}</p>}
        <button
          type="submit"
          disabled={loading}
          className="w-full bg-red-600 text-white py-2.5 rounded-lg font-medium hover:bg-red-700 disabled:opacity-50"
        >
          {loading ? "처리 중..." : "불량 이용자로 지정"}
        </button>
      </form>
    </Modal>
  );
}
