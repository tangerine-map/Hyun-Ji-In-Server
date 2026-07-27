package com.example.hyunjiinserver.user.restaurant.sync;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantImportResult;
import com.example.hyunjiinserver.core.restaurant.application.TourApiRestaurantSyncService;
import com.example.hyunjiinserver.user.restaurant.sync.dto.TourApiRestaurantSyncRequest;
import com.example.hyunjiinserver.user.restaurant.sync.dto.TourApiRestaurantSyncResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TourApiRestaurantSyncController implements TourApiRestaurantSyncApi {

    private final TourApiRestaurantSyncService tourApiRestaurantSyncService;

    @Override
    public TourApiRestaurantSyncResponse synchronize(
            String tourApiKey,
            TourApiRestaurantSyncRequest request
    ) {
        RestaurantImportResult result = tourApiRestaurantSyncService.synchronize(tourApiKey, request.maxItems());
        return TourApiRestaurantSyncResponse.from(result);
    }
}
