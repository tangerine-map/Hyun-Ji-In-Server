package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantRepository;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantEnrichmentAutoApplyService {

    private final RestaurantRepository restaurantRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int apply(Long restaurantId, List<ExtractedRestaurantCandidate> extracted) {
        Restaurant restaurant = restaurantRepository.findByIdForUpdate(restaurantId)
                .orElseThrow(() -> new BusinessException(RestaurantErrorCode.RESTAURANT_NOT_FOUND));

        boolean menusWereMissing = restaurant.hasNoMenus();
        int appliedFieldCount = 0;
        for (ExtractedRestaurantCandidate item : extracted) {
            boolean applied = switch (item.field()) {
                case PHONE_NUMBER -> restaurant.applyPhoneNumberIfMissing(truncate(item.valueText(), 30));
                case OPENING_HOURS -> restaurant.applyOpeningHoursIfMissing(truncate(item.valueText(), 100));
                case SUMMARY -> restaurant.applySummaryIfMissing(truncate(item.valueText(), 500));
                case STATUS -> restaurant.applyStatusIfUnknown(parseStatus(item.valueText()));
                case MENU -> menusWereMissing && restaurant.applyDiscoveredMenu(
                        item.valueText(),
                        item.valueNumber(),
                        item.representative()
                );
                case MENU_PRICE -> restaurant.applyMenuPriceIfMissing(
                        item.valueText(),
                        item.valueNumber()
                );
            };
            if (applied) {
                appliedFieldCount++;
            }
        }
        if (appliedFieldCount > 0) {
            restaurantRepository.save(restaurant);
        }
        log.info("Restaurant enrichment applied. restaurantId={}, name={}, appliedFieldCount={}",
                restaurantId, restaurant.getName(), appliedFieldCount);
        return appliedFieldCount;
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
