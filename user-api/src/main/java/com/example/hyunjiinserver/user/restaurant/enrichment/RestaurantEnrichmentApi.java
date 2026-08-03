package com.example.hyunjiinserver.user.restaurant.enrichment;

import com.example.hyunjiinserver.user.global.error.dto.ErrorResponse;
import com.example.hyunjiinserver.user.global.error.dto.ValidationErrorResponse;
import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentApplyRequest;
import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentApplyResponse;
import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentCandidatesResponse;
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
            description = "식당별 누락 필드를 자동으로 판단한 뒤 제한된 병렬 수로 검색·크롤링·로컬 AI 추출을 실행합니다."
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

    @Operation(summary = "식당 정보 보강 작업 상태 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작업 상태 조회 성공",
                    content = @Content(schema = @Schema(implementation = RestaurantEnrichmentJobResponse.class))),
            @ApiResponse(responseCode = "404", description = "작업을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{jobId}")
    RestaurantEnrichmentJobResponse getJob(@PathVariable UUID jobId);

    @Operation(summary = "로컬 AI가 추출한 보강 후보 조회", description = "각 값의 원본 URL, 근거 문장과 신뢰도를 함께 반환합니다.")
    @GetMapping("/{jobId}/candidates")
    RestaurantEnrichmentCandidatesResponse getCandidates(@PathVariable UUID jobId);

    @Operation(summary = "선택한 보강 후보 반영", description = "기존 값은 덮어쓰지 않고 아직 누락된 필드에만 반영합니다.")
    @PostMapping("/{jobId}/apply")
    RestaurantEnrichmentApplyResponse apply(
            @PathVariable UUID jobId,
            @Valid @RequestBody RestaurantEnrichmentApplyRequest request
    );
}
