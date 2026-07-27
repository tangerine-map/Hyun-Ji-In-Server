package com.example.hyunjiinserver.core.restaurant.infrastructure;

import com.example.hyunjiinserver.core.restaurant.application.TourApiRestaurantClient;
import com.example.hyunjiinserver.core.restaurant.application.TourApiRestaurantData;
import com.example.hyunjiinserver.core.restaurant.infrastructure.TourApiResponseParser.TourApiCommonDetail;
import com.example.hyunjiinserver.core.restaurant.infrastructure.TourApiResponseParser.TourApiIntroDetail;
import com.example.hyunjiinserver.core.restaurant.infrastructure.TourApiResponseParser.TourApiListItem;
import com.example.hyunjiinserver.core.restaurant.infrastructure.TourApiResponseParser.TourApiPage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@Slf4j
class TourApiRestaurantClientImpl implements TourApiRestaurantClient {

    private static final String JSON_TYPE = "json";

    private final TourApiProperties properties;
    private final TourApiResponseParser responseParser;
    private final TourApiRestaurantMapper restaurantMapper;
    private final RestClient restClient;

    TourApiRestaurantClientImpl(
            TourApiProperties properties,
            TourApiResponseParser responseParser,
            TourApiRestaurantMapper restaurantMapper
    ) {
        this.properties = properties;
        this.responseParser = responseParser;
        this.restaurantMapper = restaurantMapper;
        this.restClient = RestClient.builder().baseUrl(properties.getBaseUrl()).build();
    }

    @Override
    public List<TourApiRestaurantData> fetchJejuRestaurants(String serviceKey, int maxItems) {
        validateConfiguration(serviceKey, maxItems);

        List<TourApiRestaurantData> restaurants = new ArrayList<>();
        int pageNumber = 1;
        int totalCount = Integer.MAX_VALUE;

        while (restaurants.size() < maxItems
                && (pageNumber - 1) * properties.getPageSize() < totalCount) {
            TourApiPage page = fetchList(serviceKey, pageNumber);
            totalCount = page.totalCount();
            if (page.items().isEmpty()) {
                break;
            }

            for (TourApiListItem item : page.items()) {
                if (restaurants.size() >= maxItems) {
                    break;
                }
                if (item.contentId() == null || item.contentId().isBlank()) {
                    log.warn("TourAPI restaurant skipped because contentId is missing.");
                    continue;
                }
                TourApiCommonDetail commonDetail = fetchCommonDetail(serviceKey, item.contentId());
                TourApiIntroDetail introDetail = fetchIntroDetail(serviceKey, item.contentId());
                restaurantMapper.map(item, commonDetail, introDetail)
                        .ifPresentOrElse(
                                restaurants::add,
                                () -> log.warn("TourAPI restaurant skipped because required data is missing. contentId={}", item.contentId())
                        );
            }
            pageNumber++;
        }

        return List.copyOf(restaurants);
    }

    private TourApiPage fetchList(String serviceKey, int pageNumber) {
        return responseParser.parseList(request(
                serviceKey,
                "/areaBasedList2",
                Map.of(
                        "areaCode", String.valueOf(properties.getAreaCode()),
                        "contentTypeId", String.valueOf(properties.getContentTypeId()),
                        "arrange", "O",
                        "numOfRows", String.valueOf(properties.getPageSize()),
                        "pageNo", String.valueOf(pageNumber)
                )
        ));
    }

    private TourApiCommonDetail fetchCommonDetail(String serviceKey, String contentId) {
        return responseParser.parseCommonDetail(request(
                serviceKey,
                "/detailCommon2",
                Map.of("contentId", contentId)
        ));
    }

    private TourApiIntroDetail fetchIntroDetail(String serviceKey, String contentId) {
        return responseParser.parseIntroDetail(request(
                serviceKey,
                "/detailIntro2",
                Map.of(
                        "contentId", contentId,
                        "contentTypeId", String.valueOf(properties.getContentTypeId())
                )
        ));
    }

    private String request(String serviceKey, String path, Map<String, String> operationParameters) {
        MultiValueMap<String, String> queryParameters = new LinkedMultiValueMap<>();
        queryParameters.add("MobileOS", properties.getMobileOs());
        queryParameters.add("MobileApp", properties.getMobileApp());
        queryParameters.add("_type", JSON_TYPE);
        operationParameters.forEach(queryParameters::add);

        try {
            String responseBody = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(path)
                            .queryParam("serviceKey", "{serviceKey}")
                            .queryParams(queryParameters)
                            .build(serviceKey))
                    .retrieve()
                    .body(String.class);
            if (responseBody == null || responseBody.isBlank()) {
                throw new TourApiClientException("TourAPI가 빈 응답을 반환했습니다: " + path);
            }
            return responseBody;
        } catch (RestClientException exception) {
            throw new TourApiClientException("TourAPI 호출에 실패했습니다: " + path, exception);
        }
    }

    private void validateConfiguration(String serviceKey, int maxItems) {
        if (serviceKey == null || serviceKey.isBlank()) {
            throw new TourApiClientException("TourAPI 서비스 키가 요청에 포함되지 않았습니다.");
        }
        if (properties.getPageSize() <= 0 || maxItems <= 0) {
            throw new TourApiClientException("TourAPI page-size와 maxItems는 1 이상이어야 합니다.");
        }
    }
}
