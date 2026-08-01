package com.example.hyunjiinserver.core.restaurant.application;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantImportService {

    private final RestaurantImportTransactionService transactionService;
    private final Clock clock;

    public RestaurantImportResult upsertTourApiRestaurants(List<TourApiRestaurantData> sourceRestaurants) {
        int createdCount = 0;
        int updatedCount = 0;
        int failedCount = 0;
        OffsetDateTime syncedAt = OffsetDateTime.now(clock);

        for (TourApiRestaurantData source : sourceRestaurants) {
            try {
                RestaurantImportItemResult result = transactionService.upsertOne(source, syncedAt);
                if (result == RestaurantImportItemResult.CREATED) {
                    createdCount++;
                } else {
                    updatedCount++;
                }
                log.info(
                        "TourAPI restaurant saved. contentId={}, name={}, result={}",
                        source.contentId(),
                        source.name(),
                        result
                );
            } catch (RuntimeException exception) {
                failedCount++;
                log.error(
                        "TourAPI restaurant save failed. contentId={}, causeType={}",
                        source.contentId(),
                        exception.getClass().getSimpleName(),
                        exception
                );
            }
        }

        return new RestaurantImportResult(
                sourceRestaurants.size(),
                createdCount,
                updatedCount,
                failedCount
        );
    }
}
