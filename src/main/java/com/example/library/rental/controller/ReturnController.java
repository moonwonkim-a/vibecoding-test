package com.example.library.rental.controller;

import com.example.library.common.response.ApiResponse;
import com.example.library.rental.dto.ReturnCandidateDto;
import com.example.library.rental.dto.ReturnRequestDto;
import com.example.library.rental.dto.ReturnResponseDto;
import com.example.library.rental.service.ReturnService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    // 반납 후보 목록 조회 기능 (이름·코드로 현재 대여 중인 도서 목록 조회)
    @GetMapping("/candidates")
    public ApiResponse<List<ReturnCandidateDto>> getCandidates(
            @RequestParam(name = "userName") String userName,
            @RequestParam(name = "userCode7") String userCode7
    ) {
        List<ReturnCandidateDto> data = returnService.getCandidates(userName, userCode7);
        return ApiResponse.success(data, "반납 가능한 도서를 조회했습니다.");
    }

    // 도서 반납 기능 (재고 복구, 연체 여부 판정 및 연체일 기록)
    @PostMapping
    public ApiResponse<ReturnResponseDto> returnBook(@Valid @RequestBody ReturnRequestDto request) {
        ReturnResponseDto data = returnService.returnBook(request);
        return ApiResponse.success(data, "반납이 완료되었습니다. 감사합니다.");
    }
}
