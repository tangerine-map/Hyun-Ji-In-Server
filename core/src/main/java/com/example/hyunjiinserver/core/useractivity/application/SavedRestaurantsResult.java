package com.example.hyunjiinserver.core.useractivity.application;

import java.util.List;

public record SavedRestaurantsResult(
        List<SavedRestaurantResult> restaurants
) {

    public SavedRestaurantsResult {
        restaurants = List.copyOf(restaurants);
    }
}
