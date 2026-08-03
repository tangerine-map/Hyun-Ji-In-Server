package com.example.hyunjiinserver.user.restaurant.enrichment;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantEnrichmentApplyResult;
import com.example.hyunjiinserver.core.restaurant.application.RestaurantEnrichmentApplyService;
import com.example.hyunjiinserver.core.restaurant.application.RestaurantEnrichmentCandidateQueryService;
import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentApplyRequest;
import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentApplyResponse;
import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentCandidateResponse;
import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentCandidatesResponse;
import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentJobRequest;
import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentJobResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestaurantEnrichmentController implements RestaurantEnrichmentApi {

    private final RestaurantEnrichmentJobService jobService;
    private final RestaurantEnrichmentCandidateQueryService candidateQueryService;
    private final RestaurantEnrichmentApplyService applyService;

    @Override
    public ResponseEntity<RestaurantEnrichmentJobResponse> start(
            RestaurantEnrichmentJobRequest request
    ) {
        log.info("Restaurant enrichment request received. restaurantCount={}", request.restaurantIds().size());
        RestaurantEnrichmentJob job = jobService.start(request.restaurantIds());
        return ResponseEntity.accepted().body(RestaurantEnrichmentJobResponse.from(job));
    }

    @Override
    public RestaurantEnrichmentJobResponse getJob(UUID jobId) {
        return RestaurantEnrichmentJobResponse.from(jobService.get(jobId));
    }

    @Override
    public RestaurantEnrichmentCandidatesResponse getCandidates(UUID jobId) {
        jobService.get(jobId);
        List<RestaurantEnrichmentCandidateResponse> candidates = candidateQueryService.findByJobId(jobId).stream()
                .map(RestaurantEnrichmentCandidateResponse::from)
                .toList();
        return new RestaurantEnrichmentCandidatesResponse(jobId, candidates.size(), candidates);
    }

    @Override
    public RestaurantEnrichmentApplyResponse apply(UUID jobId, RestaurantEnrichmentApplyRequest request) {
        jobService.ensureFinished(jobId);
        RestaurantEnrichmentApplyResult result = applyService.apply(jobId, request.candidateIds());
        if (!candidateQueryService.hasPendingCandidates(jobId)) {
            jobService.completeReview(jobId);
        }
        return RestaurantEnrichmentApplyResponse.from(jobId, result, jobService.get(jobId).reviewStatus());
    }
}
