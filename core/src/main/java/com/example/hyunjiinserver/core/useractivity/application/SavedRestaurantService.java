package com.example.hyunjiinserver.core.useractivity.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.global.geo.GeoDistance;
import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantMenu;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantRepository;
import com.example.hyunjiinserver.core.useractivity.domain.SavedRestaurant;
import com.example.hyunjiinserver.core.useractivity.domain.SavedRestaurantRepository;
import com.example.hyunjiinserver.core.useractivity.domain.SavedRestaurantSort;
import com.example.hyunjiinserver.core.useractivity.domain.UserActivityErrorCode;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SavedRestaurantService {

    private static final Set<String> PRICE_ADEQUATE_LABELS = Set.of("평균보다 저렴", "가격 적정");

    private final SavedRestaurantRepository savedRestaurantRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public ToggleSavedRestaurantResult toggleSavedRestaurant(ToggleSavedRestaurantCommand command) {
        restaurantRepository.findById(command.restaurantId())
                .orElseThrow(() -> new BusinessException(RestaurantErrorCode.RESTAURANT_NOT_FOUND));

        return savedRestaurantRepository.findByDeviceIdAndRestaurantId(command.deviceId(), command.restaurantId())
                .map(saved -> {
                    savedRestaurantRepository.delete(saved);
                    return new ToggleSavedRestaurantResult(command.restaurantId(), false);
                })
                .orElseGet(() -> {
                    savedRestaurantRepository.save(new SavedRestaurant(command.deviceId(), command.restaurantId()));
                    return new ToggleSavedRestaurantResult(command.restaurantId(), true);
                });
    }

    public SavedRestaurantsResult getSavedRestaurants(GetSavedRestaurantsQuery query) {
        SavedRestaurantSort sort = query.sortType();
        if (sort == SavedRestaurantSort.DISTANCE && (query.latitude() == null || query.longitude() == null)) {
            throw new BusinessException(UserActivityErrorCode.LOCATION_REQUIRED_FOR_DISTANCE_SORT);
        }

        var savedRestaurants = savedRestaurantRepository.findAllByDeviceId(query.deviceId());

        Map<Long, Restaurant> restaurantsById = restaurantRepository.findByIds(
                        savedRestaurants.stream().map(SavedRestaurant::getRestaurantId).toList()
                )
                .stream()
                .collect(Collectors.toMap(Restaurant::getId, Function.identity()));

        return new SavedRestaurantsResult(
                savedRestaurants.stream()
                        .map(saved -> toResult(saved, restaurantsById.get(saved.getRestaurantId()), query))
                        .filter(Objects::nonNull)
                        .filter(result -> matchesFilters(result, query))
                        .sorted(comparator(sort))
                        .toList()
        );
    }

    private SavedRestaurantResult toResult(SavedRestaurant saved, Restaurant restaurant, GetSavedRestaurantsQuery query) {
        if (restaurant == null) {
            return null;
        }

        RestaurantMenu representativeMenu = restaurant.representativeMenu().orElse(null);

        return new SavedRestaurantResult(
                restaurant.getId(),
                restaurant.getName(),
                representativeMenu == null ? null : representativeMenu.getName(),
                representativeMenu == null ? null : representativeMenu.getPrice(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                distanceMeters(query, restaurant),
                restaurant.getPriceAdequacyLabel(),
                restaurant.isLocalRecommended(),
                saved.getSavedAt()
        );
    }

    private boolean matchesFilters(SavedRestaurantResult result, GetSavedRestaurantsQuery query) {
        if (query.localRecommendedOnly() && !result.localRecommended()) {
            return false;
        }
        if (query.priceAdequateOnly() && !PRICE_ADEQUATE_LABELS.contains(result.priceAdequacyLabel())) {
            return false;
        }
        return true;
    }

    private Comparator<SavedRestaurantResult> comparator(SavedRestaurantSort sort) {
        if (sort == SavedRestaurantSort.DISTANCE) {
            return Comparator.comparing(
                    SavedRestaurantResult::distanceMeters,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
        }
        return Comparator.comparing(SavedRestaurantResult::savedAt).reversed();
    }

    private Integer distanceMeters(GetSavedRestaurantsQuery query, Restaurant restaurant) {
        if (query.latitude() == null || query.longitude() == null) {
            return null;
        }
        return GeoDistance.distanceMeters(
                query.latitude(),
                query.longitude(),
                restaurant.getLatitude(),
                restaurant.getLongitude()
        );
    }
}
