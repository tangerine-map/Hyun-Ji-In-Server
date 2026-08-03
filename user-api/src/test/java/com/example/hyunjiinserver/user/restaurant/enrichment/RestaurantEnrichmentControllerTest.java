package com.example.hyunjiinserver.user.restaurant.enrichment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentJobRequest;
import com.example.hyunjiinserver.user.restaurant.enrichment.dto.RestaurantEnrichmentJobResponse;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class RestaurantEnrichmentControllerTest {

    @Test
    void returnsAcceptedWithoutWaitingForEnrichmentCompletion() {
        UUID jobId = UUID.randomUUID();
        RestaurantEnrichmentJobService jobService = new RestaurantEnrichmentJobService(null, null, null) {
            @Override
            public RestaurantEnrichmentJob start(List<Long> restaurantIds) {
                return RestaurantEnrichmentJob.create(
                        jobId,
                        OffsetDateTime.parse("2026-08-01T12:00:00+09:00"),
                        restaurantIds
                );
            }
        };
        RestaurantEnrichmentController controller = new RestaurantEnrichmentController(jobService);

        ResponseEntity<RestaurantEnrichmentJobResponse> response = controller.start(
                new RestaurantEnrichmentJobRequest(List.of(2L, 3L))
        );

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(jobId, response.getBody().jobId());
        assertEquals(2, response.getBody().requestedCount());
    }
}
