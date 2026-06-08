package com.example.library.admin.controller;

import com.example.library.admin.dto.AdminBookAddRequestDto;
import com.example.library.admin.dto.AdminBookAddResponseDto;
import com.example.library.admin.dto.AdminBookDto;
import com.example.library.admin.dto.AdminCurrentRentalDto;
import com.example.library.admin.dto.AdminLoginRequestDto;
import com.example.library.admin.dto.AdminLoginResponseDto;
import com.example.library.admin.dto.BlacklistReasonDto;
import com.example.library.admin.dto.BlacklistReleaseRequestDto;
import com.example.library.admin.dto.BlacklistReleaseResponseDto;
import com.example.library.admin.dto.BlacklistRequestDto;
import com.example.library.admin.dto.BlacklistResponseDto;
import com.example.library.admin.dto.BlacklistUserDto;
import com.example.library.admin.dto.UserHistoryResponseDto;
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

    @PostMapping("/login")
    public ApiResponse<AdminLoginResponseDto> login(
            @Valid @RequestBody AdminLoginRequestDto request,
            HttpSession session
    ) {
        AdminLoginResponseDto data = adminService.login(request);
        session.setAttribute("adminId", data.getAdminId());
        return ApiResponse.success(data, "로그인되었습니다.");
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.success(null, "로그아웃되었습니다.");
    }

    @GetMapping("/books")
    public ApiResponse<List<AdminBookDto>> getAllBooks() {
        return ApiResponse.success(adminService.getAllBooks(), "전체 도서 목록을 조회했습니다.");
    }

    @PostMapping("/books")
    public ApiResponse<AdminBookAddResponseDto> addBook(@Valid @RequestBody AdminBookAddRequestDto request) {
        return ApiResponse.success(adminService.addBook(request), "도서가 추가되었습니다.");
    }

    @DeleteMapping("/books/{isbn}")
    public ApiResponse<Object> deleteBook(@PathVariable String isbn) {
        adminService.deleteBook(isbn);
        return ApiResponse.success(java.util.Map.of("isbn", isbn), "도서가 삭제되었습니다.");
    }

    @DeleteMapping("/inventories/{inventoryId}")
    public ApiResponse<Object> deleteInventory(@PathVariable Long inventoryId) {
        adminService.deleteInventory(inventoryId);
        return ApiResponse.success(java.util.Map.of("inventoryId", inventoryId), "재고가 삭제되었습니다.");
    }

    @GetMapping("/rentals/current")
    public ApiResponse<List<AdminCurrentRentalDto>> getCurrentRentals(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction
    ) {
        return ApiResponse.success(adminService.getCurrentRentals(sort, direction), "대여 중인 도서 목록을 조회했습니다.");
    }

    @GetMapping("/users/history")
    public ApiResponse<UserHistoryResponseDto> getUserHistory(
            @RequestParam String userName,
            @RequestParam String userCode7
    ) {
        return ApiResponse.success(adminService.getUserHistory(userName, userCode7), "이용자 반납 이력을 조회했습니다.");
    }

    @GetMapping("/blacklist-reasons")
    public ApiResponse<List<BlacklistReasonDto>> getBlacklistReasons() {
        return ApiResponse.success(adminService.getBlacklistReasons(), "불량 지정 사유를 조회했습니다.");
    }

    @PostMapping("/users/blacklist")
    public ApiResponse<BlacklistResponseDto> blacklist(@Valid @RequestBody BlacklistRequestDto request) {
        return ApiResponse.success(adminService.blacklist(request), "불량 이용자로 지정되었습니다.");
    }

    @GetMapping("/users/blacklist")
    public ApiResponse<List<BlacklistUserDto>> getBlacklistUsers() {
        return ApiResponse.success(adminService.getBlacklistUsers(), "불량 이용자 목록을 조회했습니다.");
    }

    @PatchMapping("/users/blacklist/release")
    public ApiResponse<BlacklistReleaseResponseDto> releaseBlacklist(
            @Valid @RequestBody BlacklistReleaseRequestDto request
    ) {
        return ApiResponse.success(adminService.releaseBlacklist(request), "불량 이용자 상태가 해제되었습니다.");
    }
}
