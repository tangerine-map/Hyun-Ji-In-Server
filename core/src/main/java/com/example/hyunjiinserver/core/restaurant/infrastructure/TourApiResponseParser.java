package com.example.hyunjiinserver.core.restaurant.infrastructure;

import java.util.List;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Component
class TourApiResponseParser {

    private static final String SUCCESS_CODE = "0000";

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    TourApiPage parseList(String responseBody) {
        JsonNode body = responseBody(responseBody);
        return new TourApiPage(
                body.path("totalCount").asInt(0),
                itemNodes(body).stream().map(this::toListItem).toList()
        );
    }

    TourApiCommonDetail parseCommonDetail(String responseBody) {
        return firstItem(responseBody)
                .map(item -> new TourApiCommonDetail(
                        text(item, "title"),
                        text(item, "addr1"),
                        text(item, "addr2"),
                        text(item, "tel"),
                        text(item, "mapx"),
                        text(item, "mapy"),
                        text(item, "cat3"),
                        text(item, "overview"),
                        text(item, "modifiedtime")
                ))
                .orElse(TourApiCommonDetail.EMPTY);
    }

    TourApiIntroDetail parseIntroDetail(String responseBody) {
        return firstItem(responseBody)
                .map(item -> new TourApiIntroDetail(
                        text(item, "opentimefood"),
                        text(item, "firstmenu"),
                        text(item, "treatmenu"),
                        text(item, "restdatefood"),
                        text(item, "parkingfood"),
                        text(item, "reservationfood")
                ))
                .orElse(TourApiIntroDetail.EMPTY);
    }

    private TourApiListItem toListItem(JsonNode item) {
        return new TourApiListItem(
                text(item, "contentid"),
                text(item, "title"),
                text(item, "addr1"),
                text(item, "addr2"),
                text(item, "tel"),
                text(item, "mapx"),
                text(item, "mapy"),
                text(item, "cat3"),
                text(item, "modifiedtime")
        );
    }

    private java.util.Optional<JsonNode> firstItem(String responseBody) {
        return itemNodes(responseBody(responseBody)).stream().findFirst();
    }

    private JsonNode responseBody(String responseBody) {
        try {
            JsonNode root = jsonMapper.readTree(responseBody);
            if (root == null || !root.isObject()) {
                throw new TourApiClientException("TourAPI JSON 응답의 최상위 객체가 올바르지 않습니다.");
            }
            JsonNode response = root.path("response");
            JsonNode header = response.path("header");
            String resultCode = text(header, "resultCode");
            if (!SUCCESS_CODE.equals(resultCode)) {
                throw new TourApiClientException(
                        "TourAPI 응답 오류: " + resultCode + " " + text(header, "resultMsg")
                );
            }
            return response.path("body");
        } catch (JacksonException exception) {
            throw new TourApiClientException("TourAPI JSON 응답을 해석할 수 없습니다.", exception);
        }
    }

    private List<JsonNode> itemNodes(JsonNode body) {
        JsonNode items = body.path("items");
        if (!items.isObject()) {
            return List.of();
        }

        JsonNode item = items.path("item");
        if (item.isArray()) {
            return item.valueStream().toList();
        }
        if (item.isObject()) {
            return List.of(item);
        }
        return List.of();
    }

    private String text(JsonNode node, String fieldName) {
        String value = node.path(fieldName).asString("").trim();
        return value.isEmpty() ? null : value;
    }

    record TourApiPage(int totalCount, List<TourApiListItem> items) {
    }

    record TourApiListItem(
            String contentId,
            String title,
            String address1,
            String address2,
            String telephone,
            String mapX,
            String mapY,
            String category3,
            String modifiedTime
    ) {
    }

    record TourApiCommonDetail(
            String title,
            String address1,
            String address2,
            String telephone,
            String mapX,
            String mapY,
            String category3,
            String overview,
            String modifiedTime
    ) {
        private static final TourApiCommonDetail EMPTY = new TourApiCommonDetail(
                null, null, null, null, null, null, null, null, null
        );
    }

    record TourApiIntroDetail(
            String openingHours,
            String firstMenu,
            String treatMenu,
            String restDate,
            String parking,
            String reservation
    ) {
        private static final TourApiIntroDetail EMPTY = new TourApiIntroDetail(
                null, null, null, null, null, null
        );
    }
}
