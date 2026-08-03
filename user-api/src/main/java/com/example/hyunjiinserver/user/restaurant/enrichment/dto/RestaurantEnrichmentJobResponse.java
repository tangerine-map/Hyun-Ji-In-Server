package com.example.hyunjiinserver.user.restaurant.enrichment.dto;

import com.example.hyunjiinserver.user.restaurant.enrichment.RestaurantEnrichmentExecutionStatus;
import com.example.hyunjiinserver.user.restaurant.enrichment.RestaurantEnrichmentItemStatus;
import com.example.hyunjiinserver.user.restaurant.enrichment.RestaurantEnrichmentJob;
import com.example.hyunjiinserver.user.restaurant.enrichment.RestaurantEnrichmentReviewStatus;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record RestaurantEnrichmentJobResponse(
        UUID jobId,
        RestaurantEnrichmentExecutionStatus executionStatus,
        RestaurantEnrichmentReviewStatus reviewStatus,
        int requestedCount,
        int pendingCount,
        int runningCount,
        int completedCount,
        int skippedCount,
        int failedCount,
        int candidateCount,
        OffsetDateTime createdAt,
        List<RestaurantEnrichmentJobItemResponse> restaurants
) {

    public static RestaurantEnrichmentJobResponse from(RestaurantEnrichmentJob job) {
        return new RestaurantEnrichmentJobResponse(
                job.jobId(),
                job.executionStatus(),
                job.reviewStatus(),
                job.requestedCount(),
                job.count(RestaurantEnrichmentItemStatus.PENDING),
                job.count(RestaurantEnrichmentItemStatus.RUNNING),
                job.count(RestaurantEnrichmentItemStatus.COMPLETED),
                job.count(RestaurantEnrichmentItemStatus.SKIPPED),
                job.count(RestaurantEnrichmentItemStatus.FAILED),
                job.candidateCount(),
                job.createdAt(),
                job.items().stream().map(RestaurantEnrichmentJobItemResponse::from).toList()
        );
    }
}
