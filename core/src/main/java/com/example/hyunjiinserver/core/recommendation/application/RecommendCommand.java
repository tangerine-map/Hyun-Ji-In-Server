package com.example.hyunjiinserver.core.recommendation.application;

import java.util.List;

public record RecommendCommand(
        String deviceId,
        List<RecommendationAnswer> answers,
        Double latitude,
        Double longitude
) {

    public RecommendCommand {
        answers = answers == null ? List.of() : List.copyOf(answers);
    }
}
