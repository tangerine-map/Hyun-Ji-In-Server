package com.example.hyunjiinserver.core.restaurant.infrastructure;

import com.example.hyunjiinserver.core.restaurant.application.TourApiRestaurantData;
import com.example.hyunjiinserver.core.restaurant.infrastructure.TourApiResponseParser.TourApiCommonDetail;
import com.example.hyunjiinserver.core.restaurant.infrastructure.TourApiResponseParser.TourApiIntroDetail;
import com.example.hyunjiinserver.core.restaurant.infrastructure.TourApiResponseParser.TourApiListItem;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Component
class TourApiRestaurantMapper {

    private static final ZoneOffset KOREA_OFFSET = ZoneOffset.ofHours(9);
    private static final DateTimeFormatter TOUR_API_DATE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Pattern HTML_BREAK = Pattern.compile("(?i)<br\\s*/?>|</p>|</li>");
    private static final Pattern HTML_TAG = Pattern.compile("<[^>]+>");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Map<String, String> FOOD_CATEGORIES = Map.of(
            "A05020100", "한식",
            "A05020200", "서양식",
            "A05020300", "일식",
            "A05020400", "중식",
            "A05020700", "이색음식점",
            "A05020900", "카페"
    );

    Optional<TourApiRestaurantData> map(
            TourApiListItem listItem,
            TourApiCommonDetail commonDetail,
            TourApiIntroDetail introDetail
    ) {
        String contentId = firstNonBlank(listItem.contentId());
        String name = limit(clean(firstNonBlank(commonDetail.title(), listItem.title())), 100);
        String address = limit(clean(joinAddress(
                firstNonBlank(commonDetail.address1(), listItem.address1()),
                firstNonBlank(commonDetail.address2(), listItem.address2())
        )), 255);
        Double longitude = number(firstNonBlank(commonDetail.mapX(), listItem.mapX()));
        Double latitude = number(firstNonBlank(commonDetail.mapY(), listItem.mapY()));

        if (contentId == null || name == null || address == null || !validCoordinates(latitude, longitude)) {
            return Optional.empty();
        }

        String categoryCode = firstNonBlank(commonDetail.category3(), listItem.category3());
        String category = categoryCode == null ? "음식점" : FOOD_CATEGORIES.getOrDefault(categoryCode, "음식점");
        return Optional.of(new TourApiRestaurantData(
                contentId,
                name,
                category,
                address,
                limit(clean(firstNonBlank(commonDetail.telephone(), listItem.telephone())), 30),
                limit(openingHours(introDetail.openingHours(), introDetail.restDate()), 100),
                latitude,
                longitude,
                limit(clean(commonDetail.overview()), 500),
                limit(clean(introDetail.firstMenu()), 100),
                parseModifiedAt(firstNonBlank(commonDetail.modifiedTime(), listItem.modifiedTime()))
        ));
    }

    private boolean validCoordinates(Double latitude, Double longitude) {
        return latitude != null
                && longitude != null
                && latitude >= -90
                && latitude <= 90
                && longitude >= -180
                && longitude <= 180
                && !(latitude == 0 && longitude == 0);
    }

    private Double number(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private OffsetDateTime parseModifiedAt(String value) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, TOUR_API_DATE_TIME).atOffset(KOREA_OFFSET);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private String joinAddress(String address1, String address2) {
        if (address1 == null) {
            return address2;
        }
        if (address2 == null) {
            return address1;
        }
        return address1 + " " + address2;
    }

    private String openingHours(String openingHours, String restDate) {
        String cleanedOpeningHours = clean(openingHours);
        String cleanedRestDate = clean(restDate);
        if (cleanedOpeningHours == null) {
            return cleanedRestDate == null ? null : "휴무일: " + cleanedRestDate;
        }
        if (cleanedRestDate == null) {
            return cleanedOpeningHours;
        }
        return cleanedOpeningHours + " / 휴무일: " + cleanedRestDate;
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String withBreaks = HTML_BREAK.matcher(value).replaceAll(" ");
        String withoutTags = HTML_TAG.matcher(withBreaks).replaceAll(" ");
        String normalized = WHITESPACE.matcher(HtmlUtils.htmlUnescape(withoutTags)).replaceAll(" ").trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String limit(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
