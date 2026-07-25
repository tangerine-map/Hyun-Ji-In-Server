package com.example.hyunjiinserver.core.restaurant.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.hyunjiinserver.core.restaurant.application.TourApiRestaurantData;
import com.example.hyunjiinserver.core.restaurant.infrastructure.TourApiResponseParser.TourApiCommonDetail;
import com.example.hyunjiinserver.core.restaurant.infrastructure.TourApiResponseParser.TourApiIntroDetail;
import com.example.hyunjiinserver.core.restaurant.infrastructure.TourApiResponseParser.TourApiListItem;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class TourApiRestaurantMapperTest {

    private final TourApiRestaurantMapper mapper = new TourApiRestaurantMapper();

    @Test
    void combinesListCommonAndIntroData() {
        TourApiListItem listItem = new TourApiListItem(
                "3012345",
                "목록 이름",
                "제주특별자치도 제주시",
                null,
                null,
                "126.5312",
                "33.4996",
                "A05020100",
                "20260724120000"
        );
        TourApiCommonDetail commonDetail = new TourApiCommonDetail(
                "제주&amp;식당",
                "제주특별자치도 제주시",
                "테스트로 1",
                "064-123-4567",
                "126.5313",
                "33.4997",
                "A05020900",
                "<p>제주 현지 식재료를 사용하는<br>식당입니다.</p>",
                "20260724130000"
        );
        TourApiIntroDetail introDetail = new TourApiIntroDetail(
                "매일 09:00~18:00",
                "고기국수",
                "고기국수, 비빔국수",
                "매주 월요일",
                "가능",
                "전화"
        );

        TourApiRestaurantData result = mapper.map(listItem, commonDetail, introDetail).orElseThrow();

        assertEquals("제주&식당", result.name());
        assertEquals("카페", result.category());
        assertEquals("제주특별자치도 제주시 테스트로 1", result.address());
        assertEquals(33.4997, result.latitude());
        assertEquals(126.5313, result.longitude());
        assertEquals("매일 09:00~18:00 / 휴무일: 매주 월요일", result.openingHours());
        assertEquals("제주 현지 식재료를 사용하는 식당입니다.", result.summary());
        assertEquals("고기국수", result.representativeMenuName());
        assertEquals(OffsetDateTime.parse("2026-07-24T13:00:00+09:00"), result.modifiedAt());
    }

    @Test
    void skipsRestaurantWithoutCoordinates() {
        TourApiListItem listItem = new TourApiListItem(
                "3012345", "제주식당", "제주특별자치도 제주시", null, null,
                null, null, "A05020100", null
        );

        assertTrue(mapper.map(
                listItem,
                new TourApiCommonDetail(null, null, null, null, null, null, null, null, null),
                new TourApiIntroDetail(null, null, null, null, null, null)
        ).isEmpty());
    }

    @Test
    void usesGenericFoodCategoryWhenCategoryCodeIsMissing() {
        TourApiListItem listItem = new TourApiListItem(
                "3012345", "제주식당", "제주특별자치도 제주시", null, null,
                "126.5312", "33.4996", null, null
        );

        TourApiRestaurantData result = mapper.map(
                listItem,
                new TourApiCommonDetail(null, null, null, null, null, null, null, null, null),
                new TourApiIntroDetail(null, null, null, null, null, null)
        ).orElseThrow();

        assertEquals("음식점", result.category());
    }
}
