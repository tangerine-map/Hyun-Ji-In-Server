package com.example.hyunjiinserver.user.restaurant.sync.dto;

import com.example.hyunjiinserver.user.restaurant.sync.TourApiRestaurantSyncJob;
import com.example.hyunjiinserver.user.restaurant.sync.TourApiRestaurantSyncJobStatus;
import java.util.UUID;

public record TourApiRestaurantSyncResponse(
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

    public static TourApiRestaurantSyncResponse from(TourApiRestaurantSyncJob job) {
        return new TourApiRestaurantSyncResponse(
                job.jobId(),
                job.status(),
                job.pageNo(),
                job.nextPageNo(),
                job.maxItems(),
                job.fetchedCount(),
                job.createdCount(),
                job.updatedCount(),
                job.failedCount(),
                job.errorMessage()
        );
    }
}
