package com.example.hyunjiinserver.user.restaurant.sync;

import com.example.hyunjiinserver.user.global.error.dto.ErrorResponse;
import com.example.hyunjiinserver.user.global.error.dto.ValidationErrorResponse;
import com.example.hyunjiinserver.user.restaurant.sync.dto.TourApiRestaurantSyncRequest;
import com.example.hyunjiinserver.user.restaurant.sync.dto.TourApiRestaurantSyncResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Internal TourAPI Sync", description = "한국관광공사 음식점 데이터 수동 동기화 API")
@RequestMapping("/api/internal/tour-api/restaurants")
public interface TourApiRestaurantSyncApi {

    @Operation(
            summary = "제주 음식점 수동 동기화",
            description = "요청한 개수만큼 한국관광공사 음식점 정보를 조회하여 생성하거나 갱신합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "동기화 완료",
                    content = @Content(schema = @Schema(implementation = TourApiRestaurantSyncResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "동기화 API 인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "한국관광공사 API 호출 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/sync")
    TourApiRestaurantSyncResponse synchronize(
            @Parameter(description = "서버에 설정한 수동 동기화 인증키", required = true)
            @RequestHeader(value = "X-Sync-Api-Key", required = false) String syncApiKey,
            @Valid @RequestBody TourApiRestaurantSyncRequest request
    );
}
