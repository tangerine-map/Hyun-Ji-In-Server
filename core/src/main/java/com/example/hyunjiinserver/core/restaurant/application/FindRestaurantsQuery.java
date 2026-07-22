package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantMapSearchCondition;

public record FindRestaurantsQuery(
        double centerLatitude,
        double centerLongitude,
        Integer radiusMeters,
        String keyword,
        String category,
        Boolean localRecommended,
        Integer limit,
        String deviceId
) {

    private static final int DEFAULT_LIMIT = 50;
    private static final int DEFAULT_RADIUS_METERS = 3_000;
    private static final double METERS_PER_LATITUDE_DEGREE = 111_320.0;

    public FindRestaurantsQuery {
        radiusMeters = radiusMeters == null ? DEFAULT_RADIUS_METERS : radiusMeters;

        if (radiusMeters <= 0) {
            throw new BusinessException(RestaurantErrorCode.INVALID_MAP_BOUNDS);
        }

        limit = limit == null ? DEFAULT_LIMIT : limit;
    }

    public RestaurantMapSearchCondition toCondition() {
        double latitudeDelta = radiusMeters / METERS_PER_LATITUDE_DEGREE;
        double longitudeDelta = radiusMeters / (METERS_PER_LATITUDE_DEGREE * Math.cos(Math.toRadians(centerLatitude)));

        return new RestaurantMapSearchCondition(
                centerLatitude - latitudeDelta,
                centerLongitude - longitudeDelta,
                centerLatitude + latitudeDelta,
                centerLongitude + longitudeDelta,
                keyword,
                category,
                localRecommended,
                limit
        );
    }
}
