package com.example.hyunjiinserver.core.useractivity.application;

import java.time.OffsetDateTime;

public record SavedRestaurantResult(
        Long restaurantId,
        String name,
        String representativeMenuName,
        Integer representativeMenuPrice,
        double latitude,
        double longitude,
        Integer distanceMeters,
        String priceAdequacyLabel,
        boolean localRecommended,
        OffsetDateTime savedAt
) {
}
