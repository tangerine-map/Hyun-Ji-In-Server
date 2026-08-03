package com.example.hyunjiinserver.core.restaurant.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantEnrichmentPlan;
import com.example.hyunjiinserver.core.restaurant.application.RestaurantInformationEnrichmentResult;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantEnrichmentField;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class PythonRestaurantInformationEnrichmentClientTest {

    @Test
    void sendsRestaurantPlanAndParsesLocalAiCandidates() throws IOException {
        AtomicReference<String> requestBody = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/internal/v1/restaurants/enrich", exchange -> {
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            respond(exchange, """
                    {
                      "restaurantName": "가는곶 세화",
                      "sourceCount": 2,
                      "fetchedCount": 2,
                      "candidates": [
                        {
                          "field": "PHONE_NUMBER",
                          "valueText": "064-782-9006",
                          "valueNumber": null,
                          "representative": false,
                          "sourceUrl": "https://www.google.com/maps/place/test",
                          "evidence": "전화번호 064-782-9006",
                          "confidence": 0.98
                        },
                        {
                          "field": "MENU",
                          "valueText": "구운제주감자빵",
                          "valueNumber": 4000,
                          "representative": true,
                          "sourceUrl": "https://example.com/menu",
                          "evidence": "구운제주감자빵 4,000원",
                          "confidence": 0.95
                        }
                      ]
                    }
                    """);
        });
        server.start();

        try {
            RestaurantEnrichmentProperties properties = new RestaurantEnrichmentProperties();
            properties.setCrawlerBaseUrl("http://127.0.0.1:" + server.getAddress().getPort());
            PythonRestaurantInformationEnrichmentClient client =
                    new PythonRestaurantInformationEnrichmentClient(properties);
            RestaurantEnrichmentPlan plan = new RestaurantEnrichmentPlan(
                    2L,
                    "가는곶 세화",
                    "제주특별자치도 제주시 구좌읍 세화14길 3",
                    33.5205,
                    126.8606,
                    Set.of(RestaurantEnrichmentField.PHONE_NUMBER, RestaurantEnrichmentField.MENU),
                    List.of()
            );

            RestaurantInformationEnrichmentResult result = client.enrich(plan, 5);

            assertTrue(requestBody.get().contains("\"restaurantName\":\"가는곶 세화\""));
            assertTrue(requestBody.get().contains("\"maxSources\":5"));
            assertTrue(requestBody.get().contains("\"existingMenuNames\":[]"));
            assertEquals(2, result.sourceCount());
            assertEquals(2, result.fetchedCount());
            assertEquals("064-782-9006", result.candidates().get(0).valueText());
            assertEquals(4000, result.candidates().get(1).valueNumber());
        } finally {
            server.stop(0);
        }
    }

    private void respond(HttpExchange exchange, String json) throws IOException {
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
}
