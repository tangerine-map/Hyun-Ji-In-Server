package com.example.hyunjiinserver.core.restaurant.application;

import java.util.List;

public record RestaurantInformationEnrichmentResult(
        List<ExtractedRestaurantCandidate> candidates,
        int sourceCount,
        int fetchedCount
) {

    public RestaurantInformationEnrichmentResult {
        candidates = List.copyOf(candidates);
    }
}
