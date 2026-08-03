package com.example.hyunjiinserver.core.restaurant.infrastructure;

import com.example.hyunjiinserver.core.restaurant.application.ExtractedRestaurantCandidate;
import com.example.hyunjiinserver.core.restaurant.application.RestaurantEnrichmentPlan;
import com.example.hyunjiinserver.core.restaurant.application.RestaurantInformationEnrichmentClient;
import com.example.hyunjiinserver.core.restaurant.application.RestaurantInformationEnrichmentResult;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentField;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

@Component
public class PythonRestaurantInformationEnrichmentClient implements RestaurantInformationEnrichmentClient {

    private final RestClient restClient;

    public PythonRestaurantInformationEnrichmentClient(RestaurantEnrichmentProperties properties) {
        this.restClient = RestClient.builder()
                .baseUrl(properties.getCrawlerBaseUrl())
                .build();
    }

    @Override
    public RestaurantInformationEnrichmentResult enrich(RestaurantEnrichmentPlan plan, int maxSources) {
        try {
            JsonNode response = restClient.post()
                    .uri("/internal/v1/restaurants/enrich")
                    .header("Content-Type", "application/json")
                    .body(requestBody(plan, maxSources))
                    .retrieve()
                    .body(JsonNode.class);
            return parse(response, plan);
        } catch (RestClientException exception) {
            throw new RestaurantEnrichmentClientException("Python 로컬 AI 크롤러 호출에 실패했습니다.", exception);
        }
    }

    private Map<String, Object> requestBody(RestaurantEnrichmentPlan plan, int maxSources) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("restaurantName", plan.restaurantName());
        request.put("address", plan.address());
        request.put("latitude", plan.latitude());
        request.put("longitude", plan.longitude());
        request.put("missingFields", plan.missingFields().stream().map(Enum::name).sorted().toList());
        request.put("existingMenuNames", plan.existingMenuNames());
        request.put("maxSources", maxSources);
        return request;
    }

    private RestaurantInformationEnrichmentResult parse(JsonNode response, RestaurantEnrichmentPlan plan) {
        if (response == null || !response.path("candidates").isArray()) {
            throw new RestaurantEnrichmentClientException("Python 로컬 AI 크롤러 응답 형식이 올바르지 않습니다.");
        }

        List<ExtractedRestaurantCandidate> candidates = new ArrayList<>();
        for (JsonNode candidate : response.path("candidates")) {
            RestaurantEnrichmentField field = parseField(candidate.path("field").asString(""));
            String valueText = candidate.path("valueText").asString("").trim();
            String sourceUrl = candidate.path("sourceUrl").asString("").trim();
            Integer valueNumber = candidate.path("valueNumber").isNull()
                    || candidate.path("valueNumber").isMissingNode()
                    ? null
                    : candidate.path("valueNumber").intValue();
            double confidence = candidate.path("confidence").asDouble(-1);
            if (field == null || !plan.missingFields().contains(field) || valueText.isBlank()
                    || !isHttpUrl(sourceUrl) || (valueNumber != null && valueNumber < 0)
                    || confidence < 0 || confidence > 1) {
                continue;
            }
            candidates.add(new ExtractedRestaurantCandidate(
                    field,
                    valueText,
                    valueNumber,
                    candidate.path("representative").asBoolean(false),
                    sourceUrl,
                    candidate.path("evidence").asString(""),
                    confidence
            ));
        }
        return new RestaurantInformationEnrichmentResult(
                candidates,
                Math.max(0, response.path("sourceCount").asInt(0)),
                Math.max(0, response.path("fetchedCount").asInt(0))
        );
    }

    private RestaurantEnrichmentField parseField(String value) {
        try {
            return RestaurantEnrichmentField.valueOf(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private boolean isHttpUrl(String value) {
        try {
            String scheme = URI.create(value).getScheme();
            return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
