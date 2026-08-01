package com.example.hyunjiinserver.user.restaurant.sync;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.application.TourApiRestaurantSyncResult;
import com.example.hyunjiinserver.core.restaurant.application.TourApiRestaurantSyncService;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import java.util.concurrent.Executor;
import org.junit.jupiter.api.Test;

class TourApiRestaurantSyncJobServiceTest {

    @Test
    void startsInBackgroundAndExposesCompletedResult() {
        CapturingExecutor executor = new CapturingExecutor();
        TourApiRestaurantSyncService syncService = new TourApiRestaurantSyncService(null, null) {
            @Override
            public TourApiRestaurantSyncResult synchronize(String serviceKey, int pageNo, int maxItems) {
                return new TourApiRestaurantSyncResult(pageNo, pageNo + 1, 100, 80, 19, 1);
            }
        };
        TourApiRestaurantSyncJobService jobService = new TourApiRestaurantSyncJobService(syncService, executor);

        TourApiRestaurantSyncJob started = jobService.start("request-only-key", 3, 100);

        assertEquals(TourApiRestaurantSyncJobStatus.RUNNING, started.status());
        assertNull(started.nextPageNo());
        BusinessException conflict = assertThrows(
                BusinessException.class,
                () -> jobService.start("another-key", 4, 100)
        );
        assertEquals(RestaurantErrorCode.TOUR_API_SYNC_IN_PROGRESS, conflict.getErrorCode());

        executor.runCapturedTask();
        TourApiRestaurantSyncJob completed = jobService.get(started.jobId());

        assertEquals(TourApiRestaurantSyncJobStatus.COMPLETED, completed.status());
        assertEquals(4, completed.nextPageNo());
        assertEquals(100, completed.fetchedCount());
        assertEquals(80, completed.createdCount());
        assertEquals(19, completed.updatedCount());
        assertEquals(1, completed.failedCount());
    }

    @Test
    void exposesFailedStatusWithoutServiceKey() {
        CapturingExecutor executor = new CapturingExecutor();
        TourApiRestaurantSyncService syncService = new TourApiRestaurantSyncService(null, null) {
            @Override
            public TourApiRestaurantSyncResult synchronize(String serviceKey, int pageNo, int maxItems) {
                throw new BusinessException(RestaurantErrorCode.TOUR_API_SYNC_FAILED);
            }
        };
        TourApiRestaurantSyncJobService jobService = new TourApiRestaurantSyncJobService(syncService, executor);

        TourApiRestaurantSyncJob started = jobService.start("secret-must-not-be-stored", 1, 10);
        executor.runCapturedTask();
        TourApiRestaurantSyncJob failed = jobService.get(started.jobId());

        assertEquals(TourApiRestaurantSyncJobStatus.FAILED, failed.status());
        assertEquals(RestaurantErrorCode.TOUR_API_SYNC_FAILED.message(), failed.errorMessage());
        assertEquals(10, failed.maxItems());
    }

    private static class CapturingExecutor implements Executor {

        private Runnable task;

        @Override
        public void execute(Runnable command) {
            task = command;
        }

        void runCapturedTask() {
            task.run();
        }
    }
}
