package com.example.library.admin.dto;

public class AdminInventoryDto {
    private final Long inventoryId;
    private final boolean available;

    public AdminInventoryDto(Long inventoryId, boolean available) {
        this.inventoryId = inventoryId;
        this.available = available;
    }

    public Long getInventoryId() { return inventoryId; }
    public boolean isAvailable() { return available; }
}
