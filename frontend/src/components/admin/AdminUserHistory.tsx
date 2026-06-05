"use client";

import { useState, useEffect } from "react";
import { UserHistoryResponse, BlacklistReason } from "@/lib/types";
import { getUserHistory, getBlacklistReasons, blacklistUser } from "@/lib/api";
import Modal from "@/components/shared/Modal";

function maskCode(code: string) {
  return code.length >= 4 ? code.substring(0, 4) + "***" : code;
}

export default function AdminUserHistory() {
  const [userName, setUserName] = useState("");
  const [userCode7, setUserCode7] = useState("");
  const [isCodeFocused, setIsCodeFocused] = useState(false);
  const [maskedCode, setMaskedCode] = useState("");
  const [history, setHistory] = useState<UserHistoryResponse | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showBlacklist, setShowBlacklist] = useState(false);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!userName.trim() || !/^\d{7}$/.test(userCode7)) {
      setError("이름과 7자리 식별 코드를 입력해 주세요.");
      return;
    }
    setError("");
    setLoading(true);
    const res = await getUserHistory(userName.trim(), userCode7);
    setLoading(false);
    if (res.success) {
      setHistory(res.data);
    } else {
      setError(res.message);
      setHistory(null);
    }
  };

  const handleBlacklistSuccess = async () => {
    setShowBlacklist(false);
    if (userName && userCode7) {
      const res = await getUserHistory(userName, userCode7);
      if (res.success) setHistory(res.data);
    }
  };

  return (
    <div>
      <h2 className="text-lg font-bold text-gray-900 mb-4">이용자 대여/반납 이력 조회</h2>
      <form onSubmit={handleSearch} className="flex flex-col sm:flex-row gap-2 mb-6">
        <input
          type="text"
          value={userName}
          onChange={(e) => setUserName(e.target.value)}
          placeholder="이름"
          className="border border-gray-300 rounded-lg px-3 py-2 text-sm flex-1"
        />
        <input
          type="text"
          value={isCodeFocused ? userCode7 : (userCode7 ? maskedCode : "")}
          onChange={(e) => {
            const v = e.target.value.replace(/\D/g, "").slice(0, 7);
            setUserCode7(v);
            setMaskedCode(maskCode(v));
          }}
          onFocus={() => setIsCodeFocused(true)}
          onBlur={() => { setIsCodeFocused(false); if (userCode7) setMaskedCode(maskCode(userCode7)); }}
          placeholder="식별 코드 (7자리)"
          className="border border-gray-300 rounded-lg px-3 py-2 text-sm w-40"
        />
        <button
          type="submit"
          disabled={loading}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 disabled:opacity-50"
        >
          {loading ? "조회 중..." : "조회"}
        </button>
      </form>
      {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

      {history && (
        <div>
          {/* 이용자 요약 */}
          <div className="bg-gray-50 rounded-lg p-4 mb-4 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
            <div className="flex items-center gap-4">
              <div>
                <p className="font-bold text-gray-900">{history.userName}</p>
                <p className="text-xs text-gray-500 font-mono">{history.userCode7Masked}</p>
              </div>
              {history.blacklisted && (
                <span className="text-xs bg-red-100 text-red-600 px-2 py-1 rounded-full font-medium">불량 이용자</span>
              )}
            </div>
            <div className="flex gap-4 text-sm text-gray-600">
              <span>총 대여: <strong>{history.totalRentCount}</strong>회</span>
              <span>연체: <strong className={history.overdueCount > 0 ? "text-red-500" : ""}>{history.overdueCount}</strong>회</span>
            </div>
            {history.canBlacklist && (
              <button
                onClick={() => setShowBlacklist(true)}
                className="bg-red-600 text-white px-4 py-1.5 rounded-lg text-sm font-medium hover:bg-red-700"
              >
                불량 지정
              </button>
            )}
          </div>

          {/* 이력 테이블 */}
          {history.histories.length === 0 ? (
            <div className="text-center py-6 text-gray-400">대여 이력이 없습니다.</div>
          ) : (
            <div className="overflow-x-auto rounded-lg border border-gray-200">
              <table className="w-full text-sm">
                <thead className="bg-gray-50 text-gray-600">
                  <tr>
                    <th className="px-4 py-3 text-left">도서명</th>
                    <th className="px-4 py-3 text-left">대여일</th>
                    <th className="px-4 py-3 text-left">반납 기한</th>
                    <th className="px-4 py-3 text-left">반납일</th>
                    <th className="px-4 py-3 text-center">상태</th>
                    <th className="px-4 py-3 text-center">연체</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {history.histories.map((h) => (
                    <tr key={h.rentId} className={`hover:bg-gray-50 ${h.overdue ? "bg-red-50" : ""}`}>
                      <td className="px-4 py-3 font-medium text-gray-900 max-w-xs truncate">{h.title}</td>
                      <td className="px-4 py-3 text-gray-600">{h.rentDate}</td>
                      <td className="px-4 py-3 text-gray-600">{h.dueDate}</td>
                      <td className="px-4 py-3 text-gray-600">{h.returnDate || "-"}</td>
                      <td className="px-4 py-3 text-center">
                        <span className={`text-xs px-2 py-0.5 rounded-full ${
                          h.status === "RENTING"
                            ? "bg-blue-100 text-blue-600"
                            : "bg-gray-100 text-gray-600"
                        }`}>
                          {h.status === "RENTING" ? "대여 중" : "반납 완료"}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-center">
                        {h.overdue ? (
                          <span className="text-red-500 text-xs">{h.overdueDays}일</span>
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
        </div>
      )}

      {showBlacklist && history && (
        <BlacklistModal
          userName={history.userName}
          userCode7={userCode7}
          onClose={() => setShowBlacklist(false)}
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
