package com.example.hyunjiinserver.core.restaurant.application;

public record RestaurantSummaryResult(
        Long id,
        String name,
        String representativeMenuName,
        Integer representativeMenuPrice,
        double latitude,
        double longitude,
        Integer distanceMeters,
        String priceAdequacyLabel,
        boolean localRecommended,
        boolean saved
) {
}
