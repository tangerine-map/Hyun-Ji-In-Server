package com.example.hyunjiinserver.core.restaurant.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantEnrichmentCandidateRepository {

    List<RestaurantEnrichmentCandidate> saveAll(Collection<RestaurantEnrichmentCandidate> candidates);

    List<RestaurantEnrichmentCandidate> findByJobId(UUID jobId);

    Optional<RestaurantEnrichmentCandidate> findById(Long id);

    long countPendingByJobId(UUID jobId);
}
