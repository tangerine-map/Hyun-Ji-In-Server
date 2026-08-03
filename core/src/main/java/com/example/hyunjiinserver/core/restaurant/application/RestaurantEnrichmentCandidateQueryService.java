package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentCandidateRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantEnrichmentCandidateQueryService {

    private final RestaurantEnrichmentCandidateRepository candidateRepository;

    @Transactional(readOnly = true)
    public List<RestaurantEnrichmentCandidateResult> findByJobId(UUID jobId) {
        return candidateRepository.findByJobId(jobId).stream()
                .map(RestaurantEnrichmentCandidateResult::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean hasPendingCandidates(UUID jobId) {
        return candidateRepository.countPendingByJobId(jobId) > 0;
    }
}
