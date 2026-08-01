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
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Internal TourAPI Sync", description = "한국관광공사 음식점 데이터 수동 동기화 API")
@RequestMapping("/api/internal/tour-api/restaurants")
public interface TourApiRestaurantSyncApi {

    @Operation(
            summary = "제주 음식점 동기화 작업 시작",
            description = "동기화 작업을 백그라운드에서 시작하고 즉시 jobId를 반환합니다. "
                    + "작업 상태 조회 API에서 완료 결과와 nextPageNo를 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "동기화 작업 시작됨",
                    content = @Content(schema = @Schema(implementation = TourApiRestaurantSyncResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "다른 동기화 요청이 진행 중임",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/sync")
    ResponseEntity<TourApiRestaurantSyncResponse> synchronize(
            @Parameter(description = "공공데이터포털에서 발급받은 TourAPI Decoding 인증키입니다. 서버에 저장하지 않고 이번 요청에만 사용합니다.", required = true)
            @RequestHeader("X-Tour-Api-Key") String tourApiKey,
            @Valid @RequestBody TourApiRestaurantSyncRequest request
    );

    @Operation(
            summary = "제주 음식점 동기화 작업 상태 조회",
            description = "jobId로 백그라운드 동기화 진행 상태와 저장 결과를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "작업 상태 조회 성공",
                    content = @Content(schema = @Schema(implementation = TourApiRestaurantSyncResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "작업을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/sync/{jobId}")
    TourApiRestaurantSyncResponse getSyncJob(@PathVariable UUID jobId);
}
