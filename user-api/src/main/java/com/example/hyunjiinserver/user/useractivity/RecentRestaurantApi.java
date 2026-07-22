package com.example.hyunjiinserver.user.useractivity;

import com.example.hyunjiinserver.user.global.error.dto.ErrorResponse;
import com.example.hyunjiinserver.user.global.error.dto.ValidationErrorResponse;
import com.example.hyunjiinserver.user.useractivity.dto.RecentRestaurantListRequest;
import com.example.hyunjiinserver.user.useractivity.dto.RecentRestaurantsResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "RecentRestaurants", description = "최근 본 식당 API")
@RequestMapping("/api/recent-restaurants")
public interface RecentRestaurantApi {

    @Operation(
            summary = "최근 본 식당 목록 조회",
            description = """
                    마이페이지의 최근 본 식당 목록을 최신 열람순으로 조회합니다.

                    - 식당 상세를 X-Device-Id 헤더와 함께 조회하면 자동으로 이 목록에 추가됩니다.
                    - 같은 식당을 다시 열람하면 목록 상단으로 이동합니다.
                    - 기기당 최근 50개까지 보관하며, 초과분은 오래된 항목부터 자동 삭제됩니다.
                    - `latitude`, `longitude`를 전달하면 각 식당까지의 거리가 포함됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "최근 본 식당 목록 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = RecentRestaurantsResponse.class),
                            examples = @ExampleObject(
                                    name = "최근 본 목록 예시",
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
                                                  "saved": false,
                                                  "viewedAt": "2026-07-22T12:30:00+09:00"
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
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping
    RecentRestaurantsResponse getRecentRestaurants(
            @Parameter(description = "기기 식별자입니다. 최근 본 목록은 이 값 기준으로 관리됩니다.", example = "device-1234", required = true)
            @RequestHeader("X-Device-Id") String deviceId,
            @Valid @ParameterObject @ModelAttribute RecentRestaurantListRequest request
    );

    @Operation(
            summary = "최근 본 식당 개별 삭제",
            description = "최근 본 목록에서 특정 식당을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "최근 본 목록에 없는 식당",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "최근 본 항목 없음 예시",
                                    value = """
                                            {
                                              "code": "RECENT_VIEW_NOT_FOUND",
                                              "message": "최근 본 식당을 찾을 수 없습니다."
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
    @DeleteMapping("/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteRecentRestaurant(
            @Parameter(description = "기기 식별자입니다.", example = "device-1234", required = true)
            @RequestHeader("X-Device-Id") String deviceId,
            @Parameter(description = "최근 본 목록에서 삭제할 식당 ID입니다.", example = "1", required = true)
            @PathVariable Long restaurantId
    );

    @Operation(
            summary = "최근 본 식당 전체 삭제",
            description = "최근 본 목록을 모두 비웁니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "전체 삭제 성공"),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void clearRecentRestaurants(
            @Parameter(description = "기기 식별자입니다.", example = "device-1234", required = true)
            @RequestHeader("X-Device-Id") String deviceId
    );
}
