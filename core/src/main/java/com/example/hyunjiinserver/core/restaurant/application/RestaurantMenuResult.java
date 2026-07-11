package com.example.hyunjiinserver.core.restaurant.application;

public record RestaurantMenuResult(
        Long id,
        String name,
        Integer price,
        boolean representative
) {
}
