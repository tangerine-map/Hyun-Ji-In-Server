package com.example.hyunjiinserver.core.restaurant.application;

public record RestaurantEnrichmentApplyResult(
        int requestedCount,
        int appliedCount,
        int skippedCount
) {
}
