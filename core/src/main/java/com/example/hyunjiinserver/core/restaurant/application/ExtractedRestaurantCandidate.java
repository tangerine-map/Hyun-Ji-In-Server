package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentField;

public record ExtractedRestaurantCandidate(
        RestaurantEnrichmentField field,
        String valueText,
        Integer valueNumber,
        boolean representative,
        String sourceUrl,
        String evidence,
        double confidence
) {
}
