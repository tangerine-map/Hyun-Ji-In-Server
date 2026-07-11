package com.example.hyunjiinserver.user.restaurant;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantMapResult;
import java.util.List;

public record RestaurantMapResponse(
        List<RestaurantSummaryResponse> restaurants
) {

    public static RestaurantMapResponse from(RestaurantMapResult result) {
        return new RestaurantMapResponse(
                result.restaurants()
                        .stream()
                        .map(RestaurantSummaryResponse::from)
                        .toList()
        );
    }
}
