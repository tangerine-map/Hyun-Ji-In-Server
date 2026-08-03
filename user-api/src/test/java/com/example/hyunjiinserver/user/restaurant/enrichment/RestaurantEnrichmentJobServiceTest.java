package com.example.hyunjiinserver.user.restaurant.enrichment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantEnrichmentPipelineResult;
import com.example.hyunjiinserver.core.restaurant.application.RestaurantEnrichmentPipelineService;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentField;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import org.junit.jupiter.api.Test;

class RestaurantEnrichmentJobServiceTest {

    @Test
    void processesRestaurantsIndependentlyInBackground() {
        CapturingExecutor executor = new CapturingExecutor();
        RestaurantEnrichmentPipelineService pipeline = new RestaurantEnrichmentPipelineService(
                null, null, null, null
        ) {
            @Override
            public RestaurantEnrichmentPipelineResult enrichOne(
                    UUID jobId,
                    Long restaurantId
            ) {
                if (restaurantId == 2L) {
                    return new RestaurantEnrichmentPipelineResult(
                            2L,
                            "가는곶 세화",
                            false,
                            Set.of(RestaurantEnrichmentField.PHONE_NUMBER),
                            5,
                            4,
                            1
                    );
                }
                return new RestaurantEnrichmentPipelineResult(
                        restaurantId,
                        "정보가 완성된 식당",
                        true,
                        Set.of(),
                        0,
                        0,
                        0
                );
            }
        };
        Clock clock = Clock.fixed(Instant.parse("2026-08-01T03:00:00Z"), ZoneId.of("Asia/Seoul"));
        RestaurantEnrichmentJobService service = new RestaurantEnrichmentJobService(pipeline, executor, clock);

        RestaurantEnrichmentJob job = service.start(List.of(2L, 3L, 2L));

        assertEquals(2, job.requestedCount());
        assertEquals(RestaurantEnrichmentExecutionStatus.RUNNING, job.executionStatus());
        executor.runAll();

        assertEquals(RestaurantEnrichmentExecutionStatus.COMPLETED, job.executionStatus());
        assertEquals(RestaurantEnrichmentReviewStatus.PENDING, job.reviewStatus());
        assertEquals(1, job.count(RestaurantEnrichmentItemStatus.COMPLETED));
        assertEquals(1, job.count(RestaurantEnrichmentItemStatus.SKIPPED));
        assertEquals(1, job.candidateCount());
    }

    private static class CapturingExecutor implements Executor {

        private final List<Runnable> tasks = new ArrayList<>();

        @Override
        public void execute(Runnable command) {
            tasks.add(command);
        }

        void runAll() {
            tasks.forEach(Runnable::run);
        }
    }
}
