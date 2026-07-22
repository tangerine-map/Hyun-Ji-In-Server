package com.example.hyunjiinserver.user.recommendation.dto;

import com.example.hyunjiinserver.core.recommendation.application.RecommendCommand;
import com.example.hyunjiinserver.core.recommendation.application.RecommendationAnswer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.util.List;

public record RecommendRequest(
        @Schema(description = "질문 답변 목록입니다. 건너뛴 질문은 생략할 수 있으며, 전부 생략해도 됩니다.")
        @Valid
        List<RecommendationAnswerRequest> answers,

        @Schema(description = "현재 위치 위도입니다. 전달하면 거리 조건과 거리 표시에 사용됩니다.", example = "33.499621")
        @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
        Double latitude,

        @Schema(description = "현재 위치 경도입니다. 전달하면 거리 조건과 거리 표시에 사용됩니다.", example = "126.531188")
        @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
        Double longitude
) {

    public RecommendCommand toCommand(String deviceId) {
        List<RecommendationAnswer> answerList = answers == null
                ? List.of()
                : answers.stream().map(RecommendationAnswerRequest::toAnswer).toList();

        return new RecommendCommand(deviceId, answerList, latitude, longitude);
    }
}
