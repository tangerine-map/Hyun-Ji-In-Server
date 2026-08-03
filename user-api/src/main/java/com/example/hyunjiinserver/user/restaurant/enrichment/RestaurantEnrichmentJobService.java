package com.example.hyunjiinserver.user.restaurant.enrichment;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.application.RestaurantEnrichmentPipelineResult;
import com.example.hyunjiinserver.core.restaurant.application.RestaurantEnrichmentPipelineService;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RestaurantEnrichmentJobService {

    private final RestaurantEnrichmentPipelineService pipelineService;
    private final Executor executor;
    private final Clock clock;
    private final Map<UUID, RestaurantEnrichmentJob> jobs = new ConcurrentHashMap<>();

    public RestaurantEnrichmentJobService(
            RestaurantEnrichmentPipelineService pipelineService,
            @Qualifier("restaurantEnrichmentExecutor") Executor executor,
            Clock clock
    ) {
        this.pipelineService = pipelineService;
        this.executor = executor;
        this.clock = clock;
    }

    public RestaurantEnrichmentJob start(List<Long> restaurantIds) {
        List<Long> uniqueIds = List.copyOf(new LinkedHashSet<>(restaurantIds));
        UUID jobId = UUID.randomUUID();
        RestaurantEnrichmentJob job = RestaurantEnrichmentJob.create(
                jobId,
                OffsetDateTime.now(clock),
                uniqueIds
        );
        jobs.put(jobId, job);

        for (Long restaurantId : uniqueIds) {
            try {
                executor.execute(() -> executeOne(job, restaurantId));
            } catch (RuntimeException exception) {
                job.markFailed(restaurantId, "식당 정보 보강 작업을 실행 대기열에 등록하지 못했습니다.");
                log.error("Restaurant enrichment task rejected. jobId={}, restaurantId={}, causeType={}",
                        jobId, restaurantId, exception.getClass().getSimpleName());
            }
        }
        return job;
    }

    public RestaurantEnrichmentJob get(UUID jobId) {
        RestaurantEnrichmentJob job = jobs.get(jobId);
        if (job == null) {
            throw new BusinessException(RestaurantErrorCode.ENRICHMENT_JOB_NOT_FOUND);
        }
        return job;
    }

    private void executeOne(
            RestaurantEnrichmentJob job,
            Long restaurantId
    ) {
        job.markRunning(restaurantId);
        try {
            RestaurantEnrichmentPipelineResult result = pipelineService.enrichOne(
                    job.jobId(),
                    restaurantId
            );
            job.markCompleted(result);
        } catch (RuntimeException exception) {
            String message = exception instanceof BusinessException businessException
                    ? businessException.getErrorCode().message()
                    : "식당 정보 보강 중 오류가 발생했습니다.";
            job.markFailed(restaurantId, message);
            log.error("Restaurant enrichment failed. jobId={}, restaurantId={}, causeType={}",
                    job.jobId(), restaurantId, exception.getClass().getSimpleName());
        }
    }
}
