package com.example.hyunjiinserver.user.restaurant.enrichment.dto;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantEnrichmentCandidateResult;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentField;
import java.time.OffsetDateTime;

public record RestaurantEnrichmentCandidateResponse(
        Long candidateId,
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

    public static RestaurantEnrichmentCandidateResponse from(RestaurantEnrichmentCandidateResult result) {
        return new RestaurantEnrichmentCandidateResponse(
                result.id(),
                result.restaurantId(),
                result.field(),
                result.valueText(),
                result.valueNumber(),
                result.representative(),
                result.sourceUrl(),
                result.evidence(),
                result.confidence(),
                result.accepted(),
                result.createdAt()
        );
    }
}
