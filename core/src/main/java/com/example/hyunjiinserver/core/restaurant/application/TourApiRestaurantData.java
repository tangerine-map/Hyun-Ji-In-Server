package com.example.hyunjiinserver.core.restaurant.application;

import java.time.OffsetDateTime;

public record TourApiRestaurantData(
        String contentId,
        String name,
        String category,
        String address,
        String phoneNumber,
        String openingHours,
        double latitude,
        double longitude,
        String summary,
        String representativeMenuName,
        OffsetDateTime modifiedAt
) {
}
