package com.example.hyunjiinserver.core.recommendation.application;

import java.util.List;

public record RecommendationsResult(
        String sessionId,
        List<RecommendedRestaurantResult> restaurants
) {

    public RecommendationsResult {
        restaurants = List.copyOf(restaurants);
    }
}
