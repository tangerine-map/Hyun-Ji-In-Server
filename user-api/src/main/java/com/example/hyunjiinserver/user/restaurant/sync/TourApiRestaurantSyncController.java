package com.example.hyunjiinserver.user.restaurant.sync;

import com.example.hyunjiinserver.user.restaurant.sync.dto.TourApiRestaurantSyncRequest;
import com.example.hyunjiinserver.user.restaurant.sync.dto.TourApiRestaurantSyncResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TourApiRestaurantSyncController implements TourApiRestaurantSyncApi {

    private final TourApiRestaurantSyncJobService syncJobService;

    @Override
    public ResponseEntity<TourApiRestaurantSyncResponse> synchronize(
            String tourApiKey,
            TourApiRestaurantSyncRequest request
    ) {
        int pageNo = request.resolvedPageNo();
        log.info("TourAPI restaurant sync request received. pageNo={}, maxItems={}", pageNo, request.maxItems());
        TourApiRestaurantSyncJob job = syncJobService.start(
                tourApiKey,
                pageNo,
                request.maxItems()
        );
        return ResponseEntity.accepted().body(TourApiRestaurantSyncResponse.from(job));
    }

    @Override
    public TourApiRestaurantSyncResponse getSyncJob(UUID jobId) {
        return TourApiRestaurantSyncResponse.from(syncJobService.get(jobId));
    }
}
