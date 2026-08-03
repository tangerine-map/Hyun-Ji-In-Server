package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentField;
import java.util.List;
import java.util.Set;

public record RestaurantEnrichmentPlan(
        Long restaurantId,
        String restaurantName,
        String address,
        double latitude,
        double longitude,
        Set<RestaurantEnrichmentField> missingFields,
        List<String> existingMenuNames
) {

    public boolean skipped() {
        return missingFields.isEmpty();
    }
}
