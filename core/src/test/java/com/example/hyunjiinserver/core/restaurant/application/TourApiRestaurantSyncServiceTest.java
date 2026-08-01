package com.example.hyunjiinserver.core.restaurant.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class TourApiRestaurantSyncServiceTest {

    @Test
    void passesRequestedLimitAndReturnsImportResult() {
        AtomicInteger requestedLimit = new AtomicInteger();
        AtomicInteger requestedPage = new AtomicInteger();
        AtomicReference<String> requestedServiceKey = new AtomicReference<>();
        TourApiRestaurantClient client = (serviceKey, pageNo, maxItems) -> {
            requestedServiceKey.set(serviceKey);
            requestedPage.set(pageNo);
            requestedLimit.set(maxItems);
            return new TourApiRestaurantPage(List.of(), pageNo, pageNo + 1);
        };
        RestaurantImportResult expected = new RestaurantImportResult(0, 0, 0, 0);
        RestaurantImportService importService = new RestaurantImportService(null, null) {
            @Override
            public RestaurantImportResult upsertTourApiRestaurants(List<TourApiRestaurantData> sourceRestaurants) {
                return expected;
            }
        };

        TourApiRestaurantSyncResult result = new TourApiRestaurantSyncService(client, importService)
                .synchronize("request-service-key", 3, 100);

        assertEquals("request-service-key", requestedServiceKey.get());
        assertEquals(3, requestedPage.get());
        assertEquals(100, requestedLimit.get());
        assertEquals(3, result.pageNo());
        assertEquals(4, result.nextPageNo());
        assertEquals(expected.fetchedCount(), result.fetchedCount());
        assertEquals(expected.createdCount(), result.createdCount());
        assertEquals(expected.updatedCount(), result.updatedCount());
        assertEquals(expected.failedCount(), result.failedCount());
    }

    @Test
    void convertsTourApiFailureToBusinessException() {
        TourApiRestaurantClient client = (serviceKey, pageNo, maxItems) -> {
            throw new IllegalStateException("upstream failed");
        };
        TourApiRestaurantSyncService service = new TourApiRestaurantSyncService(client, null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.synchronize("request-service-key", 1, 100)
        );

        assertEquals(RestaurantErrorCode.TOUR_API_SYNC_FAILED, exception.getErrorCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getErrorCode().status());
    }

    @Test
    void rejectsAnotherSyncWhileOneIsInProgress() {
        AtomicReference<TourApiRestaurantSyncService> serviceReference = new AtomicReference<>();
        AtomicReference<BusinessException> concurrentException = new AtomicReference<>();
        TourApiRestaurantClient client = (serviceKey, pageNo, maxItems) -> {
            concurrentException.set(assertThrows(
                    BusinessException.class,
                    () -> serviceReference.get().synchronize("another-key", 2, 100)
            ));
            return new TourApiRestaurantPage(List.of(), pageNo, null);
        };
        RestaurantImportService importService = new RestaurantImportService(null, null) {
            @Override
            public RestaurantImportResult upsertTourApiRestaurants(List<TourApiRestaurantData> sourceRestaurants) {
                return new RestaurantImportResult(0, 0, 0, 0);
            }
        };
        TourApiRestaurantSyncService service = new TourApiRestaurantSyncService(client, importService);
        serviceReference.set(service);

        service.synchronize("request-key", 1, 100);

        assertEquals(
                RestaurantErrorCode.TOUR_API_SYNC_IN_PROGRESS,
                concurrentException.get().getErrorCode()
        );
        assertEquals(HttpStatus.CONFLICT, concurrentException.get().getErrorCode().status());
    }

}
