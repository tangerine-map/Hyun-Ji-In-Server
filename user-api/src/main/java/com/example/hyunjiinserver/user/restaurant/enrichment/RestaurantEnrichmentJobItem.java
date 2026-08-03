package com.example.hyunjiinserver.user.restaurant.enrichment;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantEnrichmentPipelineResult;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentField;
import java.util.Set;

public record RestaurantEnrichmentJobItem(
        Long restaurantId,
        String restaurantName,
        RestaurantEnrichmentItemStatus status,
        Set<RestaurantEnrichmentField> missingFields,
        int searchedSourceCount,
        int fetchedSourceCount,
        int candidateCount,
        String errorMessage
) {

    static RestaurantEnrichmentJobItem pending(Long restaurantId) {
        return new RestaurantEnrichmentJobItem(
                restaurantId,
                null,
                RestaurantEnrichmentItemStatus.PENDING,
                Set.of(),
                0,
                0,
                0,
                null
        );
    }

    RestaurantEnrichmentJobItem running() {
        return new RestaurantEnrichmentJobItem(
                restaurantId, restaurantName, RestaurantEnrichmentItemStatus.RUNNING,
                missingFields, searchedSourceCount, fetchedSourceCount, candidateCount, null
        );
    }

    static RestaurantEnrichmentJobItem completed(RestaurantEnrichmentPipelineResult result) {
        return new RestaurantEnrichmentJobItem(
                result.restaurantId(),
                result.restaurantName(),
                result.skipped() ? RestaurantEnrichmentItemStatus.SKIPPED : RestaurantEnrichmentItemStatus.COMPLETED,
                result.missingFields(),
                result.searchedSourceCount(),
                result.fetchedSourceCount(),
                result.candidateCount(),
                null
        );
    }

    RestaurantEnrichmentJobItem failed(String message) {
        return new RestaurantEnrichmentJobItem(
                restaurantId, restaurantName, RestaurantEnrichmentItemStatus.FAILED,
                missingFields, searchedSourceCount, fetchedSourceCount, candidateCount, message
        );
    }
}
