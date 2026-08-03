package com.example.hyunjiinserver.user.restaurant.enrichment.dto;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantEnrichmentApplyResult;
import com.example.hyunjiinserver.user.restaurant.enrichment.RestaurantEnrichmentReviewStatus;
import java.util.UUID;

public record RestaurantEnrichmentApplyResponse(
        UUID jobId,
        int requestedCount,
        int appliedCount,
        int skippedCount,
        RestaurantEnrichmentReviewStatus reviewStatus
) {

    public static RestaurantEnrichmentApplyResponse from(
            UUID jobId,
            RestaurantEnrichmentApplyResult result,
            RestaurantEnrichmentReviewStatus reviewStatus
    ) {
        return new RestaurantEnrichmentApplyResponse(
                jobId,
                result.requestedCount(),
                result.appliedCount(),
                result.skippedCount(),
                reviewStatus
        );
    }
}
