package com.example.hyunjiinserver.core.restaurant.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class RestaurantEnrichmentTest {

    @Test
    void appliesOnlyMissingRestaurantInformation() {
        OffsetDateTime now = OffsetDateTime.parse("2026-08-01T12:00:00+09:00");
        Restaurant restaurant = Restaurant.importedFromTourApi(
                "100",
                "가는곶 세화",
                "카페",
                "제주특별자치도 제주시 구좌읍 세화14길 3",
                null,
                "-",
                33.52,
                126.86,
                null,
                "구운제주감자빵",
                now,
                now
        );

        assertTrue(restaurant.hasMissingPhoneNumber());
        assertTrue(restaurant.hasMissingOpeningHours());
        assertTrue(restaurant.hasMissingSummary());
        assertTrue(restaurant.hasUnknownStatus());
        assertTrue(restaurant.hasUnpricedMenus());

        assertTrue(restaurant.applyPhoneNumberIfMissing("064-123-4567"));
        assertFalse(restaurant.applyPhoneNumberIfMissing("064-999-9999"));
        assertTrue(restaurant.applyOpeningHoursIfMissing("매일 09:00~21:00"));
        assertTrue(restaurant.applySummaryIfMissing("제주 농산물로 빵을 만드는 베이커리"));
        assertTrue(restaurant.applyStatusIfUnknown(RestaurantStatus.OPEN));
        assertTrue(restaurant.applyMenuPriceIfMissing("구운 제주 감자빵", 4_500));

        assertEquals("064-123-4567", restaurant.getPhoneNumber());
        assertEquals(RestaurantStatus.OPEN, restaurant.getStatus());
        assertEquals(4_500, restaurant.getMenus().getFirst().getPrice());
    }
}
