package com.example.hyunjiinserver.core.restaurant.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TourApiResponseParserTest {

    private final TourApiResponseParser parser = new TourApiResponseParser();

    @Test
    void parsesRestaurantListResponse() {
        String response = """
                {
                  "response": {
                    "header": {"resultCode": "0000", "resultMsg": "OK"},
                    "body": {
                      "totalCount": 1,
                      "items": {
                        "item": [{
                          "contentid": "3012345",
                          "title": "제주식당",
                          "addr1": "제주특별자치도 제주시",
                          "addr2": "테스트로 1",
                          "tel": "064-123-4567",
                          "mapx": "126.5312",
                          "mapy": "33.4996",
                          "cat3": "A05020100",
                          "modifiedtime": "20260724120000"
                        }]
                      }
                    }
                  }
                }
                """;

        TourApiResponseParser.TourApiPage page = parser.parseList(response);

        assertEquals(1, page.totalCount());
        assertEquals(1, page.items().size());
        assertEquals("3012345", page.items().getFirst().contentId());
        assertEquals("제주식당", page.items().getFirst().title());
    }

    @Test
    void acceptsSingleObjectItemAndEmptyItems() {
        String singleItemResponse = """
                {
                  "response": {
                    "header": {"resultCode": "0000", "resultMsg": "OK"},
                    "body": {"totalCount": 1, "items": {"item": {"contentid": "1"}}}
                  }
                }
                """;
        String emptyResponse = """
                {
                  "response": {
                    "header": {"resultCode": "0000", "resultMsg": "OK"},
                    "body": {"totalCount": 0, "items": ""}
                  }
                }
                """;

        assertEquals(1, parser.parseList(singleItemResponse).items().size());
        assertEquals(0, parser.parseList(emptyResponse).items().size());
    }

    @Test
    void rejectsTourApiErrorResponse() {
        String response = """
                {
                  "response": {
                    "header": {"resultCode": "30", "resultMsg": "SERVICE KEY IS NOT REGISTERED"},
                    "body": {}
                  }
                }
                """;

        assertThrows(TourApiClientException.class, () -> parser.parseList(response));
    }
}
