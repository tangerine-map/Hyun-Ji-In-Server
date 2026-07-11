package com.example.hyunjiinserver.core.restaurant.domain;

public record RestaurantMapSearchCondition(
        double southWestLatitude,
        double southWestLongitude,
        double northEastLatitude,
        double northEastLongitude,
        String keyword,
        String category,
        Boolean localRecommended,
        int limit
) {
}
