package com.example.hyunjiinserver.user.restaurant.sync;

import com.example.hyunjiinserver.core.restaurant.application.TourApiRestaurantSyncService;
import com.example.hyunjiinserver.core.restaurant.application.TourApiRestaurantSyncResult;
import com.example.hyunjiinserver.user.restaurant.sync.dto.TourApiRestaurantSyncRequest;
import com.example.hyunjiinserver.user.restaurant.sync.dto.TourApiRestaurantSyncResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TourApiRestaurantSyncController implements TourApiRestaurantSyncApi {

    private final TourApiRestaurantSyncService tourApiRestaurantSyncService;

    @Override
    public TourApiRestaurantSyncResponse synchronize(
            String tourApiKey,
            TourApiRestaurantSyncRequest request
    ) {
        int pageNo = request.resolvedPageNo();
        log.info("TourAPI restaurant sync request received. pageNo={}, maxItems={}", pageNo, request.maxItems());
        TourApiRestaurantSyncResult result = tourApiRestaurantSyncService.synchronize(
                tourApiKey,
                pageNo,
                request.maxItems()
        );
        return TourApiRestaurantSyncResponse.from(result);
    }
}
