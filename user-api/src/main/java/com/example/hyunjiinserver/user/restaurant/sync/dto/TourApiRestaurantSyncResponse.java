package com.example.hyunjiinserver.user.restaurant.sync.dto;

import com.example.hyunjiinserver.core.restaurant.application.TourApiRestaurantSyncResult;

public record TourApiRestaurantSyncResponse(
        int pageNo,
        Integer nextPageNo,
        int fetchedCount,
        int createdCount,
        int updatedCount
) {

    public static TourApiRestaurantSyncResponse from(TourApiRestaurantSyncResult result) {
        return new TourApiRestaurantSyncResponse(
                result.pageNo(),
                result.nextPageNo(),
                result.fetchedCount(),
                result.createdCount(),
                result.updatedCount()
        );
    }
}
