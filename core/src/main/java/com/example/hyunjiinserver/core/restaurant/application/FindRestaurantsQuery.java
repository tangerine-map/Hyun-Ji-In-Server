package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantMapSearchCondition;

public record FindRestaurantsQuery(
        double southWestLatitude,
        double southWestLongitude,
        double northEastLatitude,
        double northEastLongitude,
        Double userLatitude,
        Double userLongitude,
        String keyword,
        String category,
        Boolean localRecommended,
        Integer limit
) {

    private static final int DEFAULT_LIMIT = 50;

    public FindRestaurantsQuery {
        if (southWestLatitude >= northEastLatitude || southWestLongitude >= northEastLongitude) {
            throw new BusinessException(RestaurantErrorCode.INVALID_MAP_BOUNDS);
        }

        limit = limit == null ? DEFAULT_LIMIT : limit;
    }

    public RestaurantMapSearchCondition toCondition() {
        return new RestaurantMapSearchCondition(
                southWestLatitude,
                southWestLongitude,
                northEastLatitude,
                northEastLongitude,
                keyword,
                category,
                localRecommended,
                limit
        );
    }
}
