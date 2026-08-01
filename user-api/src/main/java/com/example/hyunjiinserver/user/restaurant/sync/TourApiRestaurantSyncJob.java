package com.example.hyunjiinserver.user.restaurant.sync;

import com.example.hyunjiinserver.core.restaurant.application.TourApiRestaurantSyncResult;
import java.util.UUID;

public record TourApiRestaurantSyncJob(
        UUID jobId,
        TourApiRestaurantSyncJobStatus status,
        int pageNo,
        Integer nextPageNo,
        int maxItems,
        int fetchedCount,
        int createdCount,
        int updatedCount,
        int failedCount,
        String errorMessage
) {

    static TourApiRestaurantSyncJob running(UUID jobId, int pageNo, int maxItems) {
        return new TourApiRestaurantSyncJob(
                jobId,
                TourApiRestaurantSyncJobStatus.RUNNING,
                pageNo,
                null,
                maxItems,
                0,
                0,
                0,
                0,
                null
        );
    }

    TourApiRestaurantSyncJob completed(TourApiRestaurantSyncResult result) {
        return new TourApiRestaurantSyncJob(
                jobId,
                TourApiRestaurantSyncJobStatus.COMPLETED,
                result.pageNo(),
                result.nextPageNo(),
                maxItems,
                result.fetchedCount(),
                result.createdCount(),
                result.updatedCount(),
                result.failedCount(),
                null
        );
    }

    TourApiRestaurantSyncJob failed(String message) {
        return new TourApiRestaurantSyncJob(
                jobId,
                TourApiRestaurantSyncJobStatus.FAILED,
                pageNo,
                null,
                maxItems,
                0,
                0,
                0,
                0,
                message
        );
    }
}
