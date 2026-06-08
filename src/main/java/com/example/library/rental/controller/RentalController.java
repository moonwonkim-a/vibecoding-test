package com.example.library.rental.controller;

import com.example.library.common.response.ApiResponse;
import com.example.library.rental.dto.RentalCheckRequestDto;
import com.example.library.rental.dto.RentalCheckResponseDto;
import com.example.library.rental.dto.RentalRequestDto;
import com.example.library.rental.dto.RentalResponseDto;
import com.example.library.rental.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping("/check")
    public ApiResponse<RentalCheckResponseDto> checkRental(@Valid @RequestBody RentalCheckRequestDto request) {
        RentalCheckResponseDto data = rentalService.checkRental(request);
        return ApiResponse.success(data, "대여 가능 여부를 확인했습니다.");
    }

    @PostMapping
    public ApiResponse<RentalResponseDto> rent(@Valid @RequestBody RentalRequestDto request) {
        RentalResponseDto data = rentalService.rent(request);
        return ApiResponse.success(data, "대여가 완료되었습니다.");
    }
}
