package com.example.hyunjiinserver.core.useractivity.application;

import com.example.hyunjiinserver.core.useractivity.domain.SavedRestaurantSort;

public record GetSavedRestaurantsQuery(
        String deviceId,
        String sort,
        boolean localRecommendedOnly,
        boolean priceAdequateOnly,
        Double latitude,
        Double longitude
) {

    public SavedRestaurantSort sortType() {
        return SavedRestaurantSort.from(sort);
    }
}
