package com.example.hyunjiinserver.user.restaurant.enrichment.dto;

import java.util.List;
import java.util.UUID;

public record RestaurantEnrichmentCandidatesResponse(
        UUID jobId,
        int candidateCount,
        List<RestaurantEnrichmentCandidateResponse> candidates
) {
}
