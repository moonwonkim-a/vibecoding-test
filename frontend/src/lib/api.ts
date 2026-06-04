import {
  AdminBook,
  AdminBookAddRequest,
  AdminBookAddResponse,
  AdminCurrentRental,
  AdminLoginRequest,
  AdminLoginResponse,
  ApiResponse,
  BlacklistReason,
  BlacklistReleaseRequest,
  BlacklistReleaseResponse,
  BlacklistRequest,
  BlacklistResponse,
  BlacklistUser,
  BookDetail,
  BookListResponse,
  RankingItem,
  RentalCheckRequest,
  RentalCheckResponse,
  RentalRequest,
  RentalResponse,
  ReturnCandidate,
  ReturnRequest,
  ReturnResponse,
  UserHistoryResponse,
} from "./types";

const BASE_URL = "http://localhost:8080/api";

async function request<T>(
  path: string,
  options?: RequestInit
): Promise<ApiResponse<T>> {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    ...options,
  });
  return res.json();
}

// ─── 도서 ───────────────────────────────────────
export function getBooks(params: {
  page?: number;
  size?: number;
  keyword?: string;
  filter?: string;
  sort?: string;
  direction?: string;
}): Promise<ApiResponse<BookListResponse>> {
  const query = new URLSearchParams();
  if (params.page !== undefined) query.set("page", String(params.page));
  if (params.size !== undefined) query.set("size", String(params.size));
  if (params.keyword) query.set("keyword", params.keyword);
  if (params.filter) query.set("filter", params.filter);
  if (params.sort) query.set("sort", params.sort);
  if (params.direction) query.set("direction", params.direction);
  return request<BookListResponse>(`/books?${query}`);
}

export function getBookDetail(isbn: string): Promise<ApiResponse<BookDetail>> {
  return request<BookDetail>(`/books/${isbn}`);
}

export function getRankings(category?: string): Promise<ApiResponse<RankingItem[]>> {
  const query = category ? `?category=${encodeURIComponent(category)}` : "";
  return request<RankingItem[]>(`/books/rankings${query}`);
}

// ─── 대여 ───────────────────────────────────────
export function checkRental(body: RentalCheckRequest): Promise<ApiResponse<RentalCheckResponse>> {
  return request<RentalCheckResponse>("/rentals/check", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export function rent(body: RentalRequest): Promise<ApiResponse<RentalResponse>> {
  return request<RentalResponse>("/rentals", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

// ─── 반납 ───────────────────────────────────────
export function getReturnCandidates(
  userName: string,
  userCode7: string
): Promise<ApiResponse<ReturnCandidate[]>> {
  const query = new URLSearchParams({ userName, userCode7 });
  return request<ReturnCandidate[]>(`/returns/candidates?${query}`);
}

export function returnBook(body: ReturnRequest): Promise<ApiResponse<ReturnResponse>> {
  return request<ReturnResponse>("/returns", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

// ─── 관리자 ──────────────────────────────────────
export function adminLogin(body: AdminLoginRequest): Promise<ApiResponse<AdminLoginResponse>> {
  return request<AdminLoginResponse>("/admin/login", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export function adminLogout(): Promise<ApiResponse<null>> {
  return request<null>("/admin/logout", { method: "POST" });
}

export function getAdminBooks(): Promise<ApiResponse<AdminBook[]>> {
  return request<AdminBook[]>("/admin/books");
}

export function addAdminBook(body: AdminBookAddRequest): Promise<ApiResponse<AdminBookAddResponse>> {
  return request<AdminBookAddResponse>("/admin/books", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export function deleteAdminBook(isbn: string): Promise<ApiResponse<{ isbn: string }>> {
  return request<{ isbn: string }>(`/admin/books/${isbn}`, { method: "DELETE" });
}

export function deleteAdminInventory(
  inventoryId: number
): Promise<ApiResponse<{ inventoryId: number }>> {
  return request<{ inventoryId: number }>(`/admin/inventories/${inventoryId}`, {
    method: "DELETE",
  });
}

export function getCurrentRentals(params?: {
  sort?: string;
  direction?: string;
}): Promise<ApiResponse<AdminCurrentRental[]>> {
  const query = new URLSearchParams();
  if (params?.sort) query.set("sort", params.sort);
  if (params?.direction) query.set("direction", params.direction);
  return request<AdminCurrentRental[]>(`/admin/rentals/current?${query}`);
}

export function getUserHistory(
  userName: string,
  userCode7: string
): Promise<ApiResponse<UserHistoryResponse>> {
  const query = new URLSearchParams({ userName, userCode7 });
  return request<UserHistoryResponse>(`/admin/users/history?${query}`);
}

export function getBlacklistReasons(): Promise<ApiResponse<BlacklistReason[]>> {
  return request<BlacklistReason[]>("/admin/blacklist-reasons");
}

export function blacklistUser(body: BlacklistRequest): Promise<ApiResponse<BlacklistResponse>> {
  return request<BlacklistResponse>("/admin/users/blacklist", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export function getBlacklistUsers(): Promise<ApiResponse<BlacklistUser[]>> {
  return request<BlacklistUser[]>("/admin/users/blacklist");
}

export function releaseBlacklist(
  body: BlacklistReleaseRequest
): Promise<ApiResponse<BlacklistReleaseResponse>> {
  return request<BlacklistReleaseResponse>("/admin/users/blacklist/release", {
    method: "PATCH",
    body: JSON.stringify(body),
  });
}
