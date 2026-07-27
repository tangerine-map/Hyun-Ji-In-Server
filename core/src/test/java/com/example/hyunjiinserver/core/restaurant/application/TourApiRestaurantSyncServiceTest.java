package com.example.hyunjiinserver.core.restaurant.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
        AtomicReference<String> requestedServiceKey = new AtomicReference<>();
        TourApiRestaurantClient client = (serviceKey, maxItems) -> {
            requestedServiceKey.set(serviceKey);
            requestedLimit.set(maxItems);
            return List.of();
        };
        RestaurantImportResult expected = new RestaurantImportResult(0, 0, 0);
        RestaurantImportService importService = new RestaurantImportService(null, null) {
            @Override
            public RestaurantImportResult upsertTourApiRestaurants(List<TourApiRestaurantData> sourceRestaurants) {
                return expected;
            }
        };

        RestaurantImportResult result = new TourApiRestaurantSyncService(client, importService)
                .synchronize("request-service-key", 120);

        assertEquals("request-service-key", requestedServiceKey.get());
        assertEquals(120, requestedLimit.get());
        assertSame(expected, result);
    }

    @Test
    void convertsTourApiFailureToBusinessException() {
        TourApiRestaurantClient client = (serviceKey, maxItems) -> {
            throw new IllegalStateException("upstream failed");
        };
        TourApiRestaurantSyncService service = new TourApiRestaurantSyncService(client, null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.synchronize("request-service-key", 100)
        );

        assertEquals(RestaurantErrorCode.TOUR_API_SYNC_FAILED, exception.getErrorCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getErrorCode().status());
    }
}
