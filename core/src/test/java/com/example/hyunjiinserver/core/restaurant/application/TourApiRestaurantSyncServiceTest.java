package com.example.hyunjiinserver.core.restaurant.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class TourApiRestaurantSyncServiceTest {

    @Test
    void passesRequestedLimitAndReturnsImportResult() {
        AtomicInteger requestedLimit = new AtomicInteger();
        TourApiRestaurantClient client = maxItems -> {
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

        RestaurantImportResult result = new TourApiRestaurantSyncService(client, importService).synchronize(120);

        assertEquals(120, requestedLimit.get());
        assertSame(expected, result);
    }

    @Test
    void convertsTourApiFailureToBusinessException() {
        TourApiRestaurantClient client = maxItems -> {
            throw new IllegalStateException("upstream failed");
        };
        TourApiRestaurantSyncService service = new TourApiRestaurantSyncService(client, null);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.synchronize(100));

        assertEquals(RestaurantErrorCode.TOUR_API_SYNC_FAILED, exception.getErrorCode());
    }
}
