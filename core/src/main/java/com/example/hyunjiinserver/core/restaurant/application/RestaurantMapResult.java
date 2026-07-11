package com.example.hyunjiinserver.core.restaurant.application;

import java.util.List;

public record RestaurantMapResult(
        List<RestaurantSummaryResult> restaurants
) {

    public RestaurantMapResult {
        restaurants = List.copyOf(restaurants);
    }
}
