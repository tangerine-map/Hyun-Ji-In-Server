package com.example.hyunjiinserver.core.recommendation.application;

import java.util.List;

public record RecommendationAnswer(
        String questionId,
        List<String> optionIds
) {

    public RecommendationAnswer {
        optionIds = optionIds == null ? List.of() : List.copyOf(optionIds);
    }
}
