package com.example.hyunjiinserver.user.restaurant.enrichment;

import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentJobRequest;
import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentJobResponse;
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
}
