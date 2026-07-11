package com.example.hyunjiinserver.user.restaurant;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantSummaryResult;

public record RestaurantSummaryResponse(
        Long id,
        String name,
        String representativeMenuName,
        Integer representativeMenuPrice,
        double latitude,
        double longitude,
        Integer distanceMeters,
        String priceAdequacyLabel,
        boolean localRecommended,
        boolean saved
) {

    public static RestaurantSummaryResponse from(RestaurantSummaryResult result) {
        return new RestaurantSummaryResponse(
                result.id(),
                result.name(),
                result.representativeMenuName(),
                result.representativeMenuPrice(),
                result.latitude(),
                result.longitude(),
                result.distanceMeters(),
                result.priceAdequacyLabel(),
                result.localRecommended(),
                result.saved()
        );
    }
}
