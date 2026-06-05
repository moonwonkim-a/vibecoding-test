package com.example.library.admin.controller;

import com.example.library.admin.dto.AdminBookAddRequestDto;
import com.example.library.admin.dto.AdminBookAddResponseDto;
import com.example.library.admin.dto.AdminBookDto;
import com.example.library.admin.dto.AdminCurrentRentalDto;
import com.example.library.admin.dto.AdminInventoryDto;
import com.example.library.admin.dto.AdminLoginRequestDto;
import com.example.library.admin.dto.AdminLoginResponseDto;
import com.example.library.admin.dto.BlacklistReasonDto;
import com.example.library.admin.dto.BlacklistReleaseRequestDto;
import com.example.library.admin.dto.BlacklistReleaseResponseDto;
import com.example.library.admin.dto.BlacklistRequestDto;
import com.example.library.admin.dto.BlacklistResponseDto;
import com.example.library.admin.dto.BlacklistUserDto;
import com.example.library.admin.dto.ReturnHistoryItemDto;
import com.example.library.admin.service.AdminService;
import com.example.library.common.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 관리자 로그인 기능 (세션 생성)
    @PostMapping("/login")
    public ApiResponse<AdminLoginResponseDto> login(
            @Valid @RequestBody AdminLoginRequestDto request,
            HttpSession session
    ) {
        AdminLoginResponseDto data = adminService.login(request);
        session.setAttribute("adminId", data.getAdminId());
        return ApiResponse.success(data, "로그인되었습니다.");
    }

    // 관리자 로그아웃 기능 (세션 무효화)
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.success(null, "로그아웃되었습니다.");
    }

    // 전체 도서 목록 조회 기능 (재고 수량·대여 현황 포함)
    @GetMapping("/books")
    public ApiResponse<List<AdminBookDto>> getAllBooks() {
        return ApiResponse.success(adminService.getAllBooks(), "전체 도서 목록을 조회했습니다.");
    }

    // 도서 추가 기능 (ISBN 중복 검사, 재고 수량만큼 인벤토리 생성)
    @PostMapping("/books")
    public ApiResponse<AdminBookAddResponseDto> addBook(@Valid @RequestBody AdminBookAddRequestDto request) {
        return ApiResponse.success(adminService.addBook(request), "도서가 추가되었습니다.");
    }

    // 도서 삭제 기능 (대여 중인 재고 있을 시 삭제 불가)
    @DeleteMapping("/books/{isbn}")
    public ApiResponse<Object> deleteBook(@PathVariable String isbn) {
        adminService.deleteBook(isbn);
        return ApiResponse.success(java.util.Map.of("isbn", isbn), "도서가 삭제되었습니다.");
    }

    // 도서별 재고 목록 조회 기능 (단건 삭제 UI용 — inventoryId 목록 제공)
    @GetMapping("/books/{isbn}/inventories")
    public ApiResponse<List<AdminInventoryDto>> getInventoriesByIsbn(@PathVariable String isbn) {
        return ApiResponse.success(adminService.getInventoriesByIsbn(isbn), "재고 목록을 조회했습니다.");
    }

    // 개별 재고 삭제 기능 (특정 재고 항목 단건 삭제)
    @DeleteMapping("/inventories/{inventoryId}")
    public ApiResponse<Object> deleteInventory(@PathVariable Long inventoryId) {
        adminService.deleteInventory(inventoryId);
        return ApiResponse.success(java.util.Map.of("inventoryId", inventoryId), "재고가 삭제되었습니다.");
    }

    // 현재 대여 현황 조회 기능 (정렬 기준·방향 선택 가능, 연체 여부 포함)
    @GetMapping("/rentals/current")
    public ApiResponse<List<AdminCurrentRentalDto>> getCurrentRentals(
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "direction", required = false) String direction
    ) {
        return ApiResponse.success(adminService.getCurrentRentals(sort, direction), "대여 중인 도서 목록을 조회했습니다.");
    }

    // 전체 반납 이력 조회 기능 (모든 도서의 반납 완료 기록, 각 행에서 불량 지정 가능)
    @GetMapping("/return-history")
    public ApiResponse<List<ReturnHistoryItemDto>> getReturnHistory() {
        return ApiResponse.success(adminService.getReturnHistory(), "전체 반납 이력을 조회했습니다.");
    }

    // 불량 지정 사유 목록 조회 기능
    @GetMapping("/blacklist-reasons")
    public ApiResponse<List<BlacklistReasonDto>> getBlacklistReasons() {
        return ApiResponse.success(adminService.getBlacklistReasons(), "불량 지정 사유를 조회했습니다.");
    }

    // 불량 이용자 지정 기능 (이름·코드 검증 후 사유·메모 저장)
    @PostMapping("/users/blacklist")
    public ApiResponse<BlacklistResponseDto> blacklist(@Valid @RequestBody BlacklistRequestDto request) {
        return ApiResponse.success(adminService.blacklist(request), "불량 이용자로 지정되었습니다.");
    }

    // 불량 이용자 목록 조회 기능
    @GetMapping("/users/blacklist")
    public ApiResponse<List<BlacklistUserDto>> getBlacklistUsers() {
        return ApiResponse.success(adminService.getBlacklistUsers(), "불량 이용자 목록을 조회했습니다.");
    }

    // 불량 이용자 해제 기능 (마스킹된 코드·이름으로 식별 후 상태 해제)
    @PatchMapping("/users/blacklist/release")
    public ApiResponse<BlacklistReleaseResponseDto> releaseBlacklist(
            @Valid @RequestBody BlacklistReleaseRequestDto request
    ) {
        return ApiResponse.success(adminService.releaseBlacklist(request), "불량 이용자 상태가 해제되었습니다.");
    }
}
