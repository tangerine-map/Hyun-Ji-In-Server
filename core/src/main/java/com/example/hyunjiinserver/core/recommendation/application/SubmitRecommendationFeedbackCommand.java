package com.example.hyunjiinserver.core.recommendation.application;

import java.util.List;

public record SubmitRecommendationFeedbackCommand(
        String deviceId,
        String sessionId,
        Long restaurantId,
        boolean liked,
        List<String> reasons
) {

    public SubmitRecommendationFeedbackCommand {
        reasons = reasons == null ? List.of() : List.copyOf(reasons);
    }
}
