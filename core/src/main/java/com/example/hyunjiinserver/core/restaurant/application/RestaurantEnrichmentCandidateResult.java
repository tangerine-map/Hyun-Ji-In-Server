package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentCandidate;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentField;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RestaurantEnrichmentCandidateResult(
        Long id,
        UUID jobId,
        Long restaurantId,
        RestaurantEnrichmentField field,
        String valueText,
        Integer valueNumber,
        boolean representative,
        String sourceUrl,
        String evidence,
        double confidence,
        boolean accepted,
        OffsetDateTime createdAt
) {

    public static RestaurantEnrichmentCandidateResult from(RestaurantEnrichmentCandidate candidate) {
        return new RestaurantEnrichmentCandidateResult(
                candidate.getId(),
                candidate.jobIdAsUuid(),
                candidate.getRestaurantId(),
                candidate.getField(),
                candidate.getValueText(),
                candidate.getValueNumber(),
                candidate.isRepresentative(),
                candidate.getSourceUrl(),
                candidate.getEvidence(),
                candidate.getConfidence(),
                candidate.isAccepted(),
                candidate.getCreatedAt()
        );
    }
}
