package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.domain.LocalComment;
import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantMenu;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantQueryService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantMapResult findRestaurants(FindRestaurantsQuery query) {
        return new RestaurantMapResult(
                restaurantRepository.findByMapBounds(query.toCondition())
                        .stream()
                        .map(restaurant -> toSummaryResult(restaurant, query))
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
                false,
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

    private RestaurantSummaryResult toSummaryResult(Restaurant restaurant, FindRestaurantsQuery query) {
        RestaurantMenu representativeMenu = restaurant.representativeMenu().orElse(null);

        return new RestaurantSummaryResult(
                restaurant.getId(),
                restaurant.getName(),
                representativeMenu == null ? null : representativeMenu.getName(),
                representativeMenu == null ? null : representativeMenu.getPrice(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                distanceMeters(query.centerLatitude(), query.centerLongitude(), restaurant),
                restaurant.getPriceAdequacyLabel(),
                restaurant.isLocalRecommended(),
                false
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

    private Integer distanceMeters(double userLatitude, double userLongitude, Restaurant restaurant) {
        double earthRadiusMeters = 6_371_000;
        double userLatRad = Math.toRadians(userLatitude);
        double restaurantLatRad = Math.toRadians(restaurant.getLatitude());
        double deltaLatRad = Math.toRadians(restaurant.getLatitude() - userLatitude);
        double deltaLngRad = Math.toRadians(restaurant.getLongitude() - userLongitude);

        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2)
                + Math.cos(userLatRad) * Math.cos(restaurantLatRad)
                * Math.sin(deltaLngRad / 2) * Math.sin(deltaLngRad / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) Math.round(earthRadiusMeters * c);
    }
}
