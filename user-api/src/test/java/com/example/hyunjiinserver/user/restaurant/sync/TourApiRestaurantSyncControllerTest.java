package com.example.hyunjiinserver.user.restaurant.sync;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.hyunjiinserver.user.restaurant.sync.dto.TourApiRestaurantSyncRequest;
import com.example.hyunjiinserver.user.restaurant.sync.dto.TourApiRestaurantSyncResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TourApiRestaurantSyncControllerTest {

    @Test
    void returnsAcceptedWithoutWaitingForSyncCompletion() {
        UUID jobId = UUID.randomUUID();
        TourApiRestaurantSyncJobService jobService = new TourApiRestaurantSyncJobService(null, null) {
            @Override
            public TourApiRestaurantSyncJob start(String serviceKey, int pageNo, int maxItems) {
                return TourApiRestaurantSyncJob.running(jobId, pageNo, maxItems);
            }
        };
        TourApiRestaurantSyncController controller = new TourApiRestaurantSyncController(jobService);

        ResponseEntity<TourApiRestaurantSyncResponse> response = controller.synchronize(
                "request-only-key",
                new TourApiRestaurantSyncRequest(2, 100)
        );

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(jobId, response.getBody().jobId());
        assertEquals(TourApiRestaurantSyncJobStatus.RUNNING, response.getBody().status());
        assertEquals(2, response.getBody().pageNo());
        assertEquals(100, response.getBody().maxItems());
    }
}
