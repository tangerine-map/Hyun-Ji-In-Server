package com.example.hyunjiinserver.user.recommendation.dto;

import com.example.hyunjiinserver.core.recommendation.application.RecommendationsResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record RecommendationsResponse(
        @Schema(description = "추천 세션 ID입니다. 다른 추천 보기와 피드백 요청에 사용합니다.", example = "b1a4f3e0-6a9b-4c1e-9d2f-0a1b2c3d4e5f")
        String sessionId,

        @Schema(description = "추천 식당 목록입니다. 추천 엔진 준비 전에는 빈 목록이 반환됩니다.")
        List<RecommendedRestaurantResponse> restaurants
) {

    public static RecommendationsResponse from(RecommendationsResult result) {
        return new RecommendationsResponse(
                result.sessionId(),
                result.restaurants()
                        .stream()
                        .map(RecommendedRestaurantResponse::from)
                        .toList()
        );
    }
}
