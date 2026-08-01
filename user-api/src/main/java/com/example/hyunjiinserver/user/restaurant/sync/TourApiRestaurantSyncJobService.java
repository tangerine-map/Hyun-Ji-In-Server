package com.example.hyunjiinserver.user.restaurant.sync;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.application.TourApiRestaurantSyncResult;
import com.example.hyunjiinserver.core.restaurant.application.TourApiRestaurantSyncService;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TourApiRestaurantSyncJobService {

    private final TourApiRestaurantSyncService syncService;
    private final Executor syncExecutor;
    private final AtomicBoolean syncInProgress = new AtomicBoolean(false);
    private final Map<UUID, TourApiRestaurantSyncJob> jobs = new ConcurrentHashMap<>();

    public TourApiRestaurantSyncJobService(
            TourApiRestaurantSyncService syncService,
            @Qualifier("tourApiSyncExecutor") Executor syncExecutor
    ) {
        this.syncService = syncService;
        this.syncExecutor = syncExecutor;
    }

    public TourApiRestaurantSyncJob start(String serviceKey, int pageNo, int maxItems) {
        if (!syncInProgress.compareAndSet(false, true)) {
            throw new BusinessException(RestaurantErrorCode.TOUR_API_SYNC_IN_PROGRESS);
        }

        UUID jobId = UUID.randomUUID();
        TourApiRestaurantSyncJob job = TourApiRestaurantSyncJob.running(jobId, pageNo, maxItems);
        jobs.put(jobId, job);
        try {
            syncExecutor.execute(() -> execute(job, serviceKey));
        } catch (RuntimeException exception) {
            jobs.remove(jobId);
            syncInProgress.set(false);
            throw exception;
        }
        return job;
    }

    public TourApiRestaurantSyncJob get(UUID jobId) {
        TourApiRestaurantSyncJob job = jobs.get(jobId);
        if (job == null) {
            throw new BusinessException(RestaurantErrorCode.TOUR_API_SYNC_JOB_NOT_FOUND);
        }
        return job;
    }

    private void execute(TourApiRestaurantSyncJob job, String serviceKey) {
        try {
            TourApiRestaurantSyncResult result = syncService.synchronize(
                    serviceKey,
                    job.pageNo(),
                    job.maxItems()
            );
            jobs.put(job.jobId(), job.completed(result));
        } catch (RuntimeException exception) {
            String message = exception instanceof BusinessException businessException
                    ? businessException.getErrorCode().message()
                    : "TourAPI 음식점 동기화 실행 중 오류가 발생했습니다.";
            jobs.put(job.jobId(), job.failed(message));
            log.error(
                    "TourAPI restaurant sync job failed. jobId={}, pageNo={}, causeType={}",
                    job.jobId(),
                    job.pageNo(),
                    exception.getClass().getSimpleName()
            );
        } finally {
            syncInProgress.set(false);
        }
    }
}
