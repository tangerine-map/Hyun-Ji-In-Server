package com.example.hyunjiinserver.user.restaurant;

import com.example.hyunjiinserver.user.global.error.ErrorResponse;
import com.example.hyunjiinserver.user.global.error.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Restaurants", description = "식당 탐색 API")
@RequestMapping("/api/restaurants")
public interface RestaurantApi {

    @Operation(
            summary = "지도 영역 기반 식당 목록 조회",
            description = """
                    현재 위치 또는 지도 중심 좌표를 기준으로 주변 식당을 조회합니다.

                    - 앱 최초 실행 시 현재 위치 좌표를 `centerLatitude`, `centerLongitude`로 전달합니다.
                    - 위치 권한이 거부된 경우 기본 지역(예: 제주시 중심) 좌표를 전달합니다.
                    - 지도를 이동하거나 확대/축소한 경우 현재 지도 중심 좌표와 화면에 맞는 반경을 전달합니다.
                    - 검색어와 빠른 필터를 함께 전달하면 지도 마커와 바텀시트 리스트를 같은 결과로 동기화할 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "지도 영역 식당 목록 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = RestaurantMapResponse.class),
                            examples = @ExampleObject(
                                    name = "지도 식당 목록 예시",
                                    value = """
                                            {
                                              "restaurants": [
                                                {
                                                  "id": 1,
                                                  "name": "제주 고기국수 현지인집",
                                                  "representativeMenuName": "고기국수",
                                                  "representativeMenuPrice": 9000,
                                                  "latitude": 33.500912,
                                                  "longitude": 126.529756,
                                                  "distanceMeters": 430,
                                                  "priceAdequacyLabel": "가격 적정",
                                                  "localRecommended": true,
                                                  "saved": false
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 파라미터가 올바르지 않음",
                    content = @Content(
                            schema = @Schema(implementation = ValidationErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "지도 조회 검증 실패 예시",
                                    value = """
                                            {
                                              "code": "VALIDATION_ERROR",
                                              "message": "요청 값이 올바르지 않습니다.",
                                              "fields": [
                                                {
                                                  "field": "centerLatitude",
                                                  "message": "중심 위도는 필수입니다."
                                                },
                                                {
                                                  "field": "limit",
                                                  "message": "조회 개수는 100 이하여야 합니다."
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
    @GetMapping("/map")
    RestaurantMapResponse findRestaurants(
            @Valid @ParameterObject @ModelAttribute RestaurantMapSearchRequest request
    );

    @Operation(
            summary = "식당 상세 조회",
            description = "식당 카드 선택 시 필요한 상세 정보, 대표 메뉴, 가격 적정성, 현지인 추천 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "식당 상세 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = RestaurantDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "식당 상세 예시",
                                    value = """
                                            {
                                              "id": 1,
                                              "name": "제주 고기국수 현지인집",
                                              "category": "음식점",
                                              "address": "제주특별자치도 제주시 중앙로 1",
                                              "phoneNumber": "064-000-0000",
                                              "openingHours": "10:00-20:00",
                                              "latitude": 33.500912,
                                              "longitude": 126.529756,
                                              "summary": "현지인이 자주 찾는 고기국수 식당입니다.",
                                              "localRecommended": true,
                                              "localRecommendationReason": "관광지 근처지만 가격과 맛이 안정적입니다.",
                                              "priceAdequacyLabel": "가격 적정",
                                              "priceAdequacyDescription": "주변 유사 메뉴 대비 평균 가격대입니다.",
                                              "saved": false,
                                              "representativeMenus": [
                                                {
                                                  "id": 10,
                                                  "name": "고기국수",
                                                  "price": 9000,
                                                  "representative": true
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "식당을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "식당 없음 예시",
                                    value = """
                                            {
                                              "code": "RESTAURANT_NOT_FOUND",
                                              "message": "식당을 찾을 수 없습니다."
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
    @GetMapping("/{restaurantId}")
    RestaurantDetailResponse getRestaurantDetail(
            @Parameter(description = "조회할 식당 ID입니다.", example = "1", required = true)
            @PathVariable Long restaurantId
    );

    @Operation(
            summary = "식당 현지인 코멘트 조회",
            description = """
                    식당 상세 화면에서 노출할 현지인 코멘트 목록을 조회합니다.

                    - `restaurantId`는 식당 상세 조회 응답의 `id`를 전달합니다.
                    - `sort`는 생략 가능하며, 지원하지 않는 값은 서버 기본 정렬로 처리됩니다.
                    - `limit`로 상세 화면 첫 페이지에 필요한 개수만 제한해서 가져올 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "현지인 코멘트 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = RestaurantCommentsResponse.class),
                            examples = @ExampleObject(
                                    name = "현지인 코멘트 예시",
                                    value = """
                                            {
                                              "comments": [
                                                {
                                                  "id": 100,
                                                  "authorContext": "제주 거주자",
                                                  "content": "점심시간에는 대기가 있지만 회전이 빨라요.",
                                                  "helpfulCount": 12,
                                                  "createdAt": "2026-07-21T12:30:00+09:00"
                                                },
                                                {
                                                  "id": 101,
                                                  "authorContext": "근처 직장인",
                                                  "content": "관광지 근처 치고 가격이 과하지 않은 편입니다.",
                                                  "helpfulCount": 8,
                                                  "createdAt": "2026-07-20T18:10:00+09:00"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 파라미터가 올바르지 않음",
                    content = @Content(
                            schema = @Schema(implementation = ValidationErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "코멘트 조회 검증 실패 예시",
                                    value = """
                                            {
                                              "code": "VALIDATION_ERROR",
                                              "message": "요청 값이 올바르지 않습니다.",
                                              "fields": [
                                                {
                                                  "field": "limit",
                                                  "message": "조회 개수는 100 이하여야 합니다."
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "식당을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "식당 없음 예시",
                                    value = """
                                            {
                                              "code": "RESTAURANT_NOT_FOUND",
                                              "message": "식당을 찾을 수 없습니다."
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
    @GetMapping("/{restaurantId}/comments")
    RestaurantCommentsResponse getRestaurantComments(
            @Parameter(description = "코멘트를 조회할 식당 ID입니다.", example = "1", required = true)
            @PathVariable Long restaurantId,
            @Valid @ParameterObject @ModelAttribute RestaurantCommentSearchRequest request
    );
}
