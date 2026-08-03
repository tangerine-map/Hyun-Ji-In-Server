package com.example.hyunjiinserver.user.restaurant.enrichment;

import com.example.hyunjiinserver.user.global.error.dto.ErrorResponse;
import com.example.hyunjiinserver.user.global.error.dto.ValidationErrorResponse;
import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentJobRequest;
import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentJobResponse;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Internal Restaurant Enrichment", description = "웹 검색·크롤링·AI 기반 식당 누락 정보 보강 API")
@RequestMapping("/api/internal/restaurant-enrichment-jobs")
public interface RestaurantEnrichmentApi {

    @Operation(
            summary = "식당 정보 보강 작업 시작",
            description = "식당별 누락 필드를 자동으로 판단한 뒤 검색·크롤링·로컬 AI 추출을 실행하고, "
                    + "저장 시점에도 비어 있는 필드를 식당별 독립 트랜잭션으로 즉시 반영합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "보강 작업 시작됨",
                    content = @Content(schema = @Schema(implementation = RestaurantEnrichmentJobResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<RestaurantEnrichmentJobResponse> start(
            @Valid @RequestBody RestaurantEnrichmentJobRequest request
    );

    @Operation(summary = "식당 정보 보강 작업 상태 조회", description = "식당별로 실제 반영된 필드 수와 실패 여부를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작업 상태 조회 성공",
                    content = @Content(schema = @Schema(implementation = RestaurantEnrichmentJobResponse.class))),
            @ApiResponse(responseCode = "404", description = "작업을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{jobId}")
    RestaurantEnrichmentJobResponse getJob(@PathVariable UUID jobId);
}
