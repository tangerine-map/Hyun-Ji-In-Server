package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantRepository;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantImportTransactionService {

    private final RestaurantRepository restaurantRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RestaurantImportItemResult upsertOne(TourApiRestaurantData source, OffsetDateTime syncedAt) {
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
            restaurantRepository.save(restaurant);
            return RestaurantImportItemResult.CREATED;
        }

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
        restaurantRepository.save(restaurant);
        return RestaurantImportItemResult.UPDATED;
    }
}
