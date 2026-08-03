package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentCandidate;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentCandidateRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantEnrichmentCandidateCommandService {

    private final RestaurantEnrichmentCandidateRepository candidateRepository;
    private final Clock clock;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int saveAll(UUID jobId, Long restaurantId, List<ExtractedRestaurantCandidate> extracted) {
        OffsetDateTime createdAt = OffsetDateTime.now(clock);
        List<RestaurantEnrichmentCandidate> candidates = extracted.stream()
                .map(item -> RestaurantEnrichmentCandidate.create(
                        jobId,
                        restaurantId,
                        item.field(),
                        item.valueText(),
                        item.valueNumber(),
                        item.representative(),
                        item.sourceUrl(),
                        item.evidence(),
                        item.confidence(),
                        createdAt
                ))
                .toList();
        candidateRepository.saveAll(candidates);
        return candidates.size();
    }
}
