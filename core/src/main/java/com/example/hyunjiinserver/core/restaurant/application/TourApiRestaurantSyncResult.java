package com.example.hyunjiinserver.core.restaurant.application;

public record TourApiRestaurantSyncResult(
        int pageNo,
        Integer nextPageNo,
        int fetchedCount,
        int createdCount,
        int updatedCount
) {

    public static TourApiRestaurantSyncResult of(
            TourApiRestaurantPage page,
            RestaurantImportResult importResult
    ) {
        return new TourApiRestaurantSyncResult(
                page.pageNo(),
                page.nextPageNo(),
                importResult.fetchedCount(),
                importResult.createdCount(),
                importResult.updatedCount()
        );
    }
}
