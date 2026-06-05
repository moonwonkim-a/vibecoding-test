// 공통 응답 구조
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
  errorCode?: string;
  requirementId?: string;
}

// 도서 목록
export interface BookListItem {
  isbn: string;
  title: string;
  author: string;
  publisher: string;
  category: string;
  price: number;
  totalCount: number;
  availableCount: number;
  rentCount: number;
  rentAvailable: boolean;
}

export interface BookListResponse {
  content: BookListItem[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

// 도서 상세
export interface BookDetail {
  isbn: string;
  title: string;
  author: string;
  publisher: string;
  category: string;
  price: number;
  totalCount: number;
  availableCount: number;
  rentAvailable: boolean;
}

// 대여 순위
export interface RankingItem {
  rank: number;
  isbn: string;
  title: string;
  author: string;
  category: string;
  rentCount: number;
}

// 대여 확인
export interface RentalCheckRequest {
  userName: string;
  userCode7: string;
}

export interface CurrentRentalItem {
  rentId: number;
  isbn: string;
  title: string;
  rentDate: string;
  dueDate: string;
}

export interface RentalCheckResponse {
  userName: string;
  userCode7Masked: string;
  blacklisted: boolean;
  currentRentCount: number;
  canRent: boolean;
  currentRentals: CurrentRentalItem[];
}

// 대여
export interface RentalRequest {
  isbn: string;
  userName: string;
  userCode7: string;
}

export interface RentalResponse {
  rentId: number;
  inventoryId: number;
  isbn: string;
  title: string;
  rentDate: string;
  dueDate: string;
}

// 반납 후보
export interface ReturnCandidate {
  rentId: number;
  inventoryId: number;
  isbn: string;
  title: string;
  rentDate: string;
  dueDate: string;
  overdue: boolean;
  overdueDays: number;
}

// 반납
export interface ReturnRequest {
  rentId: number;
  userName: string;
  userCode7: string;
}

export interface ReturnResponse {
  rentId: number;
  isbn: string;
  title: string;
  returnDate: string;
  overdue: boolean;
  overdueDays: number;
}

// 관리자 로그인
export interface AdminLoginRequest {
  adminId: string;
  adminPw: string;
}

export interface AdminLoginResponse {
  adminId: string;
  adminName: string;
}

// 관리자 도서
export interface AdminBook {
  isbn: string;
  title: string;
  author: string;
  category: string;
  publisher: string;
  price: number;
  totalCount: number;
  availableCount: number;
  rentCount: number;
}

export interface AdminBookAddRequest {
  isbn: string;
  title: string;
  author: string;
  publisher: string;
  category: string;
  price: number;
  quantity: number;
}

export interface AdminBookAddResponse {
  isbn: string;
  addedInventoryCount: number;
}

export interface AdminInventory {
  inventoryId: number;
  available: boolean;
}

// 관리자 현재 대여
export interface AdminCurrentRental {
  rentId: number;
  inventoryId: number;
  isbn: string;
  title: string;
  userName: string;
  userCode7Masked: string;
  rentDate: string;
  dueDate: string;
  overdue: boolean;
  overdueDays: number;
}

// 전체 반납 이력
export interface ReturnHistoryItem {
  rentId: number;
  isbn: string;
  title: string;
  userName: string;
  userCode7: string;
  userCode7Masked: string;
  rentDate: string;
  dueDate: string;
  returnDate: string | null;
  overdue: boolean;
  overdueDays: number;
  blacklisted: boolean;
  canBlacklist: boolean;
}

// 불량 이유
export interface BlacklistReason {
  reasonCode: string;
  reasonLabel: string;
  description: string;
}

// 불량 지정
export interface BlacklistRequest {
  userName: string;
  userCode7: string;
  reasonCode: string;
  memo: string | null;
}

export interface BlacklistResponse {
  userName: string;
  userCode7Masked: string;
  blacklisted: boolean;
  reasonCode: string;
  blacklistedAt: string;
}

// 불량 목록
export interface BlacklistUser {
  userName: string;
  userCode7Masked: string;
  reasonCode: string;
  reasonLabel: string;
  blacklistedAt: string;
}

// 불량 해제
export interface BlacklistReleaseRequest {
  userName: string;
  userCode7Masked: string;
}

export interface BlacklistReleaseResponse {
  userName: string;
  userCode7Masked: string;
  blacklisted: boolean;
}
