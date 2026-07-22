package com.example.hyunjiinserver.core.useractivity.application;

import java.util.List;

public record RecentRestaurantsResult(
        List<RecentRestaurantResult> restaurants
) {

    public RecentRestaurantsResult {
        restaurants = List.copyOf(restaurants);
    }
}
