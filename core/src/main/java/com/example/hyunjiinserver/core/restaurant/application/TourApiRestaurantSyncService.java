package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourApiRestaurantSyncService {

    private final TourApiRestaurantClient tourApiRestaurantClient;
    private final RestaurantImportService restaurantImportService;

    public RestaurantImportResult synchronize(String serviceKey, int maxItems) {
        log.info("TourAPI restaurant sync started. maxItems={}", maxItems);

        List<TourApiRestaurantData> restaurants;
        try {
            restaurants = tourApiRestaurantClient.fetchJejuRestaurants(serviceKey, maxItems);
        } catch (RuntimeException exception) {
            log.error(
                    "TourAPI restaurant fetch failed. maxItems={}, causeType={}",
                    maxItems,
                    exception.getClass().getSimpleName()
            );
            throw new BusinessException(RestaurantErrorCode.TOUR_API_SYNC_FAILED, exception);
        }
        log.info("TourAPI restaurant fetch completed. fetchedCount={}", restaurants.size());

        try {
            RestaurantImportResult result = restaurantImportService.upsertTourApiRestaurants(restaurants);
            log.info(
                    "TourAPI restaurant sync completed. fetchedCount={}, createdCount={}, updatedCount={}",
                    result.fetchedCount(),
                    result.createdCount(),
                    result.updatedCount()
            );
            return result;
        } catch (RuntimeException exception) {
            log.error(
                    "TourAPI restaurant persistence failed. fetchedCount={}, causeType={}",
                    restaurants.size(),
                    exception.getClass().getSimpleName()
            );
            throw exception;
        }
    }
}
