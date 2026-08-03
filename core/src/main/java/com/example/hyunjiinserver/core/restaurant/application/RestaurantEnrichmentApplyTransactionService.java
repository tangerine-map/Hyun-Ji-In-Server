package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentCandidate;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentCandidateRepository;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantRepository;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantStatus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantEnrichmentApplyTransactionService {

    private final RestaurantEnrichmentCandidateRepository candidateRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RestaurantEnrichmentApplyItemResult applyOne(UUID jobId, Long candidateId) {
        RestaurantEnrichmentCandidate candidate = candidateRepository.findById(candidateId)
                .filter(item -> item.jobIdAsUuid().equals(jobId))
                .orElseThrow(() -> new BusinessException(RestaurantErrorCode.ENRICHMENT_CANDIDATE_NOT_FOUND));
        Restaurant restaurant = restaurantRepository.findById(candidate.getRestaurantId())
                .orElseThrow(() -> new BusinessException(RestaurantErrorCode.RESTAURANT_NOT_FOUND));

        boolean applied = switch (candidate.getField()) {
            case PHONE_NUMBER -> restaurant.applyPhoneNumberIfMissing(truncate(candidate.getValueText(), 30));
            case OPENING_HOURS -> restaurant.applyOpeningHoursIfMissing(truncate(candidate.getValueText(), 100));
            case SUMMARY -> restaurant.applySummaryIfMissing(truncate(candidate.getValueText(), 500));
            case STATUS -> restaurant.applyStatusIfUnknown(parseStatus(candidate.getValueText()));
            case MENU, MENU_PRICE -> restaurant.applyMenuIfMissing(
                    candidate.getValueText(),
                    candidate.getValueNumber(),
                    candidate.isRepresentative()
            );
        };
        if (applied) {
            restaurantRepository.save(restaurant);
        }
        candidate.accept();
        candidateRepository.saveAll(java.util.List.of(candidate));
        return new RestaurantEnrichmentApplyItemResult(candidateId, applied);
    }

    private RestaurantStatus parseStatus(String value) {
        try {
            return RestaurantStatus.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return RestaurantStatus.UNKNOWN;
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
