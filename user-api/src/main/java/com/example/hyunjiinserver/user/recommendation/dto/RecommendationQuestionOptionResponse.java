package com.example.hyunjiinserver.user.recommendation.dto;

import com.example.hyunjiinserver.core.recommendation.application.RecommendationQuestionOptionResult;
import io.swagger.v3.oas.annotations.media.Schema;

public record RecommendationQuestionOptionResponse(
        @Schema(description = "선택지 ID입니다. 추천 요청의 optionIds에 사용합니다.", example = "alone")
        String id,

        @Schema(description = "선택지 표시 문구입니다.", example = "혼자")
        String text
) {

    public static RecommendationQuestionOptionResponse from(RecommendationQuestionOptionResult result) {
        return new RecommendationQuestionOptionResponse(result.id(), result.text());
    }
}
