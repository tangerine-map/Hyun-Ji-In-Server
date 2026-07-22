package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.global.geo.GeoDistance;
import com.example.hyunjiinserver.core.restaurant.domain.LocalComment;
import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantMenu;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantRepository;
import com.example.hyunjiinserver.core.useractivity.domain.SavedRestaurantRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantQueryService {

    private final RestaurantRepository restaurantRepository;
    private final SavedRestaurantRepository savedRestaurantRepository;

    public RestaurantMapResult findRestaurants(FindRestaurantsQuery query) {
        List<Restaurant> restaurants = restaurantRepository.findByMapBounds(query.toCondition());
        Set<Long> savedRestaurantIds = savedRestaurantIds(query.deviceId(), restaurants);

        return new RestaurantMapResult(
                restaurants.stream()
                        .map(restaurant -> toSummaryResult(
                                restaurant,
                                query,
                                savedRestaurantIds.contains(restaurant.getId())
                        ))
                        .toList()
        );
    }

    public RestaurantDetailResult getRestaurantDetail(GetRestaurantDetailQuery query) {
        Restaurant restaurant = restaurantRepository.findById(query.restaurantId())
                .orElseThrow(() -> new BusinessException(RestaurantErrorCode.RESTAURANT_NOT_FOUND));

        return new RestaurantDetailResult(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCategory(),
                restaurant.getAddress(),
                restaurant.getPhoneNumber(),
                restaurant.getOpeningHours(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getSummary(),
                restaurant.isLocalRecommended(),
                restaurant.getLocalRecommendationReason(),
                restaurant.getPriceAdequacyLabel(),
                restaurant.getPriceAdequacyDescription(),
                isSaved(query.deviceId(), restaurant.getId()),
                restaurant.getMenus()
                        .stream()
                        .filter(RestaurantMenu::isRepresentative)
                        .map(this::toMenuResult)
                        .toList()
        );
    }

    public RestaurantCommentsResult getRestaurantComments(GetRestaurantCommentsQuery query) {
        return new RestaurantCommentsResult(
                restaurantRepository.findComments(query.toCondition())
                        .stream()
                        .map(this::toCommentResult)
                        .toList()
        );
    }

    private Set<Long> savedRestaurantIds(String deviceId, List<Restaurant> restaurants) {
        if (deviceId == null || deviceId.isBlank() || restaurants.isEmpty()) {
            return Set.of();
        }
        return savedRestaurantRepository.findSavedRestaurantIds(
                deviceId,
                restaurants.stream().map(Restaurant::getId).toList()
        );
    }

    private boolean isSaved(String deviceId, Long restaurantId) {
        if (deviceId == null || deviceId.isBlank()) {
            return false;
        }
        return savedRestaurantRepository.findByDeviceIdAndRestaurantId(deviceId, restaurantId).isPresent();
    }

    private RestaurantSummaryResult toSummaryResult(Restaurant restaurant, FindRestaurantsQuery query, boolean saved) {
        RestaurantMenu representativeMenu = restaurant.representativeMenu().orElse(null);

        return new RestaurantSummaryResult(
                restaurant.getId(),
                restaurant.getName(),
                representativeMenu == null ? null : representativeMenu.getName(),
                representativeMenu == null ? null : representativeMenu.getPrice(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                GeoDistance.distanceMeters(
                        query.centerLatitude(),
                        query.centerLongitude(),
                        restaurant.getLatitude(),
                        restaurant.getLongitude()
                ),
                restaurant.getPriceAdequacyLabel(),
                restaurant.isLocalRecommended(),
                saved
        );
    }

    private RestaurantMenuResult toMenuResult(RestaurantMenu menu) {
        return new RestaurantMenuResult(
                menu.getId(),
                menu.getName(),
                menu.getPrice(),
                menu.isRepresentative()
        );
    }

    private RestaurantCommentResult toCommentResult(LocalComment comment) {
        return new RestaurantCommentResult(
                comment.getId(),
                comment.getAuthorContext(),
                comment.getContent(),
                comment.getHelpfulCount(),
                comment.getCreatedAt()
        );
    }
}
