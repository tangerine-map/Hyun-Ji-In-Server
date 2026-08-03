package com.example.hyunjiinserver.core.restaurant.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentField;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantRepository;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

class RestaurantEnrichmentAutoApplyServiceTest {

    @Test
    void appliesExtractedValuesInOneRestaurantTransaction() {
        RestaurantRepository restaurantRepository = mock(RestaurantRepository.class);
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
                null,
                OffsetDateTime.parse("2026-08-01T12:00:00+09:00"),
                OffsetDateTime.parse("2026-08-01T12:00:00+09:00")
        );
        when(restaurantRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(restaurant));
        RestaurantEnrichmentAutoApplyService service =
                new RestaurantEnrichmentAutoApplyService(restaurantRepository);

        int appliedFieldCount = service.apply(2L, List.of(
                candidate(RestaurantEnrichmentField.PHONE_NUMBER, "064-782-9006", null, false),
                candidate(RestaurantEnrichmentField.MENU, "구운제주감자빵", 4_000, true),
                candidate(RestaurantEnrichmentField.MENU, "에멘탈토마토빵", 4_500, false)
        ));

        assertEquals(3, appliedFieldCount);
        assertEquals("064-782-9006", restaurant.getPhoneNumber());
        assertEquals(2, restaurant.getMenus().size());
        verify(restaurantRepository).findByIdForUpdate(2L);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void startsANewTransactionForEveryRestaurantApply() throws NoSuchMethodException {
        Method method = RestaurantEnrichmentAutoApplyService.class.getMethod("apply", Long.class, List.class);

        Transactional transactional = method.getAnnotation(Transactional.class);

        assertEquals(Propagation.REQUIRES_NEW, transactional.propagation());
    }

    private ExtractedRestaurantCandidate candidate(
            RestaurantEnrichmentField field,
            String valueText,
            Integer valueNumber,
            boolean representative
    ) {
        return new ExtractedRestaurantCandidate(
                field,
                valueText,
                valueNumber,
                representative,
                "https://www.google.com/maps/place/test",
                valueText,
                0.9
        );
    }
}
