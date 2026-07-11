package com.example.hyunjiinserver.core.restaurant.application;

public record GetRestaurantDetailQuery(
        Long restaurantId,
        Long userId
) {
}
