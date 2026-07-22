package com.example.hyunjiinserver.core.recommendation.application;

import java.util.List;

public record RecommendationQuestionsResult(
        List<RecommendationQuestionResult> questions
) {

    public RecommendationQuestionsResult {
        questions = List.copyOf(questions);
    }
}
