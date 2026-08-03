package com.example.hyunjiinserver.user.restaurant.enrichment;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantEnrichmentPipelineResult;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RestaurantEnrichmentJob {

    private final UUID jobId;
    private final OffsetDateTime createdAt;
    private final Map<Long, RestaurantEnrichmentJobItem> items;

    private RestaurantEnrichmentJob(UUID jobId, OffsetDateTime createdAt, Collection<Long> restaurantIds) {
        this.jobId = jobId;
        this.createdAt = createdAt;
        this.items = new ConcurrentHashMap<>();
        restaurantIds.forEach(id -> items.put(id, RestaurantEnrichmentJobItem.pending(id)));
    }

    static RestaurantEnrichmentJob create(UUID jobId, OffsetDateTime createdAt, Collection<Long> restaurantIds) {
        return new RestaurantEnrichmentJob(jobId, createdAt, restaurantIds);
    }

    public UUID jobId() {
        return jobId;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public List<RestaurantEnrichmentJobItem> items() {
        return items.values().stream()
                .sorted(Comparator.comparing(RestaurantEnrichmentJobItem::restaurantId))
                .toList();
    }

    void markRunning(Long restaurantId) {
        items.computeIfPresent(restaurantId, (id, item) -> item.running());
    }

    void markCompleted(RestaurantEnrichmentPipelineResult result) {
        items.put(result.restaurantId(), RestaurantEnrichmentJobItem.completed(result));
    }

    void markFailed(Long restaurantId, String message) {
        items.computeIfPresent(restaurantId, (id, item) -> item.failed(message));
    }

    public int requestedCount() {
        return items.size();
    }

    public int count(RestaurantEnrichmentItemStatus status) {
        return (int) items.values().stream().filter(item -> item.status() == status).count();
    }

    public int appliedFieldCount() {
        return items.values().stream().mapToInt(RestaurantEnrichmentJobItem::appliedFieldCount).sum();
    }

    public RestaurantEnrichmentExecutionStatus executionStatus() {
        if (count(RestaurantEnrichmentItemStatus.PENDING) > 0
                || count(RestaurantEnrichmentItemStatus.RUNNING) > 0) {
            return RestaurantEnrichmentExecutionStatus.RUNNING;
        }
        int failed = count(RestaurantEnrichmentItemStatus.FAILED);
        if (failed == requestedCount()) {
            return RestaurantEnrichmentExecutionStatus.FAILED;
        }
        if (failed > 0) {
            return RestaurantEnrichmentExecutionStatus.PARTIAL_FAILED;
        }
        return RestaurantEnrichmentExecutionStatus.COMPLETED;
    }

}
