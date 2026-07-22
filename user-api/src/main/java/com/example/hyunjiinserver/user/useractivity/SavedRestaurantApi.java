package com.example.hyunjiinserver.user.useractivity;

import com.example.hyunjiinserver.user.global.error.dto.ErrorResponse;
import com.example.hyunjiinserver.user.global.error.dto.ValidationErrorResponse;
import com.example.hyunjiinserver.user.useractivity.dto.SavedRestaurantListRequest;
import com.example.hyunjiinserver.user.useractivity.dto.SavedRestaurantsResponse;
import com.example.hyunjiinserver.user.useractivity.dto.ToggleSavedRestaurantResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "SavedRestaurants", description = "저장(찜) 식당 API")
@RequestMapping("/api/saved-restaurants")
public interface SavedRestaurantApi {

    @Operation(
            summary = "식당 저장(찜) 토글",
            description = """
                    식당 카드 또는 상세 화면의 저장 버튼 클릭 시 호출합니다.

                    - 저장되지 않은 식당이면 저장하고 `saved: true`를 반환합니다.
                    - 이미 저장된 식당이면 저장을 해제하고 `saved: false`를 반환합니다.
                    - 응답의 `saved` 값으로 모든 화면의 저장 상태를 즉시 동기화합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "저장 토글 성공",
                    content = @Content(
                            schema = @Schema(implementation = ToggleSavedRestaurantResponse.class),
                            examples = @ExampleObject(
                                    name = "저장 토글 예시",
                                    value = """
                                            {
                                              "restaurantId": 1,
                                              "saved": true
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
    @PostMapping("/{restaurantId}/toggle")
    ToggleSavedRestaurantResponse toggleSavedRestaurant(
            @Parameter(description = "기기 식별자입니다. 저장 목록은 이 값 기준으로 관리됩니다.", example = "device-1234", required = true)
            @RequestHeader("X-Device-Id") String deviceId,
            @Parameter(description = "저장 상태를 변경할 식당 ID입니다.", example = "1", required = true)
            @PathVariable Long restaurantId
    );

    @Operation(
            summary = "저장한 식당 목록 조회",
            description = """
                    마이페이지의 저장된 식당 목록을 조회합니다.

                    - 기본 정렬은 최근 저장순입니다.
                    - `sort=distance`로 거리순 정렬이 가능하며, 이 경우 `latitude`, `longitude`가 필요합니다.
                    - `localRecommendedOnly`, `priceAdequateOnly`로 목록을 필터링할 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "저장한 식당 목록 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = SavedRestaurantsResponse.class),
                            examples = @ExampleObject(
                                    name = "저장 목록 예시",
                                    value = """
                                            {
                                              "restaurants": [
                                                {
                                                  "restaurantId": 1,
                                                  "name": "제주 고기국수 현지인집",
                                                  "representativeMenuName": "고기국수",
                                                  "representativeMenuPrice": 9000,
                                                  "latitude": 33.500912,
                                                  "longitude": 126.529756,
                                                  "distanceMeters": 430,
                                                  "priceAdequacyLabel": "가격 적정",
                                                  "localRecommended": true,
                                                  "savedAt": "2026-07-21T12:30:00+09:00"
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
                                    name = "거리순 정렬 좌표 누락 예시",
                                    value = """
                                            {
                                              "code": "LOCATION_REQUIRED_FOR_DISTANCE_SORT",
                                              "message": "거리순 정렬에는 현재 위치 좌표가 필요합니다."
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
    @GetMapping
    SavedRestaurantsResponse getSavedRestaurants(
            @Parameter(description = "기기 식별자입니다. 저장 목록은 이 값 기준으로 관리됩니다.", example = "device-1234", required = true)
            @RequestHeader("X-Device-Id") String deviceId,
            @Valid @ParameterObject @ModelAttribute SavedRestaurantListRequest request
    );
}
