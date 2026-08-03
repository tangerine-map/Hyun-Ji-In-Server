package com.example.hyunjiinserver.user.restaurant.enrichment.dto;

import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentField;
import com.example.hyunjiinserver.user.restaurant.enrichment.RestaurantEnrichmentJobItem;
import com.example.hyunjiinserver.user.restaurant.enrichment.RestaurantEnrichmentItemStatus;
import java.util.Set;

public record RestaurantEnrichmentJobItemResponse(
        Long restaurantId,
        String restaurantName,
        RestaurantEnrichmentItemStatus status,
        Set<RestaurantEnrichmentField> missingFields,
        int searchedSourceCount,
        int fetchedSourceCount,
        int appliedFieldCount,
        String errorMessage
) {

    public static RestaurantEnrichmentJobItemResponse from(RestaurantEnrichmentJobItem item) {
        return new RestaurantEnrichmentJobItemResponse(
                item.restaurantId(),
                item.restaurantName(),
                item.status(),
                item.missingFields(),
                item.searchedSourceCount(),
                item.fetchedSourceCount(),
                item.appliedFieldCount(),
                item.errorMessage()
        );
    }
}
