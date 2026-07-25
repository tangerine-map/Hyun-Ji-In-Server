package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourApiRestaurantSyncService {

    private final TourApiRestaurantClient tourApiRestaurantClient;
    private final RestaurantImportService restaurantImportService;

    public RestaurantImportResult synchronize(int maxItems) {
        List<TourApiRestaurantData> restaurants;
        try {
            restaurants = tourApiRestaurantClient.fetchJejuRestaurants(maxItems);
        } catch (RuntimeException exception) {
            throw new BusinessException(RestaurantErrorCode.TOUR_API_SYNC_FAILED, exception);
        }
        return restaurantImportService.upsertTourApiRestaurants(restaurants);
    }
}
