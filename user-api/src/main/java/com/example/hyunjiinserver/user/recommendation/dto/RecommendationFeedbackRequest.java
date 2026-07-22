package com.example.hyunjiinserver.user.recommendation.dto;

import com.example.hyunjiinserver.core.recommendation.application.SubmitRecommendationFeedbackCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RecommendationFeedbackRequest(
        @Schema(description = "피드백 대상 식당 ID입니다.", example = "1")
        @NotNull(message = "식당 ID는 필수입니다.")
        Long restaurantId,

        @Schema(description = "좋아요 여부입니다. true는 좋아요, false는 별로예요를 의미합니다.", example = "false")
        @NotNull(message = "좋아요 여부는 필수입니다.")
        Boolean liked,

        @Schema(
                description = "별로예요를 선택한 경우의 이유 목록입니다. 예: 비쌈, 분위기 아님, 메뉴 취향 아님, 너무 멂",
                example = "[\"비쌈\", \"너무 멂\"]"
        )
        List<String> reasons
) {

    public SubmitRecommendationFeedbackCommand toCommand(String deviceId, String sessionId) {
        return new SubmitRecommendationFeedbackCommand(deviceId, sessionId, restaurantId, liked, reasons);
    }
}
