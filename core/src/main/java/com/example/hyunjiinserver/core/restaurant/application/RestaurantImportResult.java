package com.example.hyunjiinserver.core.restaurant.application;

public record RestaurantImportResult(
        int fetchedCount,
        int createdCount,
        int updatedCount,
        int failedCount
) {
}
