package com.example.hyunjiinserver.core.useractivity.application;

public record ToggleSavedRestaurantCommand(
        String deviceId,
        Long restaurantId
) {
}
