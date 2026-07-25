package com.example.hyunjiinserver.user.restaurant.sync.dto;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantImportResult;

public record TourApiRestaurantSyncResponse(
        int fetchedCount,
        int createdCount,
        int updatedCount
) {

    public static TourApiRestaurantSyncResponse from(RestaurantImportResult result) {
        return new TourApiRestaurantSyncResponse(
                result.fetchedCount(),
                result.createdCount(),
                result.updatedCount()
        );
    }
}
