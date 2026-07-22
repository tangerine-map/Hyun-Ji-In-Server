package com.example.hyunjiinserver.user.recommendation.dto;

import com.example.hyunjiinserver.core.recommendation.application.RecommendationQuestionsResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record RecommendationQuestionsResponse(
        @Schema(description = "AI맛잘알 질문 목록입니다. 순서대로 표시합니다.")
        List<RecommendationQuestionResponse> questions
) {

    public static RecommendationQuestionsResponse from(RecommendationQuestionsResult result) {
        return new RecommendationQuestionsResponse(
                result.questions()
                        .stream()
                        .map(RecommendationQuestionResponse::from)
                        .toList()
        );
    }
}
