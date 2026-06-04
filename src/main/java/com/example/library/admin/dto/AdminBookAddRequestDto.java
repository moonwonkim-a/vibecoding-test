package com.example.library.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AdminBookAddRequestDto {

    @NotBlank(message = "ISBN을 입력해 주세요.")
    @Pattern(regexp = "\\d{13}", message = "올바른 ISBN 형식이 아닙니다.")
    private String isbn;

    @NotBlank(message = "제목을 입력해 주세요.")
    private String title;

    @NotBlank(message = "저자를 입력해 주세요.")
    private String author;

    @NotBlank(message = "출판사를 입력해 주세요.")
    private String publisher;

    @NotBlank(message = "카테고리를 입력해 주세요.")
    private String category;

    @NotNull(message = "가격을 입력해 주세요.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "수량을 입력해 주세요.")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private Integer quantity;

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getCategory() { return category; }
    public Integer getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
}
