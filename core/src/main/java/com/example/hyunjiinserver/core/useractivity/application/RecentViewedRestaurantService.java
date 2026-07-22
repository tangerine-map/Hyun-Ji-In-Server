package com.example.hyunjiinserver.core.useractivity.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.global.geo.GeoDistance;
import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantMenu;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantRepository;
import com.example.hyunjiinserver.core.useractivity.domain.RecentViewedRestaurant;
import com.example.hyunjiinserver.core.useractivity.domain.RecentViewedRestaurantRepository;
import com.example.hyunjiinserver.core.useractivity.domain.SavedRestaurantRepository;
import com.example.hyunjiinserver.core.useractivity.domain.UserActivityErrorCode;
import java.util.List;
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
public class RecentViewedRestaurantService {

    private static final int MAX_RECENT_VIEWS = 50;

    private final RecentViewedRestaurantRepository recentViewedRestaurantRepository;
    private final SavedRestaurantRepository savedRestaurantRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void recordView(String deviceId, Long restaurantId) {
        recentViewedRestaurantRepository.findByDeviceIdAndRestaurantId(deviceId, restaurantId)
                .ifPresentOrElse(
                        RecentViewedRestaurant::refreshViewedAt,
                        () -> {
                            recentViewedRestaurantRepository.save(new RecentViewedRestaurant(deviceId, restaurantId));
                            pruneOldViews(deviceId);
                        }
                );
    }

    public RecentRestaurantsResult getRecentRestaurants(GetRecentRestaurantsQuery query) {
        List<RecentViewedRestaurant> recentViews = recentViewedRestaurantRepository.findAllByDeviceId(query.deviceId());

        List<Long> restaurantIds = recentViews.stream()
                .map(RecentViewedRestaurant::getRestaurantId)
                .toList();

        Map<Long, Restaurant> restaurantsById = restaurantRepository.findByIds(restaurantIds)
                .stream()
                .collect(Collectors.toMap(Restaurant::getId, Function.identity()));

        Set<Long> savedRestaurantIds = restaurantIds.isEmpty()
                ? Set.of()
                : savedRestaurantRepository.findSavedRestaurantIds(query.deviceId(), restaurantIds);

        return new RecentRestaurantsResult(
                recentViews.stream()
                        .map(recentView -> toResult(
                                recentView,
                                restaurantsById.get(recentView.getRestaurantId()),
                                savedRestaurantIds.contains(recentView.getRestaurantId()),
                                query
                        ))
                        .filter(Objects::nonNull)
                        .toList()
        );
    }

    @Transactional
    public void deleteRecentView(String deviceId, Long restaurantId) {
        RecentViewedRestaurant recentView = recentViewedRestaurantRepository
                .findByDeviceIdAndRestaurantId(deviceId, restaurantId)
                .orElseThrow(() -> new BusinessException(UserActivityErrorCode.RECENT_VIEW_NOT_FOUND));

        recentViewedRestaurantRepository.delete(recentView);
    }

    @Transactional
    public void clearRecentViews(String deviceId) {
        recentViewedRestaurantRepository.deleteAllByDeviceId(deviceId);
    }

    private void pruneOldViews(String deviceId) {
        List<RecentViewedRestaurant> views = recentViewedRestaurantRepository.findAllByDeviceId(deviceId);
        if (views.size() > MAX_RECENT_VIEWS) {
            recentViewedRestaurantRepository.deleteAll(views.subList(MAX_RECENT_VIEWS, views.size()));
        }
    }

    private RecentRestaurantResult toResult(
            RecentViewedRestaurant recentView,
            Restaurant restaurant,
            boolean saved,
            GetRecentRestaurantsQuery query
    ) {
        if (restaurant == null) {
            return null;
        }

        RestaurantMenu representativeMenu = restaurant.representativeMenu().orElse(null);

        return new RecentRestaurantResult(
                restaurant.getId(),
                restaurant.getName(),
                representativeMenu == null ? null : representativeMenu.getName(),
                representativeMenu == null ? null : representativeMenu.getPrice(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                distanceMeters(query, restaurant),
                restaurant.getPriceAdequacyLabel(),
                restaurant.isLocalRecommended(),
                saved,
                recentView.getViewedAt()
        );
    }

    private Integer distanceMeters(GetRecentRestaurantsQuery query, Restaurant restaurant) {
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
