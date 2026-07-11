package com.example.hyunjiinserver.user.restaurant;

import com.example.hyunjiinserver.user.global.error.ErrorResponse;
import com.example.hyunjiinserver.user.global.error.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
            description = "현재 지도 바운딩 박스와 검색/필터 조건을 기준으로 식당 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "지도 영역 식당 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = RestaurantMapResponse.class))
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
                    content = @Content(schema = @Schema(implementation = RestaurantDetailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "식당을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{restaurantId}")
    RestaurantDetailResponse getRestaurantDetail(
            @PathVariable Long restaurantId
    );

    @Operation(
            summary = "식당 현지인 코멘트 조회",
            description = "식당 상세 화면에서 노출할 현지인 코멘트 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "현지인 코멘트 조회 성공",
                    content = @Content(schema = @Schema(implementation = RestaurantCommentsResponse.class))
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
    @GetMapping("/{restaurantId}/comments")
    RestaurantCommentsResponse getRestaurantComments(
            @PathVariable Long restaurantId,
            @Valid @ParameterObject @ModelAttribute RestaurantCommentSearchRequest request
    );
}
