package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentField;
import java.util.Set;

public record RestaurantEnrichmentPipelineResult(
        Long restaurantId,
        String restaurantName,
        boolean skipped,
        Set<RestaurantEnrichmentField> missingFields,
        int searchedSourceCount,
        int fetchedSourceCount,
        int appliedFieldCount
) {

    public static RestaurantEnrichmentPipelineResult skipped(RestaurantEnrichmentPlan plan) {
        return new RestaurantEnrichmentPipelineResult(
                plan.restaurantId(),
                plan.restaurantName(),
                true,
                plan.missingFields(),
                0,
                0,
                0
        );
    }
}
