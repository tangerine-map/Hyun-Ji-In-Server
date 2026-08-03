package com.example.hyunjiinserver.core.restaurant.infrastructure;

import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentCandidate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface RestaurantEnrichmentCandidateJpaRepository extends JpaRepository<RestaurantEnrichmentCandidate, Long> {

    List<RestaurantEnrichmentCandidate> findAllByJobIdOrderById(String jobId);

    long countByJobIdAndAcceptedFalse(String jobId);
}
