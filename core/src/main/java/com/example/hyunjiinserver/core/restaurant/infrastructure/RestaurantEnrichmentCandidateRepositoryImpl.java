package com.example.hyunjiinserver.core.restaurant.infrastructure;

import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentCandidate;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentCandidateRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantEnrichmentCandidateRepositoryImpl implements RestaurantEnrichmentCandidateRepository {

    private final RestaurantEnrichmentCandidateJpaRepository jpaRepository;

    @Override
    public List<RestaurantEnrichmentCandidate> saveAll(Collection<RestaurantEnrichmentCandidate> candidates) {
        return jpaRepository.saveAll(candidates);
    }

    @Override
    public List<RestaurantEnrichmentCandidate> findByJobId(UUID jobId) {
        return jpaRepository.findAllByJobIdOrderById(jobId.toString());
    }

    @Override
    public Optional<RestaurantEnrichmentCandidate> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public long countPendingByJobId(UUID jobId) {
        return jpaRepository.countByJobIdAndAcceptedFalse(jobId.toString());
    }
}
