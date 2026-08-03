package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import com.example.hyunjiinserver.core.restaurant.infrastructure.RestaurantEnrichmentClientException;
import com.example.hyunjiinserver.core.restaurant.infrastructure.RestaurantEnrichmentProperties;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantEnrichmentPipelineService {

    private final RestaurantEnrichmentPlanningService planningService;
    private final RestaurantInformationEnrichmentClient enrichmentClient;
    private final RestaurantEnrichmentAutoApplyService autoApplyService;
    private final RestaurantEnrichmentProperties properties;

    public RestaurantEnrichmentPipelineResult enrichOne(
            UUID jobId,
            Long restaurantId
    ) {
        try {
            return execute(jobId, restaurantId);
        } catch (RestaurantEnrichmentClientException exception) {
            throw new BusinessException(RestaurantErrorCode.ENRICHMENT_PROCESSING_FAILED, exception);
        }
    }

    private RestaurantEnrichmentPipelineResult execute(
            UUID jobId,
            Long restaurantId
    ) {
        RestaurantEnrichmentPlan plan = planningService.plan(restaurantId);
        if (plan.skipped()) {
            log.info("Restaurant enrichment skipped. jobId={}, restaurantId={}, name={}",
                    jobId, restaurantId, plan.restaurantName());
            return RestaurantEnrichmentPipelineResult.skipped(plan);
        }

        log.info("Restaurant enrichment started. jobId={}, restaurantId={}, name={}, missingFields={}",
                jobId, restaurantId, plan.restaurantName(), plan.missingFields());
        int maxSources = Math.max(1, Math.min(properties.getMaxSourcesPerRestaurant(), 20));
        RestaurantInformationEnrichmentResult enriched = enrichmentClient.enrich(plan, maxSources);
        int appliedFieldCount = autoApplyService.apply(restaurantId, enriched.candidates());
        log.info(
                "Restaurant enrichment completed. jobId={}, restaurantId={}, name={}, searchedSources={}, fetchedSources={}, appliedFields={}",
                jobId,
                restaurantId,
                plan.restaurantName(),
                enriched.sourceCount(),
                enriched.fetchedCount(),
                appliedFieldCount
        );
        return new RestaurantEnrichmentPipelineResult(
                restaurantId,
                plan.restaurantName(),
                false,
                plan.missingFields(),
                enriched.sourceCount(),
                enriched.fetchedCount(),
                appliedFieldCount
        );
    }
}
