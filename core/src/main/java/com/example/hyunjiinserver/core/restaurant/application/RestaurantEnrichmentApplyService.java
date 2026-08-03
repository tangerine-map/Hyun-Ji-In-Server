package com.example.hyunjiinserver.core.restaurant.application;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantEnrichmentApplyService {

    private final RestaurantEnrichmentApplyTransactionService transactionService;

    public RestaurantEnrichmentApplyResult apply(UUID jobId, List<Long> candidateIds) {
        int appliedCount = 0;
        for (Long candidateId : candidateIds.stream().distinct().toList()) {
            if (transactionService.applyOne(jobId, candidateId).applied()) {
                appliedCount++;
            }
        }
        int requestedCount = (int) candidateIds.stream().distinct().count();
        return new RestaurantEnrichmentApplyResult(
                requestedCount,
                appliedCount,
                requestedCount - appliedCount
        );
    }
}
