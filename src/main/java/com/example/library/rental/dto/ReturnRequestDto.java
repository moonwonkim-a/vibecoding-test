package com.example.library.rental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ReturnRequestDto {

    @NotNull(message = "대여 ID를 입력해 주세요.")
    private Long rentId;

    @NotBlank(message = "이름을 입력해 주세요.")
    private String userName;

    @NotBlank(message = "이용자 식별 코드를 입력해 주세요.")
    @Pattern(regexp = "\\d{7}", message = "이용자 식별 코드는 숫자 7자리여야 합니다.")
    private String userCode7;

    public Long getRentId() { return rentId; }
    public String getUserName() { return userName; }
    public String getUserCode7() { return userCode7; }
}
