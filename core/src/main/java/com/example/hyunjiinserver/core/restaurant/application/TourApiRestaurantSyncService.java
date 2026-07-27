package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourApiRestaurantSyncService {

    private final TourApiRestaurantClient tourApiRestaurantClient;
    private final RestaurantImportService restaurantImportService;
    private final AtomicBoolean syncInProgress = new AtomicBoolean(false);

    public TourApiRestaurantSyncResult synchronize(String serviceKey, int pageNo, int maxItems) {
        if (!syncInProgress.compareAndSet(false, true)) {
            throw new BusinessException(RestaurantErrorCode.TOUR_API_SYNC_IN_PROGRESS);
        }

        try {
            return synchronizePage(serviceKey, pageNo, maxItems);
        } finally {
            syncInProgress.set(false);
        }
    }

    private TourApiRestaurantSyncResult synchronizePage(String serviceKey, int pageNo, int maxItems) {
        log.info("TourAPI restaurant sync started. pageNo={}, maxItems={}", pageNo, maxItems);

        TourApiRestaurantPage page;
        try {
            page = tourApiRestaurantClient.fetchJejuRestaurants(serviceKey, pageNo, maxItems);
        } catch (RuntimeException exception) {
            log.error(
                    "TourAPI restaurant fetch failed. pageNo={}, maxItems={}, causeType={}",
                    pageNo,
                    maxItems,
                    exception.getClass().getSimpleName()
            );
            throw new BusinessException(RestaurantErrorCode.TOUR_API_SYNC_FAILED, exception);
        }
        log.info(
                "TourAPI restaurant fetch completed. pageNo={}, fetchedCount={}, nextPageNo={}",
                page.pageNo(),
                page.restaurants().size(),
                page.nextPageNo()
        );

        try {
            RestaurantImportResult importResult = restaurantImportService.upsertTourApiRestaurants(page.restaurants());
            log.info(
                    "TourAPI restaurant sync completed. pageNo={}, fetchedCount={}, createdCount={}, updatedCount={}",
                    page.pageNo(),
                    importResult.fetchedCount(),
                    importResult.createdCount(),
                    importResult.updatedCount()
            );
            return TourApiRestaurantSyncResult.of(page, importResult);
        } catch (RuntimeException exception) {
            log.error(
                    "TourAPI restaurant persistence failed. pageNo={}, fetchedCount={}, causeType={}",
                    page.pageNo(),
                    page.restaurants().size(),
                    exception.getClass().getSimpleName()
            );
            throw exception;
        }
    }
}
