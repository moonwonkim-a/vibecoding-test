"use client";

import { useState } from "react";
import { RentalCheckResponse } from "@/lib/types";
import { checkRental, rent } from "@/lib/api";
import Modal from "@/components/shared/Modal";

interface Props {
  isbn: string;
  title: string;
  onClose: () => void;
  onSuccess: () => void;
}

type Step = "input" | "confirm" | "done";

function maskCode(code: string) {
  return code.length >= 4 ? code.substring(0, 4) + "***" : code;
}

export default function RentalModal({ isbn, title, onClose, onSuccess }: Props) {
  const [step, setStep] = useState<Step>("input");
  const [userName, setUserName] = useState("");
  const [userCode7, setUserCode7] = useState("");
  const [maskedCode, setMaskedCode] = useState("");
  const [isCodeFocused, setIsCodeFocused] = useState(false);
  const [checkResult, setCheckResult] = useState<RentalCheckResponse | null>(null);
  const [doneMessage, setDoneMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleCheck = async () => {
    if (!userName.trim()) { setError("이름을 입력해 주세요."); return; }
    if (!/^\d{7}$/.test(userCode7)) { setError("이용자 식별 코드는 숫자 7자리여야 합니다."); return; }
    setError("");
    setLoading(true);
    const res = await checkRental({ userName: userName.trim(), userCode7 });
    setLoading(false);
    if (res.success) {
      setCheckResult(res.data);
      setStep("confirm");
    } else {
      setError(res.message);
    }
  };

  const handleRent = async () => {
    if (!checkResult?.canRent) return;
    setLoading(true);
    const res = await rent({ isbn, userName: userName.trim(), userCode7 });
    setLoading(false);
    if (res.success) {
      setDoneMessage(`반납 기한: ${res.data.dueDate}`);
      setStep("done");
    } else {
      setError(res.message);
    }
  };

  return (
    <Modal isOpen title={`도서 대여 — ${title}`} onClose={onClose} maxWidth="max-w-md">
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
              type={isCodeFocused ? "text" : "text"}
              value={isCodeFocused ? userCode7 : (userCode7 ? maskedCode : "")}
              onChange={(e) => {
                const v = e.target.value.replace(/\D/g, "").slice(0, 7);
                setUserCode7(v);
                setMaskedCode(maskCode(v));
              }}
              onFocus={() => setIsCodeFocused(true)}
              onBlur={() => {
                setIsCodeFocused(false);
                if (userCode7) setMaskedCode(maskCode(userCode7));
              }}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="숫자 7자리"
              maxLength={7}
            />
          </div>
          {error && <p className="text-red-500 text-xs">{error}</p>}
          <button
            onClick={handleCheck}
            disabled={loading}
            className="w-full bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50"
          >
            {loading ? "확인 중..." : "대여 정보 확인"}
          </button>
        </div>
      )}

      {step === "confirm" && checkResult && (
        <div className="space-y-4">
          <div className="bg-gray-50 rounded-lg p-3 space-y-1 text-sm">
            <div className="flex justify-between">
              <span className="text-gray-500">이름</span>
              <span className="font-medium">{checkResult.userName}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">식별 코드</span>
              <span className="font-mono">{checkResult.userCode7Masked}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">현재 대여</span>
              <span>{checkResult.currentRentCount}권</span>
            </div>
          </div>

          {checkResult.currentRentals.length > 0 && (
            <div>
              <p className="text-xs font-medium text-gray-600 mb-1">현재 대여 중인 도서</p>
              {checkResult.currentRentals.map((r) => (
                <div key={r.rentId} className="text-xs bg-yellow-50 border border-yellow-200 rounded p-2 mb-1">
                  <span className="font-medium">{r.title}</span>
                  <span className="text-gray-500 ml-2">반납 기한: {r.dueDate}</span>
                </div>
              ))}
            </div>
          )}

          {checkResult.blacklisted && (
            <p className="text-red-500 text-sm font-medium bg-red-50 border border-red-200 rounded p-2">
              불량 이용자로 대여가 불가합니다.
            </p>
          )}
          {!checkResult.blacklisted && checkResult.currentRentCount >= 2 && (
            <p className="text-orange-500 text-sm bg-orange-50 border border-orange-200 rounded p-2">
              현재 대여 한도(2권)를 초과하였습니다.
            </p>
          )}

          {error && <p className="text-red-500 text-xs">{error}</p>}

          <div className="flex gap-2">
            <button
              onClick={() => { setStep("input"); setError(""); }}
              className="flex-1 border border-gray-300 text-gray-700 py-2.5 rounded-lg font-medium hover:bg-gray-50"
            >
              뒤로
            </button>
            <button
              onClick={handleRent}
              disabled={!checkResult.canRent || loading}
              className="flex-1 bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? "대여 중..." : "대여 확인"}
            </button>
          </div>
        </div>
      )}

      {step === "done" && (
        <div className="text-center space-y-4">
          <div className="text-5xl">✓</div>
          <p className="text-lg font-bold text-gray-900">대여가 완료되었습니다!</p>
          <p className="text-sm text-gray-600">{doneMessage}</p>
          <button
            onClick={onSuccess}
            className="w-full bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700"
          >
            확인
          </button>
        </div>
      )}
    </Modal>
  );
}
