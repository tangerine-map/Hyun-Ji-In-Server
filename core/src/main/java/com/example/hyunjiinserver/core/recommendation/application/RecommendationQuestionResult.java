package com.example.hyunjiinserver.core.recommendation.application;

import java.util.List;

public record RecommendationQuestionResult(
        String id,
        String text,
        boolean multiSelect,
        boolean skippable,
        List<RecommendationQuestionOptionResult> options
) {

    public RecommendationQuestionResult {
        options = List.copyOf(options);
    }
}
