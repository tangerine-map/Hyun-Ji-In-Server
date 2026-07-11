package com.example.hyunjiinserver.user.restaurant;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantDetailResult;
import java.util.List;

public record RestaurantDetailResponse(
        Long id,
        String name,
        String category,
        String address,
        String phoneNumber,
        String openingHours,
        double latitude,
        double longitude,
        String summary,
        boolean localRecommended,
        String localRecommendationReason,
        String priceAdequacyLabel,
        String priceAdequacyDescription,
        boolean saved,
        List<RestaurantMenuResponse> representativeMenus
) {

    public static RestaurantDetailResponse from(RestaurantDetailResult result) {
        return new RestaurantDetailResponse(
                result.id(),
                result.name(),
                result.category(),
                result.address(),
                result.phoneNumber(),
                result.openingHours(),
                result.latitude(),
                result.longitude(),
                result.summary(),
                result.localRecommended(),
                result.localRecommendationReason(),
                result.priceAdequacyLabel(),
                result.priceAdequacyDescription(),
                result.saved(),
                result.representativeMenus()
                        .stream()
                        .map(RestaurantMenuResponse::from)
                        .toList()
        );
    }
}
