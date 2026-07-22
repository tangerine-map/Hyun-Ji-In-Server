package com.example.hyunjiinserver.user.recommendation;

import com.example.hyunjiinserver.user.global.error.dto.ErrorResponse;
import com.example.hyunjiinserver.user.global.error.dto.ValidationErrorResponse;
import com.example.hyunjiinserver.user.recommendation.dto.RecommendRequest;
import com.example.hyunjiinserver.user.recommendation.dto.RecommendationFeedbackRequest;
import com.example.hyunjiinserver.user.recommendation.dto.RecommendationQuestionsResponse;
import com.example.hyunjiinserver.user.recommendation.dto.RecommendationsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Recommendations", description = "AI맛잘알 맞춤 추천 API (추천 엔진 준비 중 - 현재 추천 결과는 빈 목록으로 반환됩니다)")
@RequestMapping("/api/recommendations")
public interface RecommendationApi {

    @Operation(
            summary = "AI맛잘알 질문 목록 조회",
            description = """
                    AI맛잘알 탭 진입 시 표시할 질문 플로우를 조회합니다.

                    - 질문은 순서대로 표시하며, 모두 선택형입니다.
                    - `skippable`이 true인 질문은 건너뛸 수 있습니다.
                    - `multiSelect`가 true인 질문은 선택지를 여러 개 고를 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "질문 목록 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = RecommendationQuestionsResponse.class),
                            examples = @ExampleObject(
                                    name = "질문 목록 예시",
                                    value = """
                                            {
                                              "questions": [
                                                {
                                                  "id": "companion",
                                                  "text": "누구와 함께 식사하시나요?",
                                                  "multiSelect": false,
                                                  "skippable": true,
                                                  "options": [
                                                    { "id": "alone", "text": "혼자" },
                                                    { "id": "couple", "text": "연인" },
                                                    { "id": "friends", "text": "친구" },
                                                    { "id": "family", "text": "가족" }
                                                  ]
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/questions")
    RecommendationQuestionsResponse getQuestions();

    @Operation(
            summary = "AI맛잘알 식당 추천",
            description = """
                    질문 답변을 기반으로 맞춤 식당을 추천합니다.

                    **추천 엔진 준비 중**: 현재는 API 계약만 확정된 상태로, `restaurants`는 항상 빈 목록으로 반환됩니다.
                    응답의 `sessionId`는 다른 추천 보기(refresh)와 피드백 요청에 사용합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "추천 성공",
                    content = @Content(
                            schema = @Schema(implementation = RecommendationsResponse.class),
                            examples = @ExampleObject(
                                    name = "추천 결과 예시 (엔진 준비 중)",
                                    value = """
                                            {
                                              "sessionId": "b1a4f3e0-6a9b-4c1e-9d2f-0a1b2c3d4e5f",
                                              "restaurants": []
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    RecommendationsResponse recommend(
            @Parameter(description = "기기 식별자입니다. 추천 세션과 피드백은 이 값 기준으로 관리됩니다.", example = "device-1234", required = true)
            @RequestHeader("X-Device-Id") String deviceId,
            @Valid @RequestBody RecommendRequest request
    );

    @Operation(
            summary = "다른 추천 보기",
            description = """
                    동일한 답변 조건으로 다른 식당을 추천받습니다.

                    **추천 엔진 준비 중**: 현재는 `restaurants`가 항상 빈 목록으로 반환됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "재추천 성공",
                    content = @Content(schema = @Schema(implementation = RecommendationsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/{sessionId}/refresh")
    RecommendationsResponse refresh(
            @Parameter(description = "기기 식별자입니다.", example = "device-1234", required = true)
            @RequestHeader("X-Device-Id") String deviceId,
            @Parameter(description = "추천 응답에서 받은 세션 ID입니다.", example = "b1a4f3e0-6a9b-4c1e-9d2f-0a1b2c3d4e5f", required = true)
            @PathVariable String sessionId
    );

    @Operation(
            summary = "추천 결과 피드백",
            description = """
                    추천 결과에 대해 좋아요/별로예요 피드백을 남깁니다.

                    - `liked: false`인 경우 `reasons`로 이유(비쌈, 분위기 아님, 메뉴 취향 아님, 너무 멂 등)를 전달할 수 있습니다.
                    - **추천 엔진 준비 중**: 현재는 피드백을 수신만 하고 추천에 반영하지 않습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "피드백 접수 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/{sessionId}/feedback")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void submitFeedback(
            @Parameter(description = "기기 식별자입니다.", example = "device-1234", required = true)
            @RequestHeader("X-Device-Id") String deviceId,
            @Parameter(description = "추천 응답에서 받은 세션 ID입니다.", example = "b1a4f3e0-6a9b-4c1e-9d2f-0a1b2c3d4e5f", required = true)
            @PathVariable String sessionId,
            @Valid @RequestBody RecommendationFeedbackRequest request
    );
}
