package com.example.library.admin.dto;

import java.util.List;

public class AdminBookAddResponseDto {
    private final String isbn;
    private final int addedInventoryCount;
    private final List<Long> inventoryIds;

    public AdminBookAddResponseDto(String isbn, int addedInventoryCount, List<Long> inventoryIds) {
        this.isbn = isbn;
        this.addedInventoryCount = addedInventoryCount;
        this.inventoryIds = inventoryIds;
    }

    public String getIsbn() { return isbn; }
    public int getAddedInventoryCount() { return addedInventoryCount; }
    public List<Long> getInventoryIds() { return inventoryIds; }
}
