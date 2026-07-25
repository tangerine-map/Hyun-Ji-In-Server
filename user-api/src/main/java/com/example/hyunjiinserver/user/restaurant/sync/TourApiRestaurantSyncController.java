package com.example.hyunjiinserver.user.restaurant.sync;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantImportResult;
import com.example.hyunjiinserver.core.restaurant.application.TourApiRestaurantSyncService;
import com.example.hyunjiinserver.user.global.security.InternalApiKeyVerifier;
import com.example.hyunjiinserver.user.restaurant.sync.dto.TourApiRestaurantSyncRequest;
import com.example.hyunjiinserver.user.restaurant.sync.dto.TourApiRestaurantSyncResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TourApiRestaurantSyncController implements TourApiRestaurantSyncApi {

    private final TourApiRestaurantSyncService tourApiRestaurantSyncService;
    private final InternalApiKeyVerifier internalApiKeyVerifier;

    @Override
    public TourApiRestaurantSyncResponse synchronize(
            String syncApiKey,
            TourApiRestaurantSyncRequest request
    ) {
        internalApiKeyVerifier.verify(syncApiKey);
        RestaurantImportResult result = tourApiRestaurantSyncService.synchronize(request.maxItems());
        return TourApiRestaurantSyncResponse.from(result);
    }
}
