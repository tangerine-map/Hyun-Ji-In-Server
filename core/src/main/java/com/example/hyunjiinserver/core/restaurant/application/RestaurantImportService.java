package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantImportService {

    private final RestaurantRepository restaurantRepository;
    private final Clock clock;

    @Transactional
    public RestaurantImportResult upsertTourApiRestaurants(List<TourApiRestaurantData> sourceRestaurants) {
        int createdCount = 0;
        int updatedCount = 0;
        OffsetDateTime syncedAt = OffsetDateTime.now(clock);

        for (TourApiRestaurantData source : sourceRestaurants) {
            Restaurant restaurant = restaurantRepository.findByTourContentId(source.contentId()).orElse(null);
            if (restaurant == null) {
                restaurant = Restaurant.importedFromTourApi(
                        source.contentId(),
                        source.name(),
                        source.category(),
                        source.address(),
                        source.phoneNumber(),
                        source.openingHours(),
                        source.latitude(),
                        source.longitude(),
                        source.summary(),
                        source.representativeMenuName(),
                        source.modifiedAt(),
                        syncedAt
                );
                createdCount++;
            } else {
                restaurant.updateTourApiInformation(
                        source.name(),
                        source.category(),
                        source.address(),
                        source.phoneNumber(),
                        source.openingHours(),
                        source.latitude(),
                        source.longitude(),
                        source.summary(),
                        source.representativeMenuName(),
                        source.modifiedAt(),
                        syncedAt
                );
                updatedCount++;
            }
            restaurantRepository.save(restaurant);
        }

        return new RestaurantImportResult(sourceRestaurants.size(), createdCount, updatedCount);
    }
}
