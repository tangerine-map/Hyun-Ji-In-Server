package com.example.hyunjiinserver.user.recommendation.dto;

import com.example.hyunjiinserver.core.recommendation.application.RecommendationAnswer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record RecommendationAnswerRequest(
        @Schema(description = "답변한 질문 ID입니다. 질문 목록 조회 응답의 id를 사용합니다.", example = "companion")
        @NotBlank(message = "질문 ID는 필수입니다.")
        String questionId,

        @Schema(description = "선택한 선택지 ID 목록입니다. multiSelect가 false인 질문은 1개만 전달합니다.", example = "[\"alone\"]")
        List<String> optionIds
) {

    public RecommendationAnswer toAnswer() {
        return new RecommendationAnswer(questionId, optionIds);
    }
}
