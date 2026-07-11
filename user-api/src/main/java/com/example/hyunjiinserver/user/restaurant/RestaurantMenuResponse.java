package com.example.hyunjiinserver.user.restaurant;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantMenuResult;

public record RestaurantMenuResponse(
        Long id,
        String name,
        Integer price,
        boolean representative
) {

    public static RestaurantMenuResponse from(RestaurantMenuResult result) {
        return new RestaurantMenuResponse(
                result.id(),
                result.name(),
                result.price(),
                result.representative()
        );
    }
}
