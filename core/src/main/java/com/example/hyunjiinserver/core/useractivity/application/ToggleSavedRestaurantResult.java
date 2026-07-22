package com.example.hyunjiinserver.core.useractivity.application;

public record ToggleSavedRestaurantResult(
        Long restaurantId,
        boolean saved
) {
}
