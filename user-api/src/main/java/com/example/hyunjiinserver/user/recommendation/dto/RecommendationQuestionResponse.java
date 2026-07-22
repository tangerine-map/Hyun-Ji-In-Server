package com.example.hyunjiinserver.user.recommendation.dto;

import com.example.hyunjiinserver.core.recommendation.application.RecommendationQuestionResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record RecommendationQuestionResponse(
        @Schema(description = "질문 ID입니다. 추천 요청의 questionId에 사용합니다.", example = "companion")
        String id,

        @Schema(description = "질문 표시 문구입니다.", example = "누구와 함께 식사하시나요?")
        String text,

        @Schema(description = "선택지를 여러 개 고를 수 있는지 여부입니다.", example = "false")
        boolean multiSelect,

        @Schema(description = "건너뛸 수 있는 질문인지 여부입니다.", example = "true")
        boolean skippable,

        @Schema(description = "선택지 목록입니다.")
        List<RecommendationQuestionOptionResponse> options
) {

    public static RecommendationQuestionResponse from(RecommendationQuestionResult result) {
        return new RecommendationQuestionResponse(
                result.id(),
                result.text(),
                result.multiSelect(),
                result.skippable(),
                result.options()
                        .stream()
                        .map(RecommendationQuestionOptionResponse::from)
                        .toList()
        );
    }
}
