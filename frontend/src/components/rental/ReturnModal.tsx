"use client";

import { useState } from "react";
import { ReturnCandidate } from "@/lib/types";
import { getReturnCandidates, returnBook } from "@/lib/api";
import Modal from "@/components/shared/Modal";

interface Props {
  onClose: () => void;
}

type Step = "input" | "list" | "done";

function maskCode(code: string) {
  return code.length >= 4 ? code.substring(0, 4) + "***" : code;
}

export default function ReturnModal({ onClose }: Props) {
  const [step, setStep] = useState<Step>("input");
  const [userName, setUserName] = useState("");
  const [userCode7, setUserCode7] = useState("");
  const [isCodeFocused, setIsCodeFocused] = useState(false);
  const [maskedCode, setMaskedCode] = useState("");
  const [candidates, setCandidates] = useState<ReturnCandidate[]>([]);
  const [selectedRentId, setSelectedRentId] = useState<number | null>(null);
  const [doneTitle, setDoneTitle] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    if (!userName.trim()) { setError("이름을 입력해 주세요."); return; }
    if (!/^\d{7}$/.test(userCode7)) { setError("이용자 식별 코드는 숫자 7자리여야 합니다."); return; }
    setError("");
    setLoading(true);
    const res = await getReturnCandidates(userName.trim(), userCode7);
    setLoading(false);
    if (res.success) {
      setCandidates(res.data);
      setStep("list");
    } else {
      setError(res.message);
    }
  };

  const handleReturn = async (candidate: ReturnCandidate) => {
    if (!confirm(`"${candidate.title}" 도서를 반납하시겠습니까?`)) return;
    setLoading(true);
    const res = await returnBook({ rentId: candidate.rentId, userName: userName.trim(), userCode7 });
    setLoading(false);
    if (res.success) {
      setDoneTitle(candidate.title);
      setStep("done");
    } else {
      setError(res.message);
    }
  };

  return (
    <Modal isOpen title="도서 반납" onClose={onClose} maxWidth="max-w-md">
      {step === "input" && (
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">이름</label>
            <input
              type="text"
              value={userName}
              onChange={(e) => setUserName(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="이름을 입력하세요"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              이용자 식별 코드 (테스트용 7자리)
            </label>
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
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="숫자 7자리"
            />
          </div>
          {error && <p className="text-red-500 text-xs">{error}</p>}
          <button
            onClick={handleSearch}
            disabled={loading}
            className="w-full bg-green-600 text-white py-2.5 rounded-lg font-medium hover:bg-green-700 disabled:opacity-50"
          >
            {loading ? "조회 중..." : "반납 도서 조회"}
          </button>
        </div>
      )}

      {step === "list" && (
        <div className="space-y-3">
          {candidates.length === 0 ? (
            <div className="text-center py-6 text-gray-400">대여 중인 도서가 없습니다.</div>
          ) : (
            <>
              <p className="text-sm text-gray-500">반납할 도서를 선택하세요</p>
              {candidates.map((c) => (
                <div
                  key={c.rentId}
                  className={`border rounded-lg p-3 space-y-1 ${c.overdue ? "border-red-300 bg-red-50" : "border-gray-200"}`}
                >
                  <div className="flex justify-between items-start">
                    <div>
                      <p className="font-medium text-sm text-gray-900">{c.title}</p>
                      <p className="text-xs text-gray-500">
                        대여일: {c.rentDate} / 반납 기한: {c.dueDate}
                      </p>
                      {c.overdue && (
                        <p className="text-xs text-red-500 font-medium mt-1">
                          ⚠ 반납 기한이 초과되었습니다. ({c.overdueDays}일 초과)
                        </p>
                      )}
                    </div>
                    <button
                      onClick={() => handleReturn(c)}
                      disabled={loading}
                      className="ml-3 bg-green-600 text-white text-xs px-3 py-1.5 rounded-lg hover:bg-green-700 disabled:opacity-50 whitespace-nowrap"
                    >
                      반납
                    </button>
                  </div>
                </div>
              ))}
            </>
          )}
          {error && <p className="text-red-500 text-xs">{error}</p>}
          <button
            onClick={() => { setStep("input"); setError(""); }}
            className="w-full border border-gray-300 text-gray-700 py-2 rounded-lg text-sm hover:bg-gray-50"
          >
            뒤로
          </button>
        </div>
      )}

      {step === "done" && (
        <div className="text-center space-y-4">
          <div className="text-5xl">✓</div>
          <p className="text-lg font-bold text-gray-900">반납이 완료되었습니다!</p>
          <p className="text-sm text-gray-600">{doneTitle}</p>
          <button
            onClick={onClose}
            className="w-full bg-green-600 text-white py-2.5 rounded-lg font-medium hover:bg-green-700"
          >
            확인
          </button>
        </div>
      )}
    </Modal>
  );
}
