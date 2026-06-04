package com.example.library.admin.dto;

public class AdminBookAddResponseDto {
    private final String isbn;
    private final int addedInventoryCount;

    public AdminBookAddResponseDto(String isbn, int addedInventoryCount) {
        this.isbn = isbn;
        this.addedInventoryCount = addedInventoryCount;
    }

    public String getIsbn() { return isbn; }
    public int getAddedInventoryCount() { return addedInventoryCount; }
}
