package com.example.hyunjiinserver.core.recommendation.application;

public record RecommendedRestaurantResult(
        Long restaurantId,
        String name,
        String representativeMenuName,
        Integer representativeMenuPrice,
        double latitude,
        double longitude,
        Integer distanceMeters,
        String reason,
        String priceAdequacyLabel,
        boolean localRecommended,
        boolean saved
) {
}
