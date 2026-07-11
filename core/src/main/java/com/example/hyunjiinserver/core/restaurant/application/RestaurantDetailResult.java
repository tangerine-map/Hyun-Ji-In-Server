package com.example.hyunjiinserver.core.restaurant.application;

import java.util.List;

public record RestaurantDetailResult(
        Long id,
        String name,
        String category,
        String address,
        String phoneNumber,
        String openingHours,
        double latitude,
        double longitude,
        String summary,
        boolean localRecommended,
        String localRecommendationReason,
        String priceAdequacyLabel,
        String priceAdequacyDescription,
        boolean saved,
        List<RestaurantMenuResult> representativeMenus
) {

    public RestaurantDetailResult {
        representativeMenus = List.copyOf(representativeMenus);
    }
}
