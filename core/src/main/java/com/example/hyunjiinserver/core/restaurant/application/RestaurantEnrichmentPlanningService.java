package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentField;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantMenu;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantRepository;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantEnrichmentPlanningService {

    private final RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public RestaurantEnrichmentPlan plan(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new BusinessException(RestaurantErrorCode.RESTAURANT_NOT_FOUND));

        Set<RestaurantEnrichmentField> missingFields = EnumSet.noneOf(RestaurantEnrichmentField.class);
        if (restaurant.hasMissingPhoneNumber()) {
            missingFields.add(RestaurantEnrichmentField.PHONE_NUMBER);
        }
        if (restaurant.hasMissingOpeningHours()) {
            missingFields.add(RestaurantEnrichmentField.OPENING_HOURS);
        }
        if (restaurant.hasMissingSummary()) {
            missingFields.add(RestaurantEnrichmentField.SUMMARY);
        }
        if (restaurant.hasUnknownStatus()) {
            missingFields.add(RestaurantEnrichmentField.STATUS);
        }
        if (restaurant.hasNoMenus()) {
            missingFields.add(RestaurantEnrichmentField.MENU);
        } else if (restaurant.hasUnpricedMenus()) {
            missingFields.add(RestaurantEnrichmentField.MENU_PRICE);
        }

        List<String> existingMenuNames = restaurant.getMenus().stream()
                .map(RestaurantMenu::getName)
                .toList();
        return new RestaurantEnrichmentPlan(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                Set.copyOf(missingFields),
                existingMenuNames
        );
    }
}
